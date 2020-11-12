
/**
This expects that a messages area is present in the HTML
*/
function showMessage(message, level) {
    console.log("showMessage", message, level);
    $('#message-span').html(message);
    $('#message-div').removeClass("message-error message-warning message-info message-success");
    if (level == "error") {
        $('#message-div').addClass("message-error");
        $('#message-icon').html("error");
    } else if (level == "warning") {
        $('#message-div').addClass("message-warning");
        $('#message-icon').html("warning");
    } else if (level == "success") {
        $('#message-div').addClass("message-success");
        $('#message-icon').html("check");
    } else {
        $('#message-div').addClass("message-info");
        $('#message-icon').html("info");
    }
    $('#message-div').show();
}

function showAjaxError(response) {
    console.log(response);
    showMessage(response.responseJSON.error.value, "error");
}

function hideMessages() {
    $('#message-div').hide();
}