// this url works if every page is at same depth...
var ws_url = '../ws/';
var ws_url_mock = '../mock/'
var enable_mock = false;

var drivers = [];
var aliases = [];
var sessions = [];

// functions to execute when driver have loaded
var drivers_callbacks = [];

var current_href = location.href.split("/").splice(-1)[0];
if (current_href.endsWith('#')) {
	current_href = current_href.substring(0, current_href.length-1);
}
	
$(document).ready(function(){
	loadDrivers();
	loadAliases();
	loadSessions();
	// TODO which drivers are available in classpath?
});

/**
* Load drivers, and also perform security redirect
*/
function loadDrivers() {
	var url = (enable_mock) ? 
			ws_url_mock + 'Drivers.json' :
			ws_url + 'Drivers';
	$.ajax({
        url: url,
        dataType: "json",
        success: function(response){
            drivers = response.data;
            drivers.sort(cmpNames);
            var menu = $('#ui-sub-menu-drivers').find('nav');
            menu.html("");
            createMenuEntry(menu, 'driver.html', 'Create new');
            var checkIcon = ' <i class="material-icons mdc-list-item__start-detail mdc-drawer-item-icon" aria-hidden="true">check</i>';
            for(var i in drivers) {
                var driver = drivers[i];
                if (driver.identifier != null) {
                    var isChecked = driver.jdbcdriverClassLoaded ? checkIcon : ''
                    createMenuEntry(menu, 'driver.html?id=' + driver.identifier, driver.name + isChecked);
                } else {
                    console.log(`Skipping entry ${driver.name} with no identifier`); // should not happen
                }
            };
            
            for (i in drivers_callbacks) {
                drivers_callbacks[i]();
            }
        },
        error: function(a,b,c){
            console.log(a, b, c);
            if (a.status == 403) {
                window.location.replace("login.html");
            }
        }
    });
}

function loadAliases() {
	var url = (enable_mock) ? 
			ws_url_mock + 'Aliases.json' :
			ws_url + 'Aliases';
	$.getJSON(url, function(response){
		aliases = response.data;
		aliases.sort(cmpNames);
		var menu = $('#ui-sub-menu-aliases').find('nav');
		menu.html("");
		createMenuEntry(menu, 'alias.html', 'Create new');
		for(var i in aliases) {
			var alias = aliases[i];
			if (alias.identifier != null) {
				createMenuEntry(menu, 'alias.html?id=' + alias.identifier, alias.name);
			} else {
				console.log(`Skipping entry ${alias.name} with no identifier`); // should not happen
			}
		};
	});
}

function loadSessions() {
	var url = (enable_mock) ? 
			ws_url_mock + 'Sessions.json' :
			ws_url + 'Sessions';
	$.getJSON(url, function(response){
		sessions = response.data;
		sessions.sort(cmpTitles);
		var menu = $('#ui-sub-menu-sessions').find('nav');
		menu.html("");
		if (sessions.length > 0) {
			for(var i in sessions) {
				var session = sessions[i];
				if (session.identifier != null) {
					createMenuEntry(menu, 'session.html?id=' + session.identifier, session.title);
				} else {
					console.log(`Skipping entry ${session.name} with no identifier`); // should not happen
				}
			};
		} else {
			createMenuEntry(menu, '#', 'No open sessions.');
		}
	});
}

function cmpNames(x, y){
	var name1 = x.name ? x.name.toLowerCase() : '';
	var name2 = y.name ? y.name.toLowerCase() : '';
	return name1 < name2 ? -1 : name1 == name2 ? 0 : +1;
}

function cmpTitles(x, y){
	var name1 = x.title ? x.title.toLowerCase() : '';
	var name2 = y.title ? y.title.toLowerCase() : '';
	return name1 < name2 ? -1 : name1 == name2 ? 0 : +1;
}

function createMenuEntry(menu, url, caption) {
	// something like misc.js
	var active = "";
	if (url.endsWith(current_href)) {
		  active = ' active';
		  menu.closest('.mdc-expansion-panel').addClass('expanded');
		  menu.closest('.mdc-expansion-panel').show();
		}
	
	var html = '<div class="mdc-list-item mdc-drawer-item" tabindex="-1">'+
	'<a class="mdc-drawer-link' + active +'" href="'+url+'" tabindex="-1">'+caption+'</a></div>';
	menu.append(html);
}

function getDriverByIdentifier(id) {
	for (var i in drivers) {
		if (drivers[i].identifier == id) {
			return drivers[i];
		}
	}
	return null;
}
