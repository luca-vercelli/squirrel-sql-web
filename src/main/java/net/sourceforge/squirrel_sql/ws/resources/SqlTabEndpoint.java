package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.client.session.ISession;
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

	@POST
	@Path("/ExecuteQuery")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<IDataSet> executeQuery(@FormParam("sessionId") String sessionId, @FormParam("query") String query)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);

		try {
			return new ValueBean<>(manager.executeSqlCommand(session, query));
		} catch (DataSetException e) {
			throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
		}
	}
}
