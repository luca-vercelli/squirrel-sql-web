// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;


function executeQuery() {
    disableEdit(true);
    hideMessages();
    var query = document.querySelector('#mdc-query').MDCTextField.value;
    console.log("Query:" + query);
    var url = (enable_mock) ? 
                ws_url_mock + 'ExecuteQuery.json' :
                ws_url + 'ExecuteQuery';
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: {
            sessionId: session.identifier,
            query: query
        },
        success: function(data, status){
            if (data.value == null) {
                // not a SELECT
                showMessage("Success.", "success");
            } else {
                renderTable(data)
            }
            disableEdit(false);
        },
        error: function(response, status){
            showAjaxError(response);
            disableEdit(false);
        }
    });
}

function renderTable(data) {
    console.log("Data: ", data);
}
