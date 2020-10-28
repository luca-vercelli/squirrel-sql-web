package net.sourceforge.squirrel_sql.ws.managers;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

/**
 * Manages aliases on XML database
 * 
 * @author lv 2020
 *
 */
@Stateless
public class AliasesManager {

	@Inject
	WebApplication webapp;

	@SuppressWarnings("unchecked")
	public List<SQLAlias> getAliases() {
		return (List<SQLAlias>) webapp.getAliasesAndDriversManager().getAliasList();
	}

	public SQLAlias getAliasById(IIdentifier id) {
		return (SQLAlias) webapp.getAliasesAndDriversManager().getAlias(id);
	}

	public SQLAlias getAliasById(String id) {
		return getAliasById(getAliasIdentifier(id));
	}

	public IIdentifier getAliasIdentifier(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}

	public synchronized void saveAllAliases() {
		webapp.savePreferences(PreferenceType.ALIAS_DEFINITIONS);
	}

	public SQLAlias updateAlias(SQLAlias item, String id) throws ValidationException {

		// Load old values
		SQLAlias itemOld = getAliasById(id);

		// Replace old values with new ones
		// This may raise ValidationException if required attribute are missing
		itemOld.setName(item.getName());
		itemOld.setUrl(item.getUrl());
		itemOld.setDriverIdentifier(item.getDriverIdentifier());
		itemOld.setPassword(item.getPassword());
		itemOld.setUserName(item.getUserName());
		itemOld.setAutoLogon(item.isAutoLogon());
		itemOld.setConnectAtStartup(item.isConnectAtStartup());
		itemOld.setEncryptPassword(item.isEncryptPassword());
		itemOld.setDriverProperties(item.getDriverPropertiesClone());

		saveAllAliases();

		return itemOld;
	}

	public SQLAlias createNewAlias(SQLAlias item) throws ValidationException {

		SQLAlias itemNew = new SQLAlias(new UidIdentifier());

		// Set values
		// This may raise ValidationException if required attribute are missing
		itemNew.setName(item.getName());
		itemNew.setUrl(item.getUrl());
		itemNew.setDriverIdentifier(item.getDriverIdentifier());
		itemNew.setPassword(item.getPassword());
		itemNew.setUserName(item.getUserName());
		itemNew.setAutoLogon(item.isAutoLogon());
		itemNew.setConnectAtStartup(item.isConnectAtStartup());
		itemNew.setEncryptPassword(item.isEncryptPassword());
		itemNew.setDriverProperties(item.getDriverPropertiesClone());

		// add driver to managed drivers list
		webapp.getAliasesAndDriversManager().addAlias(item);

		saveAllAliases();

		return itemNew;
	}

	public SQLAlias removeAlias(String id) {
		SQLAlias item = getAliasById(id);
		webapp.getAliasesAndDriversManager().removeAlias(item);
		saveAllAliases();
		return item;
	}

}
