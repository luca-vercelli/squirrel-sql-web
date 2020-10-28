package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manages HTTP Sessions.
 * 
 * Very similar to Application.getSessionManager
 * 
 * @author lv 2020
 *
 */
@Stateless
public class SessionsManager {

	@Inject
	WebApplication webapp;
	@Inject
	AliasesManager aliasesManager;
	@Inject
	DriversManager driversManager;

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	public List<ISession> getConnectedSessions() {

		// FIXME user should receive only sessions opened by his HTTP session
		// i.e. a session bean is required

		ISession[] array = webapp.getSessionManager().getConnectedSessions();
		return Arrays.asList(array);
	}

	public ISession getSessionById(IIdentifier id) {
		return webapp.getSessionManager().getSession(id);
	}

	public ISession getSessionById(String id) {
		return webapp.getSessionManager().getSession(getSessionId(id));
	}

	public ISession connect(String aliasIdentifier, String user, String passwd) {
		SQLAlias alias = aliasesManager.getAliasById(aliasIdentifier);
		SQLDriver driver = driversManager.getDriverById(alias.getDriverIdentifier());
		SQLConnection conn = null; // ?!?
		return webapp.getSessionManager().createSession(webapp, driver, alias, conn, user, passwd);
	}

	public ISession disconnect(String sessionId) {
		ISession item = getSessionById(sessionId);
		if (item == null) {
			logger.warn("Trying to close non-existing session");
			return null;
		}
		try {
			item.close();
		} catch (SQLException e) {
			logger.error("Exception while closing resource", e);
		}
		return item;
	}

	private UidIdentifier getSessionId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
}
