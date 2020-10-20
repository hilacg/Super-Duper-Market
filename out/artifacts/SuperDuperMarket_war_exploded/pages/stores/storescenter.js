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


function showFileChooser(){
    if(!user.isCustomer){
        const uploadButton = document.getElementById("fileChooser");
        const uploadImg = document.getElementById("uploadImg");
        uploadButton.style.display = "block";
        uploadButton.addEventListener("mouseenter",()=>(uploadImg.style.filter = "invert(1)"));
        uploadButton.addEventListener("mouseleave",()=>(uploadImg.style.filter = "invert(0)"));
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

/*function getUserType(){
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
}*/


$(function() {
    getUser();
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

});