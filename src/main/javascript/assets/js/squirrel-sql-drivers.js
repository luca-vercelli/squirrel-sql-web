
var driver = null;
var creating = true;

$(document).ready(function(){
    loadForm();

    $('#save_button').click(saveDriver);
    $("#create_button").click(createDriver);
    $("#delete_button").click(deleteDriver);
    $("#goto_website_button").click(gotoWebsite);
    $("#check_drivers_button").click(checkDrivers);
});

function loadForm() {
    var identifier = location.href.split("driver.html?id=")[1];
    if (identifier) {
        // Updating existing driver
    	var url = (enable_mock) ? 
    			ws_url_mock + 'SingleDriver.json' :
    			ws_url + `Drivers(${identifier})`;
        $.getJSON(url, function(response){
            driver = response.value;
            if (driver != null) {
                updateDriverToForm();
                setCreating(false);
            } else {
                console.log("Requested driver not found!!!");
            }
        });
	} else {
        setCreating(true);
    }
}

function createDriver() {
    disableEdit(true);
    loadDriverFromForm();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'Drivers',
        contentType: 'application/json',
        data: JSON.stringify(driver),
        success: function(data, status){
            driver = data.value;
            updateDriverToForm();
            disableEdit(false);
            setCreating(false);
            // menu is not updated, nor it is the URL
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}

function saveDriver() {
    disableEdit(true);
    loadDriverFromForm();
    
    $.ajax({
        type: enable_mock ? 'GET' : 'PUT',
        url: ws_url + `Drivers(${driver.identifier})`,
        contentType: 'application/json',
        data: JSON.stringify(driver),
        success: function(data, status){
            driver = data.value;
            updateDriverToForm();
            disableEdit(false);
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}

function deleteDriver() {
    // TODO should give some warning
    $.ajax({
        type: enable_mock ? 'GET' : 'DELETE',
        url: ws_url + `Drivers(${driver.identifier})`,
        success: function(data, status){
            window.location.replace("..");
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}

function loadDriverFromForm() {
    
    if (driver == null) driver = new Object();
    driver.name = document.querySelector('#mdc-driver-name').MDCTextField.value.replace('&', '_');
    driver.url = document.querySelector('#mdc-driver-url').MDCTextField.value;
    driver.webSiteUrl = document.querySelector('#mdc-website-url').MDCTextField.value;
    driver.driverClassName = document.querySelector('#mdc-driver-classname').MDCTextField.value;
}

function updateDriverToForm() {
    document.querySelector('#mdc-driver-name').MDCTextField.value = driver.name;
    document.querySelector('#mdc-driver-url').MDCTextField.value = driver.url;
    document.querySelector('#mdc-website-url').MDCTextField.value = driver.webSiteUrl;
    document.querySelector('#mdc-driver-classname').MDCTextField.value = driver.driverClassName;
}

function disableEdit(true_or_false) {
    
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

function setCreating(true_or_false) {
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

function gotoWebsite() {
    loadDriverFromForm();
    if (driver.webSiteUrl) {
        window.open(driver.webSiteUrl, "_blank")
    } else {
        // TODO disable button when needed...
        console.log("No website provided");
    }
}

function checkDrivers() {
    disableEdit(true);
    $.ajax({
        type: enable_mock ? 'GET' : 'POST',
        url: ws_url + 'CheckAllDrivers',
        success: function(data, status){
            window.location.reload();
        },
        error: function(data, status){
            console.log("Data: ", data, "Status:", status);
            disableEdit(false);
        }
    });
}
