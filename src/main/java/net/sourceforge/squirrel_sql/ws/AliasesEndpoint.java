package net.sourceforge.squirrel_sql.ws;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

@Path("/")
public class AliasesEndpoint {

	@Inject
	WebApp webapp;

	@GET
	@Path("/Aliases")
	public List<? extends ISQLAlias> getItems() {
		return webapp.getSwingApp().getAliasesAndDriversManager().getAliasList();
	}

	@GET
	@Path("/Aliases/{name}")
	public List<String> getItem(String name) {
		return null;
	}

	@POST
	@Path("/Aliases")
	public void createItem(ISQLAlias item) {
	}

	@PUT
	@Path("/Aliases/{name}")
	public void updateItem(ISQLAlias item) {
	}

	@DELETE
	@Path("/Aliases/{name}")
	public void deleteItem(String name) {
	}
}
