var objectsTree = null;
var nodes = {};

$(document).ready(function(){
    loadObjectsTree();
    $(document).on('click', '.content', expandTreeNode);
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
    console.log(objectsTree);
    $('#objects-tab').html('');
    _displayObjectsTree(objectsTree, $('#objects-tab'), '');
}

function _displayObjectsTree(node, parentDiv, parentPath) {
    var path = parentPath + '/' + node.simpleName
    nodes[path] = node;
    var s = `<div id="${node.simpleName}" class="objects-tree-node" data-object-path="${path}"><span>${node.simpleName} (${node.objectType})</span></div>`;
    var childDiv = $(s).appendTo(parentDiv);
    for (var i in node.children) {
        _displayObjectsTree(node.children[i], childDiv, path);
    }
}

function expandTreeNode(evt) {
    var elm = $(evt.target).closest('div .objects-tree-node');
    var path = elm.attr('data-object-path');
    var node = nodes[path];
    
    // TODO change behaviour according to node.objectType
    _expandTreeNode(node)
}

function _expandTreeNode(node) {
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
            node.children = data.data;
            displayObjectsTree();
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}
