package net.sourceforge.squirrel_sql.ws.resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.ws.managers.DriversManager;

@Path("/")
@Stateless
public class DriversEndpoint {

	@Inject
	DriversManager manager;

	@GET
	@Path("/Drivers")
	public ListBean<SQLDriver> getItems() {
		List<SQLDriver> list = manager.getDrivers();
		// If 0, may raise HTTP 404
		return new ListBean<>(list);
	}

	@GET
	@Path("/Drivers({identifier})")
	public ValueBean<ISQLDriver> getItem(@PathParam("identifier") String stringId) {
		ISQLDriver item = manager.getDriverById(stringId);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Drivers")
	public ValueBean<SQLDriver> createItem(SQLDriver item) throws ValidationException {
		item = manager.createNewDriver(item);
		return new ValueBean<>(item);
	}

	@PUT
	@Path("/Drivers({identifier})")
	public ValueBean<SQLDriver> updateItem(@PathParam("identifier") String stringId, SQLDriver item)
			throws ValidationException {
		item = manager.updateDriver(item, stringId);
		return new ValueBean<>(item);
	}

	@DELETE
	@Path("/Drivers({identifier})")
	public void deleteItem(@PathParam("identifier") String stringId) {
		manager.removeDriver(stringId);
	}

	@POST
	@Path("/CheckAllDrivers")
	public void checkAllDrivers() {
		manager.searchJDBCDriversInClasspath();
	}
}
