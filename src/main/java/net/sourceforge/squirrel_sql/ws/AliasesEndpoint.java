package net.sourceforge.squirrel_sql.ws;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

@Path("/")
@Stateless
public class AliasesEndpoint {

	@Inject
	WebApplication webapp;

	// We cannot inject the manager, as it is not an EJB
	// Instead, we inject the WebApplication and retrieve the manager
	private AliasesAndDriversManager getManager() {
		return webapp.getAliasesAndDriversManager();
	}

	@GET
	@Path("/Aliases")
	public List<? extends ISQLAlias> getItems() {
		return getManager().getAliasList();
	}

	@GET
	@Path("/Aliases/{name}")
	public ISQLAlias getItem(String name) {
		List<ISQLAlias> items = getManager().getAliasList().stream().filter(x -> x.getName().equals(name))
				.collect(Collectors.toList());
		return items.isEmpty() ? null : items.get(0);
	}

	@POST
	@Path("/Aliases")
	public void createItem(ISQLAlias item) {
		getManager().addAlias(item);
		// should call saveAliases() ?
	}

	@PUT
	@Path("/Aliases/{name}")
	public void updateItem(ISQLAlias item) {
		// TODO
	}

	@DELETE
	@Path("/Aliases/{name}")
	public void deleteItem(SQLAlias item) {
		getManager().removeAlias(item);
		// should call saveAliases() ?
	}
}
