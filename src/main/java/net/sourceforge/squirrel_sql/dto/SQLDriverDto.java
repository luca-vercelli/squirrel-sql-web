package net.sourceforge.squirrel_sql.dto;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

public class SQLDriverDto {

    private ISQLDriver driver;

    public SQLDriverDto(ISQLDriver driver) {
        this.driver = driver;
    }

    @XmlTransient
    @JsonIgnore
    public ISQLDriver getDriver() {
        return driver;
    }

    public String getDriverClassName() {
        return driver.getDriverClassName();
    }

    public String getIdentifier() {
        return driver.getIdentifier().toString();
    }

    public String[] getJarFileNames() {
        return driver.getJarFileNames();
    }

    public String getName() {
        return driver.getName();
    }

    public String getUrl() {
        return driver.getUrl();
    }

    public String getWebSiteUrl() {
        return driver.getWebSiteUrl();
    }

    public boolean isJDBCDriverClassLoaded() {
        return driver.isJDBCDriverClassLoaded();
    }

    public void setDriverClassName(String clazz) throws ValidationException {
        driver.setDriverClassName(clazz);
    }

    public void setJDBCDriverClassLoaded(boolean loaded) {
        driver.setJDBCDriverClassLoaded(loaded);

    }

    public void setJarFileName(String jar) throws ValidationException {
        driver.setJarFileName(jar);
    }

    public void setJarFileNames(String[] jarFileNames) {
        driver.setJarFileNames(jarFileNames);
    }

    public void setName(String name) throws ValidationException {
        driver.setName(name);
    }

    public void setUrl(String url) throws ValidationException {
        driver.setUrl(url);
    }

    public void setWebSiteUrl(String url) throws ValidationException {
        driver.setWebSiteUrl(url);
    }

}
