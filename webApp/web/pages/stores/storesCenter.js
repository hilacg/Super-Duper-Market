var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var LOGIN_URL = buildUrlWithContextPath("pages/login/loginShortResponse");

var user={
    name:"",
    isCustomer:true,
};

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
    if(!user.isCustomer){
        $('<input type="file" id="xmlFile" name="xmlFile" accept=".xml">')
            .appendTo($("#fileChooser"));
    }
}

function getUser(){
    getUserType();
    getUserName();
}

function getUserType(){
      $.ajax({
      async: false,
      url: LOGIN_URL,
      data: {
          action: "getUserType"
      },
      type: 'GET',
      success: function (json) {
          user.isCustomer = JSON.parse(json);
          $("#userType").append(user.isCustomer ? "customer" : "store owner");
          showFileChooser();
      }
  });
  return false;
}

function getUserName(){
    $.ajax({
        async: false,
        url: LOGIN_URL,
        data: {
            action: "getUserName"
        },
        type: 'GET',
        success: function (json) {
            user.name = json;
            $("#hello").append(json);
        }
    });
    return false;
}


$(function() {
    getUser();
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

});