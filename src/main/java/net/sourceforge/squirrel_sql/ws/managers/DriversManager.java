package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

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

    public synchronized void saveAllDrivers() {
        webapp.savePreferences(PreferenceType.DRIVER_DEFINITIONS);
    }

    public SQLDriver updateDriver(final SQLDriver item, String id) throws ValidationException {

        logger.info("updateSQLDriver: " + item + ";" + id);
        // Load old values
        SQLDriver itemOld = getDriverById(id);

        logger.info("itemOld: " + itemOld);
        logger.info("itemOld.getJarFileNames(): " + Arrays.asList(itemOld.getJarFileNames()));

        // Replace old values with new ones
        // This may raise ValidationException if required attribute are missing
        itemOld.setName(item.getName());
        itemOld.setDriverClassName(item.getDriverClassName());
        itemOld.setUrl(item.getUrl());
        itemOld.setWebSiteUrl(item.getWebSiteUrl());

        if (item.getJarFileNames() == null || item.getJarFileNames().length == 0) {
            searchJDBCDriverInClasspath(itemOld);
        } else {
            itemOld.setJarFileNames(item.getJarFileNames());
        }

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

        if (item.getJarFileNames() == null || item.getJarFileNames().length == 0) {
            searchJDBCDriverInClasspath(itemNew);
        } else {
            itemNew.setJarFileNames(item.getJarFileNames());
        }

        searchJDBCDriverInClasspath(itemNew);

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

    /**
     * Search a class in current classpath.
     * 
     * No custom classloader is used.
     * 
     * @param className
     * @return
     */
    public Class<?> isClassInClasspath(String className) {
        if (className == null || className.trim().isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className.trim());
        } catch (ClassNotFoundException exc) {
            return null;
        }
    }

    /**
     * Set JDBCDriverClassLoaded, JarFileName, JarFileNames driver attributes.
     * 
     * @param driver
     */
    public void searchJDBCDriverInClasspath(SQLDriver driver) {

        // TODO search for equivalent SquirrelSql-core method

        driver.setJDBCDriverClassLoaded(false);
        driver.setJarFileName(null);
        driver.setJarFileNames(new String[] {});

        Class<?> clazz = isClassInClasspath(driver.getDriverClassName());
        if (clazz == null) {
            return;
        }
        URI jarURI;
        try {
            jarURI = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            logger.error("Exception while loading JDBC driver", e);
            return;
        } catch (SecurityException e) {
            logger.error("Exception while loading JDBC driver", e);
            return;
        }
        String jarFilename = new File(jarURI).getAbsolutePath();
        driver.setJarFileNames(new String[] { jarFilename });
        webapp.getAliasesAndDriversManager().refreshDriver(driver, NullMessageHandler.getInstance());
    }

    /**
     * Call searchJDBCDriverInClasspath() for all drivers, then save all of them
     */
    public void searchJDBCDriversInClasspath() {
        List<SQLDriver> list = getDrivers();
        for (SQLDriver driver : list) {
            searchJDBCDriverInClasspath(driver);
        }
        saveAllDrivers();
    }
}
