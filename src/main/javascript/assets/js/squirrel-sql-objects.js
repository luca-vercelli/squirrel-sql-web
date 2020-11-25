var objectsTree = null;

$(document).ready(function(){
    loadObjectsTree();
});

function loadObjectsTree() {
    var sessionId = location.href.split('#')[0].split("session.html?id=")[1];
    // session.identifier is not ready yet
    hideMessages();
    var url = (enable_mock) ? 
                ws_url + 'RootNode.json' :
                ws_url + `Session(${session.identifier})/RootNode`;
    $.ajax({
        type: 'GET',
        url: url,
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(data, status){
            objectsTree = data.value;
            displayObjectsTree()
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}

function displayObjectsTree() {
    $('#objects-tab').html('');
    _displayObjectsTree(objectsTree, $('#objects-tab'));
}

function _displayObjectsTree(node, parentDiv) {
    var childDiv = $(`<div id="${node.simpleName}><span>${node.simpleName}</span></div>`).appendTo(parentDiv);
    for (var i in node.children) {
        _displayObjectsTree(node.children[i], childDiv);
    }
}

function expandTreeNode(node) {
    var url = (enable_mock) ? 
                ws_url + 'ExpandNode.json' :
                ws_url + `Session(${session.identifier})/ExpandNode`;
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: JSON.stringify(node),
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(data, status){
            node.childrens = data.data;
            displayObjectsTree();
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}
