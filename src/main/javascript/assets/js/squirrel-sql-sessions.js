// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = true;

var session = null;

$(document).ready(function(){
    var identifier = location.href.split("session.html?id=")[1];
    if (identifier) {
        // Updating existing session
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleSession.json' :
    			ws_url + 'Sessions/' + identifier;
        $.getJSON(url, function(response){
            session = response.value;
            if (session != null) {
                update_session_to_form();
            } else {
                console.log("Requested session not found!!!");
            }
        });
	}
    
    $("#disconnect_button").click(disconnect);
});

function disconnect() {
    // TODO should give some warning
    disable_edit(true);
    $.ajax({
        type: enable_mock ? 'GET' : 'DELETE',
        url: ws_url + 'Disconnect',
        data: { aliasIdentifier: session.identifier.string },
        success: function(data, status){
            console.log("Data: ", data, "Status:", status);
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function load_session_from_form() {
    
    /*session.name = document.querySelector('#mdc-session-name').MDCTextField.value.replace('&', '_');
    session.url = document.querySelector('#mdc-session-url').MDCTextField.value;
    session.webSiteUrl = document.querySelector('#mdc-website-url').MDCTextField.value;
    session.sessionClassName = document.querySelector('#mdc-session-classname').MDCTextField.value;*/
}

function update_session_to_form() {
    /*document.querySelector('#mdc-session-name').MDCTextField.value = session.name;
    document.querySelector('#mdc-session-url').MDCTextField.value = session.url;
    document.querySelector('#mdc-website-url').MDCTextField.value = session.webSiteUrl;
    document.querySelector('#mdc-session-classname').MDCTextField.value = session.sessionClassName;*/
}

function disable_edit(true_or_false) {
    
    // we use session name as key
    /*$("#session_name").prop('disabled', (!creating) && true_or_false);
    $("#session_url").prop('disabled', true_or_false);
    $("#session_website_url").prop('disabled', true_or_false);
    $("#session_class_name").prop('disabled', true_or_false);
    // buttons
    $("#create_button").prop('disabled', true_or_false);
    $("#save_button").prop('disabled', true_or_false);
    $("#delete_button").prop('disabled', true_or_false);*/
}
