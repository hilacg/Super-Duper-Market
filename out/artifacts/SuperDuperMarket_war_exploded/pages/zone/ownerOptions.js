function getFeedbacks(storeId) {
    console.log(storeId);
}

function openFeedbacks(event){
    var popup = $(`<div id="feedbacks-container" class="popup-window">
    <div>
    <button onclick="$('#feedbacks-container').remove()" class="fa fa-lg fa-times"></button>
    <h1>Feedbacks</h1>
</div></div>`);
    $("body").append(popup);
    showWindow("feedbacks-container");
    getFeedbacks(event.target.closest("tr").children[2].innerHTML);
}

function openOrderHistory(event){

}