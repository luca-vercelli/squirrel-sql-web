package net.sourceforge.squirrel_sql.ws.resources;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;
import net.sourceforge.squirrel_sql.ws.managers.SqlTabManager;

@Path("/")
@Stateless
public class SqlTabEndpoint {

    @Inject
    SqlTabManager manager;
    @Inject
    SessionsManager sessionsManager;

    @GET
    @Path("/Session({sessionId})/History")
    public ListBean<SQLHistoryItem> getHistory(@PathParam("sessionId") String sessionId) throws AuthorizationException {
        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        return new ListBean<>(manager.getHistory(session));
    }

    @POST
    @Path("/ExecuteQuery")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ValueBean<IDataSet> executeQuery(@FormParam("sessionId") String sessionId, @FormParam("query") String query,
            @QueryParam("$skip") Integer skip, @QueryParam("$top") Integer top) throws AuthorizationException {

        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        try {
            return new ValueBean<>(manager.executeSqlCommand(query, session, skip, top));
        } catch (DataSetException e) {
            throw webAppException(e);
        }
    }

    @GET
    @Path("/ExportExecuteQuery")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public File exportExecuteQuery(@FormParam("sessionId") String sessionId, @FormParam("query") String query)
            throws AuthorizationException, IOException {

        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        try {
            return manager.exportExecuteSqlCommand(query, session);
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
        }
    }

    /**
     * Convert a DataSetException into a WebApplicationException
     * 
     * @param e
     * @return
     */
    private WebApplicationException webAppException(DataSetException e) {
        if (e.getCause() != null) {
            // this is probably a SQLException
            return new WebApplicationException(e.getCause().getMessage(), Status.BAD_REQUEST);
        } else {
            return new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
        }
    }
}
