const refreshRate = 8000; //milli seconds
const USER_LIST_URL = buildUrlWithContextPath("users");
const LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");
const AREA_URL = buildUrlWithContextPath("area");
const ACCOUNT_URL = buildUrlWithContextPath("users/account");
const ZONE_URL = buildUrlWithContextPath("pages/zone/zone.html");

let user={
    id:0,
    name:"",
    isCustomer:true,
};



function refreshAction(actions) {
    const activitiesTable = $('#activitiesTable tbody');
    activitiesTable.empty();
    actions.forEach(function (action) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(action))
            tr.append($(document.createElement('td')).text(action[key]));
        tr.appendTo(activitiesTable);
    });
}

function getActions() {
    $.ajax({
        url: ACCOUNT_URL,
        data:{
            action: "getAccountAction"
        },
        success: function(json) {
            refreshAction(json.actions);
        }
    });
}

function getAccountBalance() {
    $.ajax({
        url: ACCOUNT_URL,
        data:{
            action: "getAccountBalance"
        },
        success: function(balance) {
            document.getElementById("balance").children[1].innerText = balance;
        }
    });
}

function showAccount(){
    document.getElementById("account-container").classList.toggle("show");
    user.isCustomer ? document.getElementById("depositForm").style.display = "block" : null;
    getAccountBalance();
    getActions();
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
        data:{
            action: "getUsersList"
        },
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
function enterZone(event){
//    var zoneName =  event.target.parentNode.children[0].innerText;
    var zoneName = event.target.closest("tr").children[0].innerText;
    var url = ZONE_URL.concat("?zoneName="+zoneName);
    window.location.replace(url);
}

function refreshZonesTable(zones) {
    const zonesTable = $('#zonesTable tbody');
    zonesTable.empty();
    zones.forEach(function (zone) {
        var tr = $(document.createElement('tr'));
        for( var key of Object.keys(zone))
           tr.append($(document.createElement('td')).text(zone[key]));
        var btn = $(document.createElement('button'));
        btn.addClass("enterZone");
        btn.append($(document.createElement('il')).addClass("fa fa-lg fa-sign-in"));
        tr.append(btn);
        tr.appendTo(zonesTable);

    });
    $(".enterZone").on("click",enterZone);
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

function showNotification(response) {
    const newNote = $('<div class="notification"></div>')
    const exit = $(document.createElement('button'));
    exit.click(event=>event.target.closest("div").remove());
    exit.addClass("fa fa-xs fa-times")
    newNote.append(exit)
    newNote.append($(document.createElement('div')).text(response));
    setTimeout(()=>{ newNote.remove() }, 10000)
    $("#notifications").append(newNote);
}

function ajaxNotification() {
    $.ajax({
        url:ACCOUNT_URL,
        data: {
            action: "getNotifications"
        },
        success: function(response) {
            response!=="" && showNotification(response);
        }
    });
}

function deposit(){
    $.ajax({
        url:ACCOUNT_URL,
        method: 'GET',
        data:{
            action:"deposit",
            amount: document.getElementById("amount").value,
            date: document.getElementById("datepicker").value,
        },
        success: function(){
            getActions();
            $("#depositForm .message").removeClass("error");
            $("#depositForm .message").text("Deposit succeeded");
            getAccountBalance();
        },
        error:(response)=> {
            $("#depositForm .message").text(response.responseText);
            $("#depositForm .message").addClass("error");
        }
    });
}

$(function() {
    getUser();
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxZone, refreshRate);
    setInterval(ajaxNotification, refreshRate);
    $("#datepicker").datepicker({minDate: 0});
    $("#datepicker").datepicker("option", "dateFormat","dd/mm/yy");
    $("#depositForm").submit(()=>{
        deposit();
        return false;
    })
});