package net.sourceforge.squirrel_sql.ws;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
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
	@Path("/Drivers/{name}")
	public ValueBean<SQLDriver> getItem(@PathParam("name") String name) {
		List<SQLDriver> items = getManager().getDriverList().stream().filter(x -> x.getName().equals(name))
				.collect(Collectors.toList());
		SQLDriver object = items.isEmpty() ? null : items.get(0);
		// If null, may raise HTTP 404
		return new ValueBean<>(object);
	}

	@POST
	@Path("/Drivers")
	public void createItem(SQLDriver item) {
		getManager().addDriver(item, null);
		// should call saveDrivers() ?
	}

	@PUT
	@Path("/Drivers/{name}")
	public void updateItem(@PathParam("name") String name, SQLDriver item) {
		// TODO
	}

	@DELETE
	@Path("/Drivers/{name}")
	public void deleteItem(@PathParam("name") String name, SQLDriver item) {
		getManager().removeDriver(item);
		// should call saveDrivers() ?
	}
}
