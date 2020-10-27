const ZONE_CENTER_URL = buildUrlWithContextPath("pages/zonesCenter/zonesCenter.html");
const AREA_URL = buildUrlWithContextPath("area");

let storesJson = {}
let zoneName;
let ownerId;

function backButton(){
    window.location.replace(ZONE_CENTER_URL);
}


function validateAmount(amount) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "validateAmount",
            zoneName:zoneName,
            owner:ownerId,
            store:storeId,
        },
        success: function(json) {
            showProducts(json.storeProducts,'#storeProductSelect tbody');
            $("#storeProductSelect tr").on("click",chooseAmount);

        }
    });
}

function chooseAmount() {
    var amount = prompt("Please enter amount to buy:");
    if (!(amount == null) || amount !== "") {
        try{
            validateAmount(amount);
        }
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
            showProducts(json.storeProducts,'#storeProductSelect tbody');
            $("#storeProductSelect tr").on("click",chooseAmount);

        }
    });
}

function selectStore(event) {
    $('#storeSelect tr').removeClass("selected");
    const storeNode = event.target.closest("tr");
    storeNode.classList.add("selected");
    getStoreProducts(storeNode.children[0].innerHTML);

}

function staticOrder() {
    $('#staticOrder-container').css("display","block");
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

function showProducts(products,selectors) {
    const table = $(selectors);
    table.empty();
    products.forEach(function (product) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(product))
            tr.append($(document.createElement('td')).text(product[key]));
        tr.appendTo(table);
    });

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

$(function() {
    zoneName = urlParam('zoneName');
    document.getElementById("zoneName").innerText = zoneName;
    getOwnerId(zoneName)

})