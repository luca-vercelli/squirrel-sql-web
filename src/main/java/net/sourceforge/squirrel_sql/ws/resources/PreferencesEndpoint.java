package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.ws.managers.PreferencesManager;

@Path("/")
@Stateless
public class PreferencesEndpoint {

    @Inject
    PreferencesManager manager;

    @GET
    @Path("/Preferences")
    public ValueBean<SquirrelPreferences> getItems() {
        SquirrelPreferences prefs = manager.get();
        return new ValueBean<>(prefs);
    }

    @PUT
    @Path("/Preferences")
    public ValueBean<SquirrelPreferences> update(SquirrelPreferences prefs) throws ValidationException {
        prefs = manager.update(prefs);
        return new ValueBean<>(prefs);
    }

}
