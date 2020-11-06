package net.sourceforge.squirrel_sql.dto;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * A bean that represents useful properties of a ISession, to be sent by
 * webservices.
 * 
 * @author lucav
 *
 */
public class SessionDto {

	ISession session;

	public SessionDto(ISession session) {
		this.session = session;
	}

	public IIdentifier getIdentifier() {
		return session.getIdentifier();
	}

	public String getTitle() {
		return session.getTitle();
	}

	public IIdentifier getAliasIdentifier() {
		return session.getAlias().getIdentifier();
	}

	public IIdentifier getDriverIdentifier() {
		return session.getDriver().getIdentifier();
	}

	public SessionProperties getProperties() {
		return session.getProperties();
	}
}
