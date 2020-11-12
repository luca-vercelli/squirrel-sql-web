var ws_url = '../ws/';
var enable_mock = true;

$(document).ready(function(){
    $('#login-button').on('click', login);
});

function login() {

    if (enable_mock) {
        window.location.replace("index.html");
    }

    $.ajax({
        url: ws_url + 'Login',
        dataType: 'json',
        type: 'POST',
        data: {
            username: document.querySelector('#username').MDCTextField.value,
            password: document.querySelector('#password').MDCTextField.value
        },
        success: function(response){
            window.location.replace("index.html");
        },
        error: function(response){
            // TODO pretty print
            if (response && response.responseJSON && response.responseJSON.error && response.responseJSON.error.value) {
                alert(response.responseJSON.error.value);
            } else {
                console.log(response);
                alert("Authentication error");
            }
        }
    });
}
