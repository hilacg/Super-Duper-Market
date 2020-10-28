const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");
const AREA_URL = buildUrlWithContextPath("area");
const ORDER_URL = buildUrlWithContextPath("area/order");

let storesJson = {}
let zoneName;
let ownerId;
let user;
let order={};

function showWindow(winId){
    document.getElementById(winId).classList.toggle("show");
}
function backButton(){
    window.location.replace(ZONE_CENTER_URL);
}

function openOrder() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "initOrder",
        },
        success: function (response) {
            $("#orderForm").trigger('reset');
            $("#storeProductSelect tbody").empty();
            $("#storeSelect tbody").empty();
            $("#staticOrder-container").css("display","none");
            $("#dynamicOrder-container").css("display","none");
            document.getElementById('order-container').classList.toggle("show");
        }
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
            $("#storeProductSelect tr").on("click",(event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML));

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
    order.type="dynamic";
    $('#dynamicOrder-container').css("display","block");
    $('#staticOrder-container').css("display","none");
}

function showProducts(products,selectors) {
    const table = $(selectors);
    table.empty();
    products.forEach(function (product) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(product))
            tr.append($(document.createElement('td')).text(product[key]));
        tr.appendTo(table);
    });
    $("#products .productsTable tr").on("click",(event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML));
}

function getProducts() {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getProducts",
            zoneName:zoneName,
            owner: ownerId,
        },
        success: function(json) {
            showProducts(json.products,".productsTable tbody");
        }
    });
}


function showStoreProducts(products) {
    var div = $(document.createElement('div'));
    div.addClass("storeProducts");
    var ol = $(document.createElement('ol'));
    div.append(ol);
    products.forEach(product=>{
        var li = $(document.createElement('li'));
        ol.append(li);
        for( var key of Object.keys(product)) {
            li.append(key + ": " + product[key]);
            li.append($(document.createElement('br')))
        }
    })
    return div
}

function showStores(stores) {
    const table = $('#storesTable tbody');
    table.empty();
    stores.forEach(function (store) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(store))
            if(key!== "products")
                tr.append($(document.createElement('td')).text(store[key]));
            else
                tr.append(showStoreProducts(store[key]));
        tr.appendTo(table);
    });

}

function getStores() {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getStores",
            zoneName:zoneName,
            owner: ownerId,
        },
        success: function(json) {
            storesJson = json.stores;
            showStores(json.stores);
        }
    });
}
function urlParam(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null) {
        return null;
    }
    return decodeURI(results[1]) || 0;
}

function getOwnerId(zoneName) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getOwnerId",
            zoneName:zoneName
        },
        success: function(response) {
            ownerId = parseInt(response);
            getProducts();
            getStores();
        }
    });
}

function getUser(){
    $.ajax({
        async: false,
        url: LOGIN_URL,
        data: {
            action: "getUser"
        },
        type: 'GET',
        success: function (json) {
            user =json;
            showUserOptions();
        }
    });
}

function showUserOptions(){
    user.isCustomer ? $(".customerOption").toggleClass("show") : $(".ownerOption").toggleClass("show");
}


function showOrderSum(orderSum) {
    var sum = $(document.createElement('div'));
    sum.addClass("orderSum");
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{$(".orderSum").remove()});
    sum.append(button);
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
    const win =  $("#order-container div:first-child");
    win.empty();
    var button = $(document.createElement('button')).text("X");
    button.on("click",()=>{document.getElementById("order-container").classList.toggle("show");});
    win.append(button);
    discounts.forEach((discount,index)=>{
        var div = $(document.createElement('div'));
        div.addClass("discount");
        div.append($(document.createElement('h2')).text(discount.name));
        div.append($(document.createElement('p')).text("Because you bought: " + discount.quantity+" "+discount.product));
        div.append($(document.createElement('span')).text(" You can get: " + discount.operator));
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
           showOrderSum(json.orderSum);
           if(json.discounts.length > 0)
              showDiscounts(json.discounts);

        },
        error: ()=>{
         alert("error")
        }
    });
}

$(function() {
    getUser();
    zoneName = urlParam('zoneName');
    document.getElementById("zoneName").innerText = zoneName;
    getOwnerId(zoneName);
    $("#orderForm").submit(()=>{
        finishOrder();
        return false;
    })
})