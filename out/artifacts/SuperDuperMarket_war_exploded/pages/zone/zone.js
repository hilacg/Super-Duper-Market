const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");
const AREA_URL = buildUrlWithContextPath("area");
const ORDER_URL = buildUrlWithContextPath("area/order");
const ACCOUNT_URL = buildUrlWithContextPath("users/account");
const refreshRate = 2000; //milli seconds

let storesJson = {}
let zoneName;
let ownerId;
let user;
let order={
        stores:[]
};

function showWindow(winId){
    document.getElementById(winId).classList.toggle("show");
}
function backButton(){
    window.location.replace(ZONE_CENTER_URL);
}


function makeProduct(discount) {

    const productDiv = $(document.createElement('div'));
    productDiv.addClass("orderProduct");
    for (var key of Object.keys(discount)) {
        productDiv.append($(document.createElement('span')).text(key + ": " + discount[key]));
        productDiv.append($(document.createElement('br')))
    }
    return productDiv;
}

function showOrders(orders,main) {
    orders.forEach(order => {
        const orderDiv = $(document.createElement('div'));
        orderDiv.addClass("storeOrder");
        for (var key of Object.keys(order.ordersum)) {
            orderDiv.append($(document.createElement('span')).text(key + ": " + order.ordersum[key]));
            orderDiv.append($(document.createElement('br')));
        }

        const orderDetailsDiv = $(document.createElement('div'));
        orderDetailsDiv.addClass("storeOrderDetails");
        orderDetailsDiv.append($(document.createElement('b')).text("Products bought:"));
        order.product.forEach(product => {
            orderDetailsDiv.append(makeProduct(product));
        })
        orderDetailsDiv.append($(document.createElement('b')).text("Products from discounts:"));
        order.discount.forEach(discount => {
            orderDetailsDiv.append(makeProduct(discount));
        })
        main.append(orderDiv);
        main.append(orderDetailsDiv);
        orderDiv.click(event => {
            var div =  event.target.closest(".storeOrder");
            div.classList.toggle("active")
            var panel = div.nextElementSibling;
            if (panel.style.maxHeight) {
                panel.style.maxHeight = null;
            } else {
                panel.style.maxHeight = panel.scrollHeight + "px";
            }
        });
    })
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
    var productTable = $(".products .productsTable tbody tr");
    productTable.unbind();
    if(user.isCustomer)
        productTable.click((event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML,event.target.closest("tr").children[1].innerHTML));
    else
        productTable.click((event)=>choosePrice(event.target.closest("tr").children[0].innerHTML,event.target.closest("tr").children[1].innerHTML));
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
    if(!user.isCustomer){
        addOwnerButtons()
    }

}

function addOwnerButtons(){
    $('#storesTable tbody>tr').append($('<td style="display: grid;position: relative;top: 50px;"></td>'))
    $('#storesTable tbody').find("tr").each(function (){
        if($(this).children().eq(2).text() === user.name ){
            var feebeacks = $('<button class="feedbacks" style="margin-bottom: 5px" onclick="openFeedbacks(event)"><i class="fa fa-commenting"></i> Feedbacks</button>');
            var orderHistory = $('<button class="orderHistory" onclick="getStoreOrders(event)"><i class="fa fa-history"></i> Orders</button>');
            $(this).children().eq(9).append(feebeacks,orderHistory);
        }
    })
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
            switchZone();
            setInterval(getProducts, refreshRate);
            setInterval(getStores, refreshRate);
            if(!user.isCustomer)
                $('#storesTable thead tr').append($(document.createElement('th')));

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


function switchZone() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "switchZone",
            zoneName: zoneName,
            ownerId: ownerId

        },
        type: 'GET',
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
    $("#storeForm").submit(()=>{
        saveNewStore();
        return false;
    })
})