package net.sourceforge.squirrel_sql.ws;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;

@Path("/")
@Stateless
public class SessionsEndpoint {

	@Inject
	WebApplication webapp;

	@GET
	@Path("/Sessions")
	public ListBean<ISession> getItems() {
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
	@Path("/Connect/{aliasIdentifier}")
	public ValueBean<ISession> connect(@PathParam("identifier") String identifier) {
		// TODO
		return new ValueBean<>(null);
	}

	private UidIdentifier createId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
}
