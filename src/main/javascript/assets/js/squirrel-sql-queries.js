
function executeQuery() {
    disableEdit(true);
    hideMessages();
    hideResults();
    var query = document.querySelector('#mdc-query').MDCTextField.value;
    console.log("Query:" + query);
    var url = (enable_mock) ? 
                ws_url + 'ExecuteQuery.json' :
                ws_url + 'ExecuteQuery';
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: {
            sessionId: session.identifier,
            query: query
        },
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
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

function renderDataSet(ds) {
    console.log("IDataSet: ", ds);

    // TODO should know data type, so we could add class="text-left" to text fields

    var thead = $('#resultsPanel').find("thead");
    thead.html("");
    renderColumnHeadersDataSet(ds.dataSetDefinition.columnDefinitions, thead);

    var tbody = $('#resultsPanel').find("tbody");
    tbody.html("");
    for (i in ds.allDataForReadOnly) {
        renderRow(ds.allDataForReadOnly[i], tbody);
    }
    
    showResults();
}

function renderColumnHeadersDataSet(columnDefinitions, thead) {
    var tr = $("<tr/>").appendTo(thead);
    for (j in columnDefinitions) {
        var th = $("<th/>").appendTo(tr);
        th.html(columnDefinitions[j].columnHeading);
        // TODO add type description
    }
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

/**
* @see https://stackoverflow.com/a/18197341/5116356
*/
function download(filename, text) {
  var element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  element.setAttribute('download', filename);
  element.style.display = 'none';
  document.body.appendChild(element);
  element.click();
  document.body.removeChild(element);
}

function downloadSql() {
	var sql = document.querySelector('#mdc-query').MDCTextField.value;
	download("Query.sql", sql);
}
