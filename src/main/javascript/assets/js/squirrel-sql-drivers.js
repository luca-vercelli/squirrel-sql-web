// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = true;

var driver = null;
var creating = true;

$(document).ready(function(){
    var driver_name = location.href.split("driver.html?name=")[1];
    if (driver_name) {
        // Updating existing driver
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleDriver.json' :
    			ws_url + 'Drivers/' + driver_name;
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
    
    $('#save_button').click(save_driver);
    $("#create_button").click(create_driver);
    $("#delete_button").click(delete_driver);
});

function create_driver() {
    disable_edit(true);
    load_driver_from_form();
    
    $.ajax({
        type: 'POST',
        url: ws_url + 'Drivers',
        contentType: 'application/json',
        data: driver,
        success: function(data, status){
            console.log("Data: " + data + "\nStatus: " + status);
            disable_edit(false);
        },
        error: function(data, status){
            console.log("ERROR! Data: " + data + "\nStatus: " + status);
            disable_edit(false);
        }
    });
}

function save_driver() {
    disable_edit(true);
    load_driver_from_form();
    
    $.ajax({
        type: 'PUT',
        url: ws_url + 'Drivers/' + driver.name,
        contentType: 'application/json',
        data: driver,
        success: function(data, status){
            console.log("Data: " + data + "\nStatus: " + status);
            disable_edit(false);
        },
        error: function(data, status){
            console.log("ERROR! Data: " + data + "\nStatus: " + status);
            disable_edit(false);
        }
    });
}

function delete_driver() {
    // TODO should give some warning
    $.ajax({
        type: 'DELETE',
        url: ws_url + 'Drivers/' + driver.name,
        success: function(data, status){
            console.log("Data: " + data + "\nStatus: " + status);
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("ERROR! Data: " + data + "\nStatus: " + status);
            disable_edit(false);
        }
    });
}

function load_driver_from_form() {
    
    driver.name = $("#driver_name").val().replace('&', '_');
    driver.url = $("#driver_url").val();
    driver.webSiteUrl = $("#driver_website_url").val();
    driver.driverClassName = $("#driver_class_name").val();
    
    // trigger change for Material UI
    $("#driver_name").trigger('change');
    $("#driver_url").trigger('change');
    $("#driver_website_url").trigger('change');
    $("#driver_class_name").trigger('change');
}

function update_driver_to_form() {
    $("#driver_name").val(driver.name);
    $("#driver_url").val(driver.url);
    $("#driver_website_url").val(driver.webSiteUrl);
    $("#driver_class_name").val(driver.driverClassName);
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
