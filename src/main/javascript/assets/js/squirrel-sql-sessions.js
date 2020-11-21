
var session = null;
var tableTree = {};

$(document).ready(function(){
    loadForm();

    $("#sql_button").click(executeQuery);
    $("#disconnect_button").click(disconnect);
});

function loadForm() {
    var identifier = location.href.split('#')[0].split("session.html?id=")[1];
    if (identifier) {
        // Updating existing session
        var url = (enable_mock) ? 
                ws_url + 'SingleSession.json' :
                ws_url + `Sessions(${identifier})`;
        $.ajax({
            url: url,
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('authToken')
            },
            success: function(response){
                session = response.value;
                if (session) {
                    $('#session-title').html(session.title);
                    disableEdit(false);
                } else {
                    showMessage(data, "error");
                }
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
                ws_url + 'JustGetOk' :
                ws_url + 'Disconnect';
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: {
            sessionId: session.identifier
        },
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
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
                ws_url + 'SchemaInfo.json' :
                ws_url + `Sessions(${session.identifier})/SchemaInfo`;
    $.ajax({
        type: 'GET',
        url: url,
        data: {
            sessionId: session.identifier
        },
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
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
