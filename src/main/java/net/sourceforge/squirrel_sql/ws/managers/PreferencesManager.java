package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

/**
 * Manages preferences on XML database
 * 
 * @author lv 2020
 *
 */
@Stateless
public class PreferencesManager {

	@Inject
	WebApplication webapp;
	@Inject
	DriversManager driversManager;

	public SquirrelPreferences get() {
		return webapp.getSquirrelPreferences();
	}

	public SquirrelPreferences update(SquirrelPreferences prefs) {

		// TODO set properties

		webapp.savePreferences(PreferenceType.DATATYPE_PREFERENCES);
		return prefs;
	}

}
