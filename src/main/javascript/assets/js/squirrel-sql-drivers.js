// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;

var driver = null;
var creating = true;

$(document).ready(function(){
    var identifier = location.href.split("driver.html?id=")[1];
    if (identifier) {
        // Updating existing driver
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleDriver.json' :
    			ws_url + 'Drivers/' + identifier;
        $.getJSON(url, function(response){
            driver = response.value;
            if (driver != null) {
                update_driver_to_form();
                set_creating(false);
            } else {
                console.log("Requested driver not found!!!");
            }
        });
	} else {
        set_creating(true);
    }
    
    $('#save_button').click(saveDriver);
    $("#create_button").click(createDriver);
    $("#delete_button").click(deleteDriver);
});

function createDriver() {
    disable_edit(true);
    load_driver_from_form();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'Drivers',
        contentType: 'application/json',
        data: JSON.stringify(driver),
        success: function(data, status){
            driver = data.value;
            update_driver_to_form();
            disable_edit(false);
            // menu is not updated, nor it is the URL
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function saveDriver() {
    disable_edit(true);
    load_driver_from_form();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'PUT',
        url: ws_url + 'Drivers/' + driver.identifier,
        contentType: 'application/json',
        data: JSON.stringify(driver),
        success: function(data, status){
            driver = data.value;
            update_driver_to_form();
            disable_edit(false);
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function deleteDriver() {
    // TODO should give some warning
    $.ajax({
        type: enable_mock ? 'GET' : 'DELETE',
        url: ws_url + 'Drivers/' + driver.identifier,
        success: function(data, status){
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disable_edit(false);
        }
    });
}

function load_driver_from_form() {
    
    if (driver == null) driver = new Object();
    driver.name = document.querySelector('#mdc-driver-name').MDCTextField.value.replace('&', '_');
    driver.url = document.querySelector('#mdc-driver-url').MDCTextField.value;
    driver.webSiteUrl = document.querySelector('#mdc-website-url').MDCTextField.value;
    driver.driverClassName = document.querySelector('#mdc-driver-classname').MDCTextField.value;
}

function update_driver_to_form() {
    document.querySelector('#mdc-driver-name').MDCTextField.value = driver.name;
    document.querySelector('#mdc-driver-url').MDCTextField.value = driver.url;
    document.querySelector('#mdc-website-url').MDCTextField.value = driver.webSiteUrl;
    document.querySelector('#mdc-driver-classname').MDCTextField.value = driver.driverClassName;
}

function disable_edit(true_or_false) {
    
    // we use driver name as key
    $("#driver_name").prop('disabled', (!creating) && true_or_false);
    $("#driver_url").prop('disabled', true_or_false);
    $("#driver_website_url").prop('disabled', true_or_false);
    $("#driver_class_name").prop('disabled', true_or_false);
    // buttons
    $("#create_button").prop('disabled', true_or_false);
    $("#save_button").prop('disabled', true_or_false);
    $("#delete_button").prop('disabled', true_or_false);
}

function set_creating(true_or_false) {
    creating = true_or_false;
    if (creating) {
        $("#create_button").show();
        $("#save_button").hide();
        $("#delete_button").hide();
    } else {
        $("#create_button").hide();
        $("#save_button").show();
        $("#delete_button").show();
    }
}
