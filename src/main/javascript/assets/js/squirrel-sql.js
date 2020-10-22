// this url works if every page is at same depth...
var ws_url = '../ws/';

var drivers = [];
var aliases = [];
var sessions = [];

$(document).ready(function(){
	$.getJSON(ws_url + 'Drivers', function(response){
		drivers = response.data;

		var menu = $('#ui-sub-menu-drivers').find('nav');
		menu.html("");
		for(var i in drivers) {
			var driver = drivers[i];
			createMenuEntry(menu, 'driver.html?name=' + driver.name, driver.name);
		};
		
		createMenuEntry(menu, 'driver.html', 'Create new');
	});
	

	$.getJSON(ws_url + 'Aliases', function(response){
		aliases = response.data;

		var menu = $('#ui-sub-menu-aliases').find('nav');
		menu.html("");
		for(var i in aliases) {
			var alias = aliases[i];
			createMenuEntry(menu, 'alias.html?name=' + alias.name, alias.name);
			menu.append(html);
		};
		
		createMenuEntry(menu, 'alias.html', 'Create new');
	});

	// TODO load sessions
});

function createMenuEntry(menu, url, caption) {
	var html = '<div class="mdc-list-item mdc-drawer-item" tabindex="-1">'+
    '<a class="mdc-drawer-link" href="'+url+'" tabindex="-1">'+caption+'</a></div>';
	menu.append(html);
}

