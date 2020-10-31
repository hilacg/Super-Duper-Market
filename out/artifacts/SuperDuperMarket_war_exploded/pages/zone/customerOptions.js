
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
    sum.addClass("optimalOrder");
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{$(".optimalOrder").remove()});
    sum.append(button);
    sum.append($(document.createElement('h1')).text("Optimal Order"));
    orderSum.forEach(storOrder=>{
        var div = $(document.createElement('div'));
        div.addClass("storeSum");
        div.append($(document.createElement('h2')).text(storOrder.storeName));
        for( var storeDetail of Object.keys(storOrder.storeDetails)){
            var divP = $(document.createElement('div'));
            divP.append($(document.createElement('span')).text((storeDetail) + ": "));
            divP.append($(document.createElement('span')).text(storOrder.storeDetails[storeDetail]));
            divP.append($(document.createElement('br')));
            div.append(divP);
        }
        sum.append(div);
    })
    $("#order-container").append(sum);
}

function chargeCustomer() {
    $.ajax({
        url: ACCOUNT_URL,
        data: {
            action: "charge",
            owner: ownerId,
            zoneName: zoneName
        },
        success:(amount)=> {
            alert("You were charged a total of "+ amount+ " Nis");

        }
    })
}

function confirmOrder() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "confirmOrder",
        },
        success:()=> {
            alert("Order Added Successfully!");
            chargeCustomer();
        }
    })
}

function showOrderSum(json,winId) {
    var orderSum = json.orderSum;
    $("#"+winId+">div:gt(0)").remove();
    var sum = $(document.createElement('div'));
    sum.addClass("orderSum");
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{$("#"+winId+">div:gt(0)").remove(); showWindow(winId)});
    sum.append(button);
    sum.append($(document.createElement('h1')).text("Order Summary"));
    var divOrder = $(document.createElement('div'));
    divOrder.addClass("summary-container");
    orderSum.forEach(storeOrder=> {
        var divStore = $(document.createElement('div'));
        var sumList = $(document.createElement('ul'))
        divStore.append(sumList);
        sumList.addClass("storeSum");
        sumList.append($(document.createElement('h2')).text(storeOrder["store Name"]));
        showProductsSum([storeOrder.details], sumList)
        sumList.append($(document.createElement('h3')).text("Product bought:"));
        showProductsSum(storeOrder.product, sumList);
        if (storeOrder.discount.length > 0) {
            sumList.append($(document.createElement('h3')).text("Product from discounts:"));
            showProductsSum(storeOrder.discount, sumList);
        }
        divOrder.append(divStore);
        });
    sum.append(divOrder);
    divOrder = $(document.createElement('div'));
    showProductsSum([json.orderFinalSum],divOrder)
    sum.append(divOrder);
    var divButtons = $(document.createElement('div'));
    divButtons.addClass("summaryButtons")
    var confirm = $(document.createElement('button')).text("confirm");
    confirm.on("click",()=> {confirmOrder(); button.click();});
    divButtons.append(confirm);
    var cancel = $(document.createElement('button')).text("cancel");
    cancel.on("click",()=>button.click());
    divButtons.append(cancel);
    sum.append(divButtons);
    $("#"+winId).append(sum);
}

function showProductsSum(products,sumList){
    products.forEach(product=>{
        var divProduct = $(document.createElement('li'));
        for( var productkey of Object.keys(product)) {
            divProduct.append($(document.createElement('span')).text(productkey + ": " + product[productkey]));
            divProduct.append($(document.createElement('br')));
        }
        divProduct.append($(document.createElement('br')));
        sumList.append(divProduct);
    });
}


function getOrderSum() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getOrderSum",
        },
        success: (json)=>{showOrderSum(json,"order-container")
        }
    })
}

function addOrderDiscounts() {
    var requestData = [];
    var selectedDiscount = $(".discount.selected");
    selectedDiscount.each( (index,discount) => {
        var productId = $(discount).find("input:radio:checked").attr('class');
        requestData.push({
            storeId: discount.classList[1],
            discountName: discount.children[0].textContent,
            chosenRadio: $(discount).find("input:radio:checked").next().text(),
            productId: typeof productId !== 'undefined' ? productId : 0
        });
    });
    $.ajax({
        url: ORDER_URL,
        contentType: 'application/json; charset=utf-8',
        data: {
            action: "saveDiscounts",
            discounts:JSON.stringify(requestData),
        },
        success: ()=>getOrderSum()
    })
}

function showDiscounts(discounts) {
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
        div.addClass(discount.storeId.toString());
        div.append($(document.createElement('h2')).text(discount.name));
        div.append($(document.createElement('p')).text("Because you bought: " + discount.quantity+" "+discount.product));
        div.append($(document.createElement('span')).text(" You can get " + discount.operator + ":"));
        var offerDiv =  $(document.createElement('div'));
        offerDiv.css("width","fit-content");
        offerDiv.click(event=>{
            e = window.event;

            //IE9 & Other Browsers
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            //IE8 and Lower
            else {
                e.cancelBubble = true;
            }
        })
        discount.offers.forEach((offer,indx2)=>{
            var of = offer.quantity+" "+ offer.product+ " for additional "+ offer.forAdditional+" Nis";
            switch (discount.operator) {
                case "one of": {
                    var radio = $('<input type="radio" name="' +index+'" id="'+ index + "_" +indx2 +'" value="'+offer.productId+'" checked>');
                    radio.addClass(offer.productId.toString())
                    offerDiv.append(radio);
                    var label = $(document.createElement('label')).text(of);
                    label.prop("for", `${index}_${indx2}`);
                    offerDiv.append(label);

                    break;
                }
                default: {
                    offerDiv.append($(document.createElement('span')).text(of));
                    break;
                }
            }
            offerDiv.append($(document.createElement('br')));
            div.append(offerDiv);
        })
        div.on("click",event=>{
            const discount = event.target.closest(".discount");
            discount.classList.toggle("selected");
        })
        win.append(div);
    })
    button = $(document.createElement('button')).text("Confirm");
    button.on("click",()=>addOrderDiscounts());
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
            $("#order-container div:first-child").css("display","none");
            order.type === "dynamic" && showOptimalOrder(json.orderSum);
            if(json.discounts.length > 0)
                showDiscounts(json.discounts);
            else
                getOrderSum();
        },
        error: ()=>{
            alert("error")
        }
    });
}

function showCustomerOrders(json) {
    var div = $(document.createElement('div'))
    div.attr('id', 'order-history');
    div.attr('class', 'pop-popup-window show');
    div.append($(document.createElement('div')));
    $("body").append(div);
    json.forEach(order=>{
        showOrderSum(order,"order-history");
    })
}

function orderHistory(){
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getCustomerOrders",
        },
        success: (json)=>{showCustomerOrders(json);
        $(".summaryButtons").remove();
        }
    })
}


$(function() {
    $("#datepicker").datepicker({minDate: 0});
    $("#datepicker").datepicker("option", "dateFormat","dd/mm/yy");

});

