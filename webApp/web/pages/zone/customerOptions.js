
function openOrder() {
    const win = document.getElementById("order-container");
    !win.classList.contains("show") && win.classList.add("show");
    $("#order-container div:first-child").css("display","block");
    $("#orderForm").trigger('reset');
    $("#storeProductSelect tbody").empty();
    $("#storeSelect tbody").empty();
    $("#staticOrder-container").css("display", "none");
    $("#dynamicOrder-container").css("display", "none");
    initOrder();
}
function initOrder() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "initOrder",
        },
    })
}


function addToCart(productId,amount) {
    $.ajax({
        url:ORDER_URL,
        data: {
            action: "addToCart",
            zoneName:zoneName,
            owner:ownerId,
            store:order.storeId,
            product:productId,
            amount:amount,
        },
        success: function(response) {
            alert(response);
        },
        error:error=>{
            alert(error.responseText);
        }
    });
}

function chooseAmount(productId) {
    var amount = prompt("Please enter amount to buy:");
    if (!(amount == null)) {
        addToCart(productId,amount);
    }
}

function getStoreProducts(storeId) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getStoreProducts",
            zoneName:zoneName,
            owner:ownerId,
            store:storeId,
        },
        success: function(json) {
            json.storeProducts.forEach(product => delete product.sold);
            showProducts(json.storeProducts,'#storeProductSelect tbody');
            $("#storeProductSelect tr").click((event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML));
        }
    });
}

function selectStore(event) {
    $('#storeSelect tr').removeClass("selected");
    const storeNode = event.target.closest("tr");
    storeNode.classList.add("selected");
    order.storeId =storeNode.children[0].innerHTML;
    getStoreProducts(order.storeId);

}

function staticOrder() {
    initOrder();
    order.type="static";
    $('#staticOrder-container').css("display","block");
    $('#dynamicOrder-container').css("display","none");
    const table = $('#storeSelect tbody');
    table.empty();
    storesJson.forEach(store => {
        var tr = $(document.createElement('tr'));
        tr.append($(document.createElement('td')).text(store["Serial Number"]));
        tr.append($(document.createElement('td')).text(store["Name"]));
        tr.append($(document.createElement('td')).text(store["Location"]));
        tr.appendTo(table);
    })
    $('#storeSelect tr').on("click",selectStore);
}

function dynamicOrder() {
    initOrder();
    order.type="dynamic";
    $('#dynamicOrder-container').css("display","block");
    $('#staticOrder-container').css("display","none");
}

function showOptimalOrder(orderSum) {
    var sum = $(document.createElement('div'));
    sum.addClass("orderSum");
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{$(".orderSum").remove()});
    sum.append(button);
    sum.append($(document.createElement('h1')).text("Optimal Order"));
    orderSum.forEach(storOrder=>{
        var div = $(document.createElement('div'));
        div.addClass("storeSum");
        div.append($(document.createElement('h2')).text(Object.keys(storOrder)[0]));
        for( var productkey of storOrder[Object.keys(storOrder)]){
            var divP = $(document.createElement('div'));
            divP.append($(document.createElement('span')).text(Object.keys(productkey)[0] + ": "));
            divP.append($(document.createElement('span')).text(productkey[Object.keys(productkey)]));
            divP.append($(document.createElement('br')));
            div.append(divP);
        }
        sum.append(div);
    })
    $("#order-container").append(sum);
}

function addOderDiscounts() {
    return undefined;
}

function showDiscounts(discounts) {
    $("#order-container div:first-child").css("display","none");
    const win =   $(document.createElement('div'));
    win.addClass("discountsWin");
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{
        document.getElementById("order-container").classList.toggle("show");
        win.remove();
    });
    win.append(button);
    win.append($(document.createElement('h1')).text("Discounts"));
    discounts.forEach((discount,index)=>{
        var div = $(document.createElement('div'));
        div.addClass("discount");
        div.append($(document.createElement('h2')).text(discount.name));
        div.append($(document.createElement('p')).text("Because you bought: " + discount.quantity+" "+discount.product));
        div.append($(document.createElement('span')).text(" You can get " + discount.operator + ":"));
        discount.offers.forEach(offer=>{
            var offerDiv =  $(document.createElement('div'));
            var of = offer.quantity+" "+ offer.product+ " for additional "+ offer.forAdditional+" Nis";
            switch (discount.operator) {
                case "one of": {
                    var radio = $('<input type="radio" name="' +index+'" id="'+offer.productId+'" value="'+offer.productId+'" checked>');
                    offerDiv.append(radio);
                    var label = $(document.createElement('label')).text(of);
                    label.prop("for", offer.productId);
                    offerDiv.append(label);
                    break;
                }
                default: {
                    offerDiv.append($(document.createElement('span')).text(of));
                    break;
                }
            }
            div.append(offerDiv);
        })
        div.on("click",event=>{
            const discount = event.target.closest(".discount");
            discount.classList.toggle("selected");
        })
        win.append(div);
    })
    button = $(document.createElement('button')).text("Confirm");
    button.on("click",addOderDiscounts());
    win.append(button);
    $("#order-container").append(win);
}

function finishOrder() {
    $.ajax({
        url: ORDER_URL,
        method: 'GET',
        data: {
            action: "finishOrder",
            type: order.type,
            store: order.storeId,
            x: document.getElementById("x").value,
            y: document.getElementById("y").value,
            date: document.getElementById("datepicker").value
        },
        success: function (json) {
            order.type === "dynamic" && showOptimalOrder(json.orderSum);
            if(json.discounts.length > 0)
                showDiscounts(json.discounts);
            else
                showOrderSum();
        },
        error: ()=>{
            alert("error")
        }
    });
}


$(function() {
    $("#datepicker").datepicker({minDate: 0});
    $("#datepicker").datepicker("option", "dateFormat","dd/mm/yy");

});

