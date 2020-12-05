
var sessionId = null;
var catalog = null;
var schema = null;
var tableType = null;
var tableName = null;

$(document).ready(function(){
    loadParams();
    $("#table_content_button").on('click', loadTableContent);
    $("#row_count_button").on('click', loadTableRowCount);
    $("#table_pk_button").on('click', loadTablePk);
});

function loadParams() {
    var params = location.href.split('#')[0].split("table.html?")[1].split("&");
    for (var i in params) {
        var pieces = params[i].split("=");
        eval(pieces[0] + "='" + pieces[1] + "'");
    }
    
    if (sessionId && tableName) {
        $('#table-title').html(tableName);
    } else {
        showMessage("Bad request: missing parameters", "error");
    }
}

function loadTableContent() {
    _commonLoadDetails("TableContent");
}

function loadTableRowCount() {
    _commonLoadDetails("TableRowCount");
}

function loadTablePk() {
    _commonLoadDetails("TablePk");
}

function _commonLoadDetails(endpoint) {
    disableEdit(true);
    var url = (enable_mock) ? 
                ws_url + 'ExecuteQuery.json' :
                ws_url + `Session(${sessionId})/${endpoint}`;
    $.ajax({
        type: 'GET',
        url: url,
        data: {
            catalog: catalog,
            schema: schema,
            tableName: tableName,
            tableType: tableType
        },
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(data, status){
            renderDataSet(data.value);
            disableEdit(false);
        },
        error: function(response, status){
            showAjaxError(response);
            disableEdit(false);
        }
    });
}

function disableEdit(true_or_false) {    
    $(".table_button").prop('disabled', true_or_false);
}
