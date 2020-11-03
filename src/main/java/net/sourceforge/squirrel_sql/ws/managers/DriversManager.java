package net.sourceforge.squirrel_sql.ws.managers;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;

/**
 * Manages drivers on XML database
 * 
 * @author lv 2020
 *
 */
@Stateless
public class DriversManager {

	@Inject
	WebApplication webapp;

	Logger logger = Logger.getLogger(DriversManager.class);

	public List<SQLDriver> getDrivers() {
		return webapp.getAliasesAndDriversManager().getDriverList();
	}

	public SQLDriver getDriverById(IIdentifier id) {
		return (SQLDriver) webapp.getAliasesAndDriversManager().getDriver(id);
	}

	public SQLDriver getDriverById(String id) {
		return getDriverById(getDriverIdentifier(id));
	}

	public IIdentifier getDriverIdentifier(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}

	public boolean isDriverInClasspath(String driverClassName) {
		boolean driverInClasspath;
		try {
			Class.forName(driverClassName);
			driverInClasspath = true;
		} catch (ClassNotFoundException exc) {
			driverInClasspath = false;
		}
		return driverInClasspath;
	}

	public synchronized void saveAllDrivers() {
		webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
	}

	public SQLDriver updateDriver(final SQLDriver item, String id) throws ValidationException {

		logger.info("updateSQLDriver: " + item + ";" + id);
		// Load old values
		SQLDriver itemOld = getDriverById(id);

		logger.info("itemOld: " + itemOld);

		// Replace old values with new ones
		// This may raise ValidationException if required attribute are missing
		itemOld.setName(item.getName());
		itemOld.setDriverClassName(item.getDriverClassName());
		itemOld.setUrl(item.getUrl());
		itemOld.setWebSiteUrl(item.getWebSiteUrl());

		// Check if JDBC class is in classpath
		boolean driverInClasspath = isDriverInClasspath(item.getDriverClassName());
		itemOld.setJDBCDriverClassLoaded(driverInClasspath);

		saveAllDrivers();

		return itemOld;
	}

	public SQLDriver createNewDriver(final SQLDriver item) throws ValidationException {

		logger.info("createNewDriver: " + item);
		SQLDriver itemNew = new SQLDriver(new UidIdentifier());
		logger.info("" + itemNew);

		// Set values
		// This may raise ValidationException if required attribute are missing
		itemNew.setName(item.getName());
		itemNew.setDriverClassName(item.getDriverClassName());
		itemNew.setUrl(item.getUrl());
		itemNew.setWebSiteUrl(item.getWebSiteUrl());

		boolean driverInClasspath = isDriverInClasspath(item.getDriverClassName());
		itemNew.setJDBCDriverClassLoaded(driverInClasspath);

		// add driver to managed drivers list
		webapp.getAliasesAndDriversManager().addDriver(itemNew, null);

		saveAllDrivers();

		return itemNew;
	}

	public SQLDriver removeDriver(String id) {
		SQLDriver item = getDriverById(id);
		webapp.getAliasesAndDriversManager().removeDriver(item);
		saveAllDrivers();
		return item;
	}

	public void searchJDBCDriversInClasspath() {
		List<SQLDriver> list = getDrivers();
		for (SQLDriver driver : list) {
			boolean loaded = driver.getDriverClassName() != null && !driver.getDriverClassName().isEmpty()
					&& isDriverInClasspath(driver.getDriverClassName());
			driver.setJDBCDriverClassLoaded(loaded);
		}
		saveAllDrivers();
	}
}
