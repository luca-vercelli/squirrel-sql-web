// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;

var session = null;

$(document).ready(function(){
    loadForm();

    $("sql_button").click(executeQuery);
    $("#disconnect_button").click(disconnect);
});

function loadForm() {
    var identifier = location.href.split("session.html?id=")[1];
    if (identifier) {
        // Updating existing session
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleSession.json' :
    			ws_url + 'Sessions/' + identifier;
        $.getJSON(url, function(response){
            session = response.value;
            if (session) {
                disableEdit(false);
            } else {
                console.log("Requested session not found!!!");
            }
        });
	}
}

function disconnect() {
    // TODO should give some warning
    disableEdit(true);
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'Disconnect',
        data: { sessionId: session.identifier },
        success: function(data, status){
            console.log("Data: ", data, "Status:", status);
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}

function executeQuery() {
    disableEdit(true);
    console.log("Query:" + document.querySelector('#mdc-query').MDCTextField.value);
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'ExecuteQuery',
        data: {
            sessionId: session.identifier,
            query: document.querySelector('#mdc-query').MDCTextField.value
        },
        success: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}

function disableEdit(true_or_false) {    
    $("#sql_button").prop('disabled', true_or_false);
    $("#disconnect_button").prop('disabled', true_or_false);
}
