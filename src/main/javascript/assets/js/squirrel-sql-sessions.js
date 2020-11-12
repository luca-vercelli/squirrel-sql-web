// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;

var session = null;
var tableTree = {};

$(document).ready(function(){
    loadForm();

    $("#sql_button").click(executeQuery);
    $("#disconnect_button").click(disconnect);
});

function loadForm() {
    var identifier = location.href.split("session.html?id=")[1];
    if (identifier) {
        // Updating existing session
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleSession.json' :
    			ws_url + `Sessions(${identifier})`;
        $.getJSON(url, function(response){
            session = response.value;
            if (session) {
                disableEdit(false);
            } else {
                showMessage(data, "error");
            }
        });
	} else {
        showMessage("Bad request: no session identifier given", "error");
    }
}

function disconnect() {
    // TODO should give some warning
    disableEdit(true);
    var url = (enable_mock) ? 
                ws_url_mock + 'JustGetOk' :
                ws_url + 'Disconnect';
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: { sessionId: session.identifier },
        success: function(data, status){
            window.location.replace("..");
        },
        error: function(response, status){
            showAjaxError(response);
            disableEdit(false);
        }
    });
}

function getCatalogs() {
    var url = (enable_mock) ? 
                ws_url_mock + 'SchemaInfo.json' :
                ws_url + `Sessions(${session.identifier})/SchemaInfo`;
    $.ajax({
        type: 'GET',
        url: url,
        data: { sessionId: session.identifier },
        success: function(data, status){
            console.log("Data: ", data, "Status:", status);
            var schemaInfo = data.value;
            // ASSOCIATION CATALOGS/SCHEMAS???
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}

function disableEdit(true_or_false) {    
    $("#sql_button").prop('disabled', true_or_false);
    $("#disconnect_button").prop('disabled', true_or_false);
}
