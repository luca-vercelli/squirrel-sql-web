package net.sourceforge.squirrel_sql.dto;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasConnectionProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.jaxrs.JsonSerializable;

public class SQLAliasDto implements JsonSerializable {
    private SQLAlias alias;

    Logger logger = Logger.getLogger(SQLAliasDto.class);

    public SQLAliasDto(SQLAlias alias) {
        this.alias = alias;
    }

    public SQLAliasDto() {
        this(new SQLAlias());
    }

    @XmlTransient
    @JsonIgnore
    public SQLAlias getAlias() {
        return alias;
    }

    public String getDriverIdentifier() {
        return alias.getDriverIdentifier().toString();
    }

    public String getName() {
        return alias.getName();
    }

    public String getPassword() {
        return alias.getPassword();
    }

    public String getUrl() {
        return alias.getUrl();
    }

    public boolean getUseDriverProperties() {
        return alias.getUseDriverProperties();
    }

    public String getUserName() {
        return alias.getUserName();
    }

    public boolean isAutoLogon() {
        return alias.isAutoLogon();
    }

    public boolean isConnectAtStartup() {
        return alias.isConnectAtStartup();
    }

    public boolean isEncryptPassword() {
        return alias.isEncryptPassword();
    }

    public void setAutoLogon(boolean value) {
        alias.setAutoLogon(value);
    }

    public void setConnectAtStartup(boolean value) {
        alias.setConnectAtStartup(value);
    }

    public void setDriverIdentifier(String stringId) throws ValidationException {
        UidIdentifier id = new UidIdentifier();
        id.setString(stringId);
        alias.setDriverIdentifier(id);
    }

    public void setEncryptPassword(boolean value) {
        alias.setEncryptPassword(value);
    }

    public void setName(String value) throws ValidationException {
        alias.setName(value);
    }

    public void setPassword(String value) throws ValidationException {
        alias.setPassword(value);
    }

    public void setUrl(String value) throws ValidationException {
        alias.setUrl(value);
    }

    public void setUseDriverProperties(boolean value) {
        alias.setUseDriverProperties(value);
    }

    public void setUserName(String value) throws ValidationException {
        alias.setUserName(value);
    }

    public String getIdentifier() {
        return alias.getIdentifier().toString();
    }

    public SQLAliasColorProperties getColorProperties() {
        return alias.getColorProperties();
    }

    public SQLAliasConnectionProperties getConnectionProperties() {
        return alias.getConnectionProperties();
    }

    public SQLAliasSchemaProperties getSchemaProperties() {
        return alias.getSchemaProperties();
    }

    public void setColorProperties(SQLAliasColorProperties value) {
        alias.setColorProperties(value);
    }

    public void setConnectionProperties(SQLAliasConnectionProperties value) {
        alias.setConnectionProperties(value);
    }

    public void setSchemaProperties(SQLAliasSchemaProperties value) {
        logger.info("=======> value==null?" + (value == null));
        if (value != null) {
            logger.info("=======> value.details==null?" + (value.getSchemaDetails() == null));
            if (value.getSchemaDetails() != null) {
                logger.info("=======> value.details.len?" + (value.getSchemaDetails().length));
            }
        }
        alias.setSchemaProperties(value);
    }

}
