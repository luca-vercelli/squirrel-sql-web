package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.SqlTabManager;
import net.sourceforge.squirrel_sql.ws.model.Table;

@Path("/")
@Stateless
public class SqlTabEndpoint {

	@Inject
	SqlTabManager manager;

	@POST
	@Path("/ExecuteQuery")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<Table> executeQuery(@FormParam("sessionId") String sessionId, @FormParam("query") String query)
			throws SQLException {
		return new ValueBean<>(manager.executeQuery(sessionId, query));
	}
}
