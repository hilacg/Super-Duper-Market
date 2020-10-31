const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");
const AREA_URL = buildUrlWithContextPath("area");
const ORDER_URL = buildUrlWithContextPath("area/order");
const ACCOUNT_URL = buildUrlWithContextPath("users/account");

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


function showProducts(products,selectors) {
    const table = $(selectors);
    table.empty();
    products.forEach(function (product) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(product))
            tr.append($(document.createElement('td')).text(product[key]));
        tr.appendTo(table);
    });
    $("#products .productsTable tbody tr").unbind();
    $("#products .productsTable tbody tr").click((event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML));
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