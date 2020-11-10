package net.sourceforge.squirrel_sql.ws.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.SessionDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/")
@Stateless
public class SessionsEndpoint {

	@Inject
	SessionsManager manager;

	@GET
	@Path("/Sessions")
	public ListBean<SessionDto> getItems() {
		Set<ISession> set = manager.getConnectedSessions();
		List<SessionDto> list = new ArrayList<>();
		for (ISession session : set) {
			list.add(new SessionDto(session));
		}
		long count = list.size();
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/Sessions({identifier})")
	public ValueBean<SessionDto> getItem(@PathParam("identifier") String identifier) {
		ISession session = manager.getSessionById(identifier);
		// If null, may raise HTTP 404
		return new ValueBean<>(new SessionDto(session));
	}

	@POST
	@Path("/Connect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<SessionDto> connect(@FormParam("aliasIdentifier") String aliasIdentifier,
			@FormParam("userName") String user, @FormParam("password") String passwd) {

		ISession session = manager.connect(aliasIdentifier, user, passwd);
		return new ValueBean<>(new SessionDto(session));
	}

	@POST
	@Path("/Disconnect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void disconnect(@FormParam("sessionId") String sessionId) {
		manager.disconnect(sessionId);
	}

}
