// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;

var alias = null;
var creating = true;

// pro-memoria
// if (a) {...}
// a = undefined/null/0/''      -> behave as false
// a = 1/-1/'xxx'/' '/[]/{}     -> behave as true
// (while in Python [] behaves as False)

$(document).ready(function(){
    drivers_callbacks.push(loadForm);
    
    $('#save_button').click(saveAlias);
    $("#create_button").click(createAlias);
    $("#delete_button").click(deleteAlias);
    $("#connect_button").click(connect);
    $('#mdc-driver').on('MDCSelect:change', changeDriverUrl);
});

/**
Load drivers menu options, then load alias
*/
function loadForm() {
    
    loadMenuOptions();

    var identifier = location.href.split("alias.html?id=")[1];
    if (identifier) {
        // Updating existing alias
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleAlias.json' :
    			ws_url + 'Aliases/' + identifier;
        $.getJSON(url, function(response){
            alias = response.value;
            
            if (alias != null) {
                update_alias_to_form();
                set_creating(false);
            } else {
                console.log("Requested alias not found!!!");
            }
        });
	} else {
        set_creating(true);
    }
}

function createAlias() {
    disable_edit(true);
    load_alias_from_form();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'Aliases',
        contentType: 'application/json',
        data: JSON.stringify(alias),
        success: function(data, status){
            alias = data.value;
            update_alias_to_form();
            disable_edit(false);
            set_creating(false);
            // menu is not updated, nor it is the URL
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function saveAlias() {
    disable_edit(true);
    load_alias_from_form();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'PUT',
        url: ws_url + 'Aliases/' + alias.identifier,
        contentType: 'application/json',
        data: JSON.stringify(alias),
        success: function(data, status){
            alias = data.value;
            update_alias_to_form();
            disable_edit(false);
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function deleteAlias() {
    // TODO should give some warning
    $.ajax({
        type: enable_mock ? 'GET' : 'DELETE',
        url: ws_url + 'Aliases/' + alias.identifier,
        success: function(data, status){
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function connect() {
    // FIXME user and password could not be saved into the alias
    var url = (enable_mock) ? 
            ws_url_mock + 'SingleSession.json' :
            ws_url + 'Connect';
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: url,
        data: {
            aliasIdentifier: alias.identifier,
            userName: $("#alias_user").val(),
            password: $("#alias_password").val()
        },
        success: function(data, status){
            console.log("Data: ", data, "Status:", status);
            window.location.replace("session.html?id=" + data.value.identifier);
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function load_alias_from_form() {
    
    if (alias == null) alias = new Object();
    alias.name = document.querySelector('#mdc-alias-name').MDCTextField.value.replace('&', '_');
    alias.driverIdentifier = document.querySelector('#mdc-driver').MDCSelect.value;
    alias.url = document.querySelector('#mdc-alias-url').MDCTextField.value;
    alias.userName = document.querySelector('#mdc-alias-user').MDCTextField.value;
    alias.password = document.querySelector('#mdc-alias-password').MDCTextField.value;
    alias.autoLogon = document.querySelector('#mdc-autologon').MDCSwitch.checked;
    alias.connectAtStartup = document.querySelector('#mdc-autoconnect').MDCSwitch.checked;
    alias.encryptPassword = document.querySelector('#mdc-encrypted').MDCSwitch.checked;
}

function update_alias_to_form() {
    // PRE: alias != null

    // here is a pair of properties that server should not set
    alias.driverPropertiesClone = undefined;
    alias.valid = undefined;

    document.querySelector('#mdc-alias-name').MDCTextField.value = alias.name;
    document.querySelector('#mdc-alias-url').MDCTextField.value = alias.url;
    document.querySelector('#mdc-alias-user').MDCTextField.value = alias.userName;
    document.querySelector('#mdc-alias-password').MDCTextField.value = alias.password;
    // MDC switches
    document.querySelector('#mdc-autologon').MDCSwitch.checked = alias.autoLogon;
    document.querySelector('#mdc-autoconnect').MDCSwitch.checked = alias.connectAtStartup;
    document.querySelector('#mdc-encrypted').MDCSwitch.checked = alias.encryptPassword;
    // MDC select
    document.querySelector('#mdc-driver').MDCSelect.value = alias.driverIdentifier;
}

function disable_edit(true_or_false) {
    
    // we use alias name as key
    $("#alias_name").prop('disabled', (!creating) && true_or_false);
    $("#alias_driver").prop('disabled', true_or_false);
    $("#alias_user").prop('disabled', true_or_false);
    $("#alias_password").prop('disabled', true_or_false);
    $("#alias_autologon").prop('disabled', true_or_false);
    $("#alias_autoconnect").prop('disabled', true_or_false);
    $("#alias_somemore").prop('disabled', true_or_false);
    // buttons
    $("#create_button").prop('disabled', true_or_false);
    $("#save_button").prop('disabled', true_or_false);
    $("#delete_button").prop('disabled', true_or_false);
}

function set_creating(true_or_false) {
    creating = true_or_false;
    if (creating) {
        $("#create_button").show();
        $("#connect_button").hide();
        $("#save_button").hide();
        $("#delete_button").hide();
    } else {
        $("#create_button").hide();
        $("#connect_button").show();
        $("#save_button").show();
        $("#delete_button").show();
    }
}

/**
 * Populate drivers drop-down menu
 */
function loadMenuOptions() {
    var select = $("#ul-for-alias-driver");
    $(".driver-item").remove();
    for(var i in drivers) {
        var driver = drivers[i];
        createSelectOption(select, driver.name, driver.identifier);
    };
    
    if (alias && alias.driverIdentifier) {
        document.querySelector('#mdc-driver').MDCSelect.value = alias.driverIdentifier;
    }
}

function createSelectOption(select, caption, value) {
    select.append('<li class="mdc-list-item driver-item" data-value="'+value+'">'+caption+'</li>');
}

/**
 * Copy driver url into alias url on the form
 */
function changeDriverUrl() {
    if (alias.driverIdentifier != document.querySelector('#mdc-driver').MDCSelect.value) {
        load_alias_from_form();
        if (alias.driverIdentifier) {
            var driver = getDriverByIdentifier(alias.driverIdentifier);
            if (driver) {
                alias.url = driver.url;
                document.querySelector('#mdc-alias-url').MDCTextField.value = alias.url;
            } else {
                console.log("Driver not found: " + alias.driverIdentifier);
            }
        }
    }
}
