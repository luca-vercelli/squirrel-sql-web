package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.ProceduresManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/Session({sessionId})/Procedure({catalog : ([^,/]+)?},{schema : ([^,/]+)?},{procName},{procType})")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ProcedureEndpoints {

    @Inject
    SessionsManager sessionsManager;
    @Inject
    ProceduresManager manager;

    @GET
    @Path("Columns")
    public ValueBean<IDataSet> tableColumns(@PathParam("sessionId") String sessionId,
            @PathParam("catalog") String catalog, @PathParam("schema") String schema,
            @PathParam("procName") String procName, @PathParam("procType") int procType) throws AuthorizationException {
        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        IDataSet dataset;
        try {
            dataset = manager.getProcedureColumns(session, catalog, schema, procName, procType);
        } catch (DataSetException e) {
            throw webAppException(e);
        }
        return new ValueBean<>(dataset);
    }

    @GET
    @Path("CreateProcedure")
    public ValueBean<String> getDdl(@PathParam("sessionId") String sessionId, @PathParam("catalog") String catalog,
            @PathParam("schema") String schema, @PathParam("procName") String procName,
            @PathParam("objectType") String objectType, @PathParam("procType") int procType)
            throws AuthorizationException, SQLException {
        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        String command = manager.getSource(session, catalog, schema, procName, objectType, procType);
        return new ValueBean<>(command);
    }

    @GET
    @Path("RunProcedure")
    public ValueBean<String> getRunProcedureScript(@PathParam("sessionId") String sessionId,
            @PathParam("catalog") String catalog, @PathParam("schema") String schema,
            @PathParam("procName") String procName, @PathParam("procType") int procType)
            throws AuthorizationException, SQLException {
        ISession session = sessionsManager.getSessionById(sessionId);
        sessionsManager.checkSession(session);
        String script;
        script = manager.getRunCommand(session, catalog, schema, procName, procType);
        return new ValueBean<>(script);
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
