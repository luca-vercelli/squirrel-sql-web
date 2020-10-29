package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
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
 * This bean is Stateful: it keeps only the sessions opened by the current HTTP
 * session.
 * 
 * @author lv 2020
 *
 */
@Stateful
public class SessionsManager {

	@Inject
	WebApplication webapp;
	@Inject
	AliasesManager aliasesManager;
	@Inject
	DriversManager driversManager;

	protected Set<ISession> openSessions = new HashSet<>();

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	/**
	 * If the user leave some opne sessions, close them at last
	 */
	@PreDestroy
	public void preDestroy() {
		for (ISession session : openSessions) {
			disconnect(session);
			logger.info("Just closed session '" + session.getTitle() + "'");
		}
	}

	/**
	 * Return all SQL sessions opened in current HTTP session
	 */
	public Set<ISession> getConnectedSessions() {
		return Collections.unmodifiableSet(openSessions);
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened in
	 * current HTTP session
	 */
	public ISession getSessionById(IIdentifier id) {
		for (ISession session : openSessions) {
			if (session.getIdentifier().equals(id)) {
				return session;
			}
		}
		return null;
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened in
	 * current HTTP session
	 */
	public ISession getSessionById(String id) {
		return getSessionById(getSessionId(id));
	}

	/**
	 * Create a new SQL session, and also a SQLConnection if needed
	 */
	public ISession connect(String aliasIdentifier, String user, String passwd) {
		SQLAlias alias = aliasesManager.getAliasById(aliasIdentifier);
		SQLDriver driver = driversManager.getDriverById(alias.getDriverIdentifier());
		SQLConnection conn = aliasesManager.createConnection(alias);
		ISession session = webapp.getSessionManager().createSession(webapp, driver, alias, conn, user, passwd);
		openSessions.add(session);
		return session;
	}

	/**
	 * Disconnect an existing SQL session, <i>if</i> it was opened in current HTTP
	 * session, and also the relative SQLConnection if needed
	 * 
	 * @param sessionId
	 * @return
	 */
	public ISession disconnect(String sessionId) {
		ISession session = getSessionById(sessionId);
		return disconnect(session);
	}

	public ISession disconnect(ISession session) {
		if (session == null) {
			logger.warn("Trying to close non-existing session");
			return null;
		}
		try {
			session.getSQLConnection().close();
		} catch (SQLException e) {
			logger.error("Exception while closing SQLConnection", e);
		}
		try {
			session.close();
		} catch (SQLException e) {
			logger.error("Exception while closing Session", e);
		}
		openSessions.remove(session);
		return session;
	}

	private UidIdentifier getSessionId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
}
