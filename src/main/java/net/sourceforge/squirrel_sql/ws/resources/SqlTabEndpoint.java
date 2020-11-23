package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.dto.TableDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.SqlTabManager;

@Path("/")
@Stateless
public class SqlTabEndpoint {

	@Inject
	SqlTabManager manager;

	@POST
	@Path("/ExecuteQuery")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ValueBean<TableDto> executeQuery(@FormParam("sessionId") String sessionId,
			@FormParam("query") String query) {
		try {
			return new ValueBean<>(manager.executeSqlCommand(sessionId, query));
		} catch (SQLException e) {
			throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
		}
	}
}
