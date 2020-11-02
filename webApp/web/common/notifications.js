const notifyRate = 5000; //milli seconds

function showNotification(notification) {
    const newNote = $('<div class="notification"></div>')
    const exit = $(document.createElement('button'));
    exit.click(event=>event.target.closest("div").remove());
    exit.addClass("fa fa-xs fa-times");
    newNote.append(exit);
    var type;
    switch(notification.type){
        case "ORDER":{
            type="fa fa-cart-plus";
                break;
            }
        case "FEEDBACK":{
            type="fa fa-commenting";
                break;
            }
        case "STORE":{
            type="fa fa-home";
                break;
            }
    }
    const icon = $(document.createElement('i'));
    icon.addClass(type)
    newNote.append(icon);
    newNote.append($(document.createElement('div')).text(notification.message));
    var tempNote =  newNote.clone();
    tempNote.find("button").click((event=>event.target.closest("div").remove()));
    setTimeout(()=>{ tempNote.remove() }, 5000)
    $("#notifications").append(tempNote);
    $(".overlay-content").prepend(newNote);
}


function ajaxNotification() {
    $.ajax({
        async: false,
        url:ACCOUNT_URL,
        data: {
            action: "getNotifications"
        },
        success: function(json) {
            const notification = JSON.parse(json);
            notification.sent===false && showNotification(notification);
        }
    });
}
var button = $(document.createElement('button'));
button.addClass("exit");
button.addClass("fa fa-lg fa-times");
function openBar() {
    document.getElementById("noteBar").style.width = "20%";
}

function closeBar() {
    document.getElementById("noteBar").style.width = "0%";
}
$(function() {
    if(!user.isCustomer) {
        $("body").append($('<div id="notifications"></div>'));
        $("#toolBar").append($('<i id="notificationsBtn" onclick="openBar()" class="fa fa-lg fa-bell-o""></i>'))
        $("body").append($(`<div id="noteBar" class="overlay">
        <button onclick="closeBar()" class="fa fa-lg fa-times"></button>
        <div class="overlay-content">`));
        setInterval(ajaxNotification, notifyRate);
    }
});