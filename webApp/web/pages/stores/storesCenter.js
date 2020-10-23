const refreshRate = 5000; //milli seconds
const USER_LIST_URL = buildUrlWithContextPath("userslist");
const LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");
const AREA_URL = buildUrlWithContextPath("area");

let user={
    id:0,
    name:"",
    isCustomer:true,
};

function showAccount(){
    document.getElementById("account-container").classList.toggle("show");
    user.isCustomer ? document.getElementById("depositForm").style.display = "block" : null;

}


function refreshUsersList(users) {
    //clear all current users
    $("#userlist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(username,type) {

        //create a new <li> tag with a value in it and append it to the #userslist (div with id=userslist) element
        $('<li>' + username + " " + type + '</li>')
            .appendTo($("#userlist"));
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}


function showFileChooser(){
    if(!user.isCustomer){
        const uploadButton = document.getElementById("fileChooser");
        uploadButton.style.display = "block";
    }
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
            $("#userType").append(user.isCustomer ? "customer" : "store owner");
            $("#hello").append(user.name);
            showFileChooser();
        }
    });
    return false;

}

function loadXML(event) {
    const file = event.target.files[0];
    var formData = new FormData();
    formData.append("file", file);
    formData.append("id", user.id);
 //   formData.append("action", "loadXML");
    const message =  $("#fileChooser .message");

    $.ajax({
        method:'POST',
        data: formData,
        url: AREA_URL,
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        timeout: 4000,
        error: function(response) {
            console.error("Failed to submit");
            message.text("Failed to upload file: " + response.responseText);
            message.addClass("error");
        },
        success: function(response) {
            message.text(response);
            message.removeClass("error");
        }
    });
}

function refreshZonesTable(zones) {
    const zonesTable = $('#zonesTable tbody');
    zonesTable.empty();
    zones.forEach(function (zone) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(zone))
           tr.append($(document.createElement('td')).text(zone[key]));
        tr.appendTo(zonesTable);
    });

   /* var tr = $('.tableBody tr');
    for (var i = 0; i < tr.length; i++) {
        tr[i].onclick = createGameDialog;
    }*/
}

function ajaxZone() {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getZones"
        },
        success: function(json) {
            refreshZonesTable(json.zones);
        }
    });
}

$(function() {
    getUser();
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxZone, refreshRate);
    $("#datepicker").datepicker({minDate: -1});
    $("#datepicker").datepicker("option", "dateFormat","dd/mm/yy");
});