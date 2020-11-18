
var enable_mock = false;
var ws_url = enable_mock ? '../mock/' : '../ws/';

var user = null;

$(document).ready(function(){
    if (location.href.indexOf("login.html") < 0) {
        // We cannot load user before login
        loadUser();
    }

    $('#login-button').on('click', login);
    $('#logout-button').on('click', logout);
});

function login() {
    if (enable_mock) {
        window.location.replace("index.html");
        return;
    }

    localStorage.removeItem('authToken');

    var url = ws_url + 'Authenticate';
    $.ajax({
        url: url,
        type: enable_mock ? 'GET' : 'POST',
        data: {
            username: document.querySelector('#username').MDCTextField.value,
            password: document.querySelector('#password').MDCTextField.value
        },
        success: function(response){
            var token = response;
            // we store the token in localStorage, because this is not a Single Page App
            // is this safe?
            localStorage.setItem('authToken', token);
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

function logout() {    
    if (enable_mock) {
        window.location.replace("login.html");
        return;
    }
    
    var url = ws_url + `DisconnectAllSessions`;
    $.ajax({
        url: url,
        type: enable_mock ? 'GET' : 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(response){
            localStorage.removeItem('authToken');
            window.location.replace("login.html");
        },
        error: function(response){
            console.log(response);
            localStorage.removeItem('authToken');
            window.location.replace("login.html");
        }
    });
}

function loadUser() {
    var url = (enable_mock) ? 
            ws_url + 'CurrentUser.json' :
            ws_url + 'CurrentUser';
    $.ajax({
        url: url,
        dataType: "json",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(response){
            user = response.value;
            $('.user-info .name').html(user.surname + ' ' + user.name);
            $('.user-info .email').html(user.email);
        },
        error: function(response){
            if (response.status == 401 || response.status == 403){
                window.location.replace("login.html");
            } else {
                console.log(response);
            }
        }
    });
}
