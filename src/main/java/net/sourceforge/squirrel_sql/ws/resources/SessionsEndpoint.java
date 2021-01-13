package net.sourceforge.squirrel_sql.ws.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.SessionDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;
import net.sourceforge.squirrel_sql.ws.managers.TokensManager;

@Path("/")
@Stateless
public class SessionsEndpoint {

    @Inject
    SessionsManager manager;
    @Inject
    TokensManager tokensManager;
    @Context
    HttpServletRequest request;

    Logger logger = Logger.getLogger(SessionsEndpoint.class);

    /**
     * Return token in current Request.
     * 
     * @return
     */
    protected String getCurrentToken() {
        try {
            return tokensManager.extractTokenFromRequest(request);
        } catch (AuthorizationException e) {
            throw new IllegalStateException("Error retrieving token. This should not happen.", e);
        }
    }

    @GET
    @Path("/Session")
    public ListBean<SessionDto> getItems() {
        Set<ISession> set = manager.getOpenSessions(getCurrentToken());
        List<SessionDto> list = new ArrayList<>();
        for (ISession session : set) {
            list.add(new SessionDto(session));
        }
        // If 0, may raise HTTP 404
        return new ListBean<>(list);
    }

    @GET
    @Path("/Session({identifier})")
    public ValueBean<SessionDto> getItem(@PathParam("identifier") String identifier) {
        ISession session = manager.getSessionById(identifier, getCurrentToken());
        // If null, may raise HTTP 404
        return new ValueBean<>(new SessionDto(session));
    }

    @POST
    @Path("/Connect")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ValueBean<SessionDto> connect(@FormParam("aliasIdentifier") String aliasIdentifier,
            @FormParam("userName") String user, @FormParam("password") String passwd) {

        ISession session = manager.connect(aliasIdentifier, user, passwd, getCurrentToken());
        return new ValueBean<>(new SessionDto(session));
    }

    @POST
    @Path("/Disconnect")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void disconnect(@FormParam("sessionId") String sessionId) {
        manager.disconnect(sessionId, getCurrentToken());
    }

    @POST
    @Path("/DisconnectAllSessions")
    public void disconnectAll() {
        manager.disconnectAll(getCurrentToken());
    }

    @PUT
    @Path("/Session({identifier})/Properties")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ValueBean<SessionProperties> saveProperties(@PathParam("identifier") String identifier,
            SessionProperties props) throws AuthorizationException {
        logger.info("DEBUG RECEIVED getSQLNbrRowsToShow=" + props.getSQLNbrRowsToShow() + "; " + props.getClass());

        ISession session = manager.getSessionById(identifier);
        manager.checkSession(session);
        props = manager.setProperties(session, props);
        return new ValueBean<>(props);
    }
}
