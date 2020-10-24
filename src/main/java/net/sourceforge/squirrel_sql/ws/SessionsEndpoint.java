package net.sourceforge.squirrel_sql.ws;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

@Path("/")
@Stateless
public class SessionsEndpoint {

	@Inject
	WebApplication webapp;

	@GET
	@Path("/Sessions")
	public ListBean<ISession> getItems() {

		// FIXME user should receive only sessions opened by his HTTP session
		// i.e. a session bean is required

		ISession[] array = webapp.getSessionManager().getConnectedSessions();
		long count = array.length;
		List<ISession> list = Arrays.asList(array);
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/Sessions/{identifier}")
	public ValueBean<ISession> getItem(@PathParam("identifier") String identifier) {
		UidIdentifier sessionID = createId(identifier);
		ISession item = webapp.getSessionManager().getSession(sessionID);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Connect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<ISession> connect(@FormParam("aliasIdentifier") String aliasIdentifier,
			@FormParam("userName") String user, @FormParam("password") String passwd) {

		SQLAlias alias = (SQLAlias) webapp.getAliasesAndDriversManager().getAlias(createId(aliasIdentifier));
		ISQLDriver driver = webapp.getAliasesAndDriversManager().getDriver(alias.getDriverIdentifier());
		SQLConnection conn = null; // ?!?
		ISession session = webapp.getSessionManager().createSession(webapp, driver, alias, conn, user, passwd);
		return new ValueBean<>(session);
	}

	private UidIdentifier createId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
}
