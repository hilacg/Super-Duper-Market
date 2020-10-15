var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");

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

function isStoreOwner() {
  /*  $.ajax({
        async: false,
        url: LOGIN_URL,
        data: {
            action: "status" //check what to send
        },
        type: 'GET',
        success: function (json) {
            result = json.isComputer;
        }
    });
    return result;*/
    return true;
}

function showFileChooser(){
    if(isStoreOwner()){
        $('<input type="file" id="xmlFile" name="xmlFile">')
            .appendTo($("#fileChooser"));
    }
}

$(function() {
    $("#welcome").text("Welcome")
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    showFileChooser();

});