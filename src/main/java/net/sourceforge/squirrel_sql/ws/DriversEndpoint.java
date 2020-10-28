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

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;

@Path("/")
@Stateless
public class DriversEndpoint {

	@Inject
	WebApplication webapp;

	Logger logger = Logger.getLogger(DriversEndpoint.class);

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
	public void createItem(SQLDriver item) throws ValidationException {

		// Check compulsory attributes
		checkCompulsoryAttributes(item);

		boolean driverInClasspath = isDriverInClasspath(item.getDriverClassName());
		item.setJDBCDriverClassLoaded(driverInClasspath);

		// Assign new id
		IIdentifier id = IdentifierFactory.getInstance().createIdentifier();
		item.setIdentifier(id);

		// add driver to managed drivers list
		getManager().addDriver(item, null);

		// save all drivers
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
	}

	@PUT
	@Path("/Drivers/{identifier}")
	public SQLDriver updateItem(@PathParam("identifier") String stringId, SQLDriver item) throws ValidationException {

		// Check compulsory attributes
		checkCompulsoryAttributes(item);

		// Check if JDBC class is in classpath
		boolean driverInClasspath = isDriverInClasspath(item.getDriverClassName());

		// Load old values
		SQLDriver itemOld = (SQLDriver) get(stringId);

		// Replace old values with new ones
		itemOld.setName(item.getName());
		itemOld.setDriverClassName(item.getDriverClassName());
		itemOld.setUrl(item.getUrl());
		itemOld.setWebSiteUrl(item.getWebSiteUrl());
		itemOld.setJDBCDriverClassLoaded(driverInClasspath);

		// save all drivers
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
		return itemOld;
	}

	private void checkCompulsoryAttributes(SQLDriver item) {
		if (item.getName() == null || item.getName().isEmpty() || item.getDriverClassName() == null
				|| item.getDriverClassName().isEmpty()) {
			throw new IllegalArgumentException("Missing driver name or JDBC class");
		}
	}

	@DELETE
	@Path("/Drivers/{identifier}")
	public void deleteItem(@PathParam("identifier") String stringId) {

		// retrieve driver
		ISQLDriver item = get(stringId);

		// remove driver from managed drivers list
		getManager().removeDriver(item);

		// save all drivers
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
	}

	@POST
	@Path("/CheckAllDrivers")
	public void checkAllDrivers() {
		List<SQLDriver> list = getManager().getDriverList();
		for (SQLDriver driver : list) {
			boolean loaded = driver.getDriverClassName() != null && !driver.getDriverClassName().isEmpty()
					&& isDriverInClasspath(driver.getDriverClassName());
			driver.setJDBCDriverClassLoaded(loaded);
		}

		// save all drivers
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

	private boolean isDriverInClasspath(String driverClassName) {
		boolean driverInClasspath;
		try {
			Class.forName(driverClassName);
			driverInClasspath = true;
		} catch (ClassNotFoundException exc) {
			driverInClasspath = false;
		}
		return driverInClasspath;
	}
}
