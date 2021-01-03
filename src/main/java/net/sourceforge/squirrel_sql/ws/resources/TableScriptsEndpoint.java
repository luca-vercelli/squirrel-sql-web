package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.DdlManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/Session({sessionId})/Table({catalog},{schema},{tableName},{tableType})")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class TableScriptsEndpoint {

	@Inject
	SessionsManager sessionsManager;
	@Inject
	DdlManager ddlManager;

	@GET
	@Path("Ddl")
	public ValueBean<String> tableDdl(@PathParam("sessionId") String sessionId, @PathParam("catalog") String catalog,
			@PathParam("schema") String schema, @PathParam("tableName") String tableName,
			@PathParam("tableType") String tableType) throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		String ddl = ddlManager.getTableDdl(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(ddl);
	}
}
