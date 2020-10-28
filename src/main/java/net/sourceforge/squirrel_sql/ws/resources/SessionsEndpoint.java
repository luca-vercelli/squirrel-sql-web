package net.sourceforge.squirrel_sql.ws.resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/")
@Stateless
public class SessionsEndpoint {

	@Inject
	SessionsManager manager;

	@GET
	@Path("/Sessions")
	public ListBean<ISession> getItems() {

		// FIXME user should receive only sessions opened by his HTTP session
		// i.e. a session bean is required

		List<ISession> list = manager.getConnectedSessions();
		long count = list.size();
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/Sessions/{identifier}")
	public ValueBean<ISession> getItem(@PathParam("identifier") String identifier) {
		ISession item = manager.getSessionById(identifier);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Connect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<ISession> connect(@FormParam("aliasIdentifier") String aliasIdentifier,
			@FormParam("userName") String user, @FormParam("password") String passwd) {

		ISession session = manager.connect(aliasIdentifier, user, passwd);
		return new ValueBean<>(session);
	}

	@DELETE
	@Path("/Disconnect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void disconnect(@FormParam("sessionId") String sessionId) {
		manager.disconnect(sessionId);
	}

}
