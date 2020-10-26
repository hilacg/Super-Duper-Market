const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const AREA_URL = buildUrlWithContextPath("area");

function backButton(){
    window.location.replace(ZONE_CENTER_URL);
}

function showProducts(products) {
    const table = $('.productsTable tbody');
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

function getStores(zoneName) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getStores",
            zoneName:zoneName
        },
        success: function(json) {
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

$(function() {
    var zoneName = urlParam('zoneName');
    document.getElementById("zoneName").innerText = zoneName;
    getProducts(zoneName);
    getStores(zoneName);
})