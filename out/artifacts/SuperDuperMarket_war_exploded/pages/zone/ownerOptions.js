let newStore={};

function showFeedbacks(json) {
    if(json.length === 0)
        $("#feedbacks").append();
    else {
        json.forEach(feedback => {
            var i;
            var feed = $(document.createElement('div'));
            feed.addClass("feedback");
            var stars = $(document.createElement('div'));
            stars.addClass("stars");
            for (i = 0; i < feedback.stars; i++)
                stars.append($('<i class="fa fa-star star checked" ></i>'));
            for (i = 0; i < 5 - feedback.stars; i++)
                stars.append($('<i class="fa fa-star star" ></i>'));
            feed.append(stars);
            var details = $(document.createElement('div'));
            for (var key of Object.keys(feedback)) {
                if (key !== "storeId") {
                    details.append($(document.createElement('span')).text(key + ": " + feedback[key]));
                    details.append($(document.createElement('br')));
                }
            }
            feed.append(details);
            $("#feedbacks").append(feed);
        });
    }
}

function getFeedbacks(storeId) {
    $.ajax({
        url: AREA_URL,
        data: {
            action: "storeFeedbacks",
            storeId: storeId,
            zoneName: zoneName
        },
        success:(json)=> {
            showFeedbacks(json);
        }
    })
}

function openFeedbacks(event){
    var popup = $(`<div id="feedbacks-container" class="popup-window">
    <div>
    <button onclick="$('#feedbacks-container').remove()" class="fa fa-lg fa-times"></button>
    <h1>Feedbacks</h1>
    <div id="feedbacks"></div>
</div></div>`);
    $("body").append(popup);
    showWindow("feedbacks-container");
    getFeedbacks(event.target.closest("tr").children[0].innerHTML);
}


function getStoreOrders(event) {
    const storeId = event.target.closest("tr").children[0].innerHTML;
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getStoreOrders",
            storeId: storeId,
        },
        success:(json)=> {
            openOrderHistory();
            if(json.length === 0)
                $('#order-history-container>div').append($(document.createElement('span')).text("No orders yet"));
            else
                showOrders(json,$('#order-history-container>div>div'));
        }
    })
}

function openOrderHistory(){
    var popup = $(`<div id="order-history-container" class="popup-window">
    <div>
    <button onclick="$('#order-history-container').remove()" class="fa fa-lg fa-times"></button>
    <h1>Orders History</h1>
    <div style="font-family: Courier New, Courier, monospace"></div></div></div>`);
    $("body").append(popup);
    showWindow("order-history-container");
}

function openStore(){
    showWindow('store-container');
    $(".productsToAdd>span,.productsToAdd>br").remove();
    $("#storeForm").trigger('reset');
    newStore = {
        products:[],
    }
}

function choosePrice(productId,productName) {
    var price = prompt("Please enter price:");
    if (!(price === null || price === "")) {
        if(/^\d+$/.test(price)) {
            const index = newStore.products.findIndex(product=>product.productId === productId);
            if(index === -1) {
                newStore.products.push({
                    productId: productId,
                    price: price,
                    productName: productName
                })
            }
            else
                newStore.products[index].price = price;
            $(".productsToAdd>span,.productsToAdd>br").remove();
            newStore.products.forEach(product=>{
                $(".productsToAdd").append($(`<span>${product.productName}: </span><span>${product.price}₪</span><br>`))
            })

        }
        else
            alert("Price must be an integer");
    }
}

function saveNewStore(){
    if( $(".productsToAdd").children().length > 1) {
        $.ajax({
            url: AREA_URL,
            method: 'GET',
            contentType: 'application/json; charset=utf-8',
            data: {
                action: "addNewStore",
                storeId:$("#storeForm #storeId").val(),
                storeName: $("#storeForm #storeName").val(),
                x: $("#storeForm .x").val(),
                y: $("#storeForm .y").val(),
                ppk: $("#storeForm .ppk").val(),
                products: JSON.stringify(newStore.products)
            },
            success: function (json) {
                alert("new store opened")
                $("#store-container").find(".exit").click();
            },
            error: (error) => {
                alert(error.responseText)
            }
        });
    }
    else
        alert("Please choose products to sell first");
}
