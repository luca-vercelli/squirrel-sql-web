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
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
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
	public ListBean<ISQLAlias> getItems() {
		@SuppressWarnings("unchecked")
		List<ISQLAlias> list = (List<ISQLAlias>) getManager().getAliasList();
		long count = list.size();
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/AliasesTest")
	public ValueBean<ISQLAlias> getAliasTest() {
		SQLAlias item = new SQLAlias();
		return new ValueBean<>(item);
	}

	@GET
	@Path("/Aliases/{name}")
	public ValueBean<ISQLAlias> getItem(String name) {
		List<ISQLAlias> items = getManager().getAliasList().stream().filter(x -> x.getName().equals(name))
				.collect(Collectors.toList());
		ISQLAlias object = items.isEmpty() ? null : items.get(0);
		// If null, may raise HTTP 404
		return new ValueBean<>(object);
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
