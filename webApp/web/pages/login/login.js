
$(function() { // onload...do
    //add a function to the submit event
    $("#loginForm").submit(function() {
        const userName = $("#userName").val();
        const isCustomer = $("#customer").is(':checked');
        $.ajax({
            data: {
                action: "login",
                userName: userName,
                isCustomer: isCustomer
            },
            url: this.action,
            timeout: 2000,
            error: function(errorObject) {
                console.error("Failed to login !");
                $("#error-placeholder").text(errorObject.responseText)
            },
            success: function(nextPageUrl) {
                window.location.replace(nextPageUrl);
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});