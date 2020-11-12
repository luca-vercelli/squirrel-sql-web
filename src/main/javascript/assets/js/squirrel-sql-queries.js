
function executeQuery() {
    disableEdit(true);
    hideMessages();
    hideResults();
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
                renderTable(data.value)
            }
            disableEdit(false);
        },
        error: function(response, status){
            showAjaxError(response);
            disableEdit(false);
        }
    });
}

function hideResults() {
    $('#resultsPanel').hide();
}

function showResults() {
    $('#resultsPanel').show();
}

function renderTable(table) {
    console.log("Table: ", table);

    // TODO should know data type, so we could add class="text-left" to text fields

    var thead = $('#resultsPanel').find("thead");
    thead.html("");
    renderColumnHeaders(table.columnHeaders, thead);

    var tbody = $('#resultsPanel').find("tbody");
    tbody.html("");
    for (i in table.rows) {
        renderRow(table.rows[i], tbody);
    }
    
    showResults();
}

function renderColumnHeaders(columnHeaders, thead) {
    var tr = $("<tr/>").appendTo(thead);
    for (j in columnHeaders) {
        var th = $("<th/>").appendTo(tr);
        th.html(columnHeaders[j]);
    }
}

function renderRow(row, tbody) {
    var tr = $("<tr/>").appendTo(tbody);
    for (j in row) {
        var td = $("<td/>").appendTo(tr);
        td.html(row[j]);
    }
}
