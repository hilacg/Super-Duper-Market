const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const AREA_URL = buildUrlWithContextPath("area");

function backButton(){
    window.location.replace(ZONE_CENTER_URL);
}

function showProducts(products) {

    const table = $('#productsTable tbody');
    table.empty();
    products.forEach(function (product) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(product))
            tr.append($(document.createElement('td')).text(product[key]));
        tr.appendTo(table);
    });

}

function getProducts(zoneName) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getProducts",
            zoneName:zoneName
        },
        success: function(json) {
            showProducts(json.products);
        }
    });
}
function showStores(stores) {

    const table = $('#storesTable tbody');
    table.empty();
    stores.forEach(function (store) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(store))
            tr.append($(document.createElement('td')).text(store[key]));
        tr.appendTo(table);
    });

}

function getStores(zoneName) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getStores",
            zoneName:zoneName
        },
        success: function(json) {
 //           showStores(json.stores);
        }
    });
}
$.urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null) {
        return null;
    }
    return decodeURI(results[1]) || 0;
}

$(function() {
    var zoneName = $.urlParam('zoneName');
    getProducts(zoneName);
    getStores(zoneName);
})