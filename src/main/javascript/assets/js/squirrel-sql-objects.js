var objectsTree = null;
var nodes = {};

$(document).ready(function(){
    loadObjectsTree();
    $(document).on('click', '.content', toggleTreeNode);
    $("#refresh_button").on('click', loadObjectsTree);
});

/**
* (Re)load root node of the tree
*/
function loadObjectsTree() {
    objectsTree = null;
    var sessionId = location.href.split('#')[0].split("session.html?id=")[1];
    // session.identifier is not ready yet
    hideMessages();
    var url = (enable_mock) ? 
                ws_url + 'RootNode.json' :
                ws_url + `Session(${sessionId})/RootNode`;
    $.ajax({
        type: 'GET',
        url: url,
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(data, status){
            objectsTree = data.value;
            objectsTree.expanded = true;
            displayObjectsTree()
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}

/**
 * (Re)draw whole objects tree
 */
function displayObjectsTree() {
    $('#objects-tree').html('');
    _displayObjectsTree(objectsTree, $('#objects-tree'), '');
}

function _displayObjectsTree(node, parentDiv, parentPath) {
    var path = parentPath + '/' + node.simpleName
    nodes[path] = node;
    var icon = (node.objectType == null || node.objectType == "TABLE") ? "" : node.expanded ? "remove" : "add";
    var s = `<div id="${node.simpleName}" class="objects-tree-node" data-object-path="${path}"><span class="material-icons" aria-hidden="true">${icon}</span><span>${node.simpleName}</span></div>`;
    var childDiv = $(s).appendTo(parentDiv);
    if (node.expanded) {
        for (var i in node.children) {
            _displayObjectsTree(node.children[i], childDiv, path);
        }
    }
}

/**
 * Toggle expansion of a single node
 */
function toggleTreeNode(evt) {
    var elm = $(evt.target).closest('div .objects-tree-node');
    var path = elm.attr('data-object-path');
    var node = nodes[path];
    
    if (node.objectType == null || node.objectType == "TABLE" || node.objectType == "VIEW") {
        var newUrl = location.href.split('#')[0].split("session.html")[0];
        newUrl += `table.html?sessionId=${session.identifier}&catalog=${node.catalog}&schema=${node.schemaName}&table=${node.simpleName}`;
        window.open(newUrl, '_blank');
        return;
    }
    //TODO handle procedures, triggers, udts, ...
    if (node.objectType == null) {
        return;
    }

    if (node.expanded === undefined) {
        _expandTreeNode(node);
    } else {
        node.expanded = !node.expanded;
        displayObjectsTree();
    }
}

function _expandTreeNode(node) {
    var url = (enable_mock) ? 
                ws_url + 'ExpandNode.json' :
                ws_url + `Session(${session.identifier})/ExpandNode`;
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(node),
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function(data, status){
            node.children = data.data;
            node.expanded = true;
            displayObjectsTree();
        },
        error: function(response, status){
            showAjaxError(response);
        }
    });
}