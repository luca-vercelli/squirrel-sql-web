package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	@Path("/History")
	public ListBean<SQLHistoryItem> getHistory() {
		
		 // should we filter by alias name?
		
		return new ListBean<>(manager.getHistory());
	}

	@POST
	@Path("/ExecuteQuery")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<IDataSet> executeQuery(@FormParam("sessionId") String sessionId, @FormParam("query") String query)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);

		try {
			return new ValueBean<>(manager.executeSqlCommand(session, query));
		} catch (DataSetException e) {
			throw webAppException(e);
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
