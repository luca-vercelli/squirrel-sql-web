package net.sourceforge.squirrel_sql.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;

@Path("/")
@Stateless
public class DriversEndpoint {

	@Inject
	WebApplication webapp;

	// We cannot inject the manager, as it is not an EJB
	// Instead, we inject the WebApplication and retrieve the manager
	private AliasesAndDriversManager getManager() {
		return webapp.getAliasesAndDriversManager();
	}

	@GET
	@Path("/Drivers")
	public ListBean<SQLDriver> getItems() {
		List<SQLDriver> list = getManager().getDriverList();
		long count = list.size();
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/Drivers/{identifier}")
	public ValueBean<ISQLDriver> getItem(@PathParam("identifier") String stringId) {
		ISQLDriver item = get(stringId);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Drivers")
	public void createItem(SQLDriver item) {
		getManager().addDriver(item, null);
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
	}

	@PUT
	@Path("/Drivers/{identifier}")
	public void updateItem(@PathParam("identifier") String stringId, SQLDriver item) {
		// TODO
	}

	@DELETE
	@Path("/Drivers/{identifier}")
	public void deleteItem(@PathParam("identifier") String stringId) {
		ISQLDriver item = get(stringId);
		getManager().removeDriver(item);
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
	}

	private UidIdentifier createId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
	
	private ISQLDriver get(String stringId) {
		UidIdentifier id = createId(stringId);
		return getManager().getDriver(id);
	}
}
