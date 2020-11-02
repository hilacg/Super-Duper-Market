function showFeedbacks(json) {
    json.forEach(feedback=>{
        var i;
        var feed = $(document.createElement('div'));
        feed.addClass("feedback");
        var stars = $(document.createElement('div'));
        stars.addClass("stars");
        for(i=0; i < feedback.stars;i++)
            stars.append($('<i class="fa fa-star star checked" ></i>'));
        for(i=0;i<5-feedback.stars;i++)
            stars.append($('<i class="fa fa-star star" ></i>'));
        feed.append(stars);
        var details = $(document.createElement('div'));
        for( var key of Object.keys(feedback)) {
            if(key!=="storeId") {
                details.append($(document.createElement('span')).text(key + ": " + feedback[key]));
                details.append($(document.createElement('br')));
            }
        }
        feed.append(details);
        $("#feedbacks").append(feed);
    });
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

function getOrders(storeId) {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getStoreOrders",
            storeId: storeId,
        },
        success:(json)=> {
            json.forEach(order=>$('#order-history-container>div').append(showOrderSum(order)));
            $(".summaryButtons").remove();
        }
    })
}

function openOrderHistory(event){
    var popup = $(`<div id="order-history-container" class="popup-window">
    <div>
    <button onclick="$('#order-history-container').remove()" class="fa fa-lg fa-times"></button>
    <h1>Orders History</h1>
</div></div>`);
    $("body").append(popup);
    showWindow("order-history-container");
    getOrders(event.target.closest("tr").children[0].innerHTML);
}