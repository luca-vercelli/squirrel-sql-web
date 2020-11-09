package net.sourceforge.squirrel_sql.ws.resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.ws.managers.AliasesManager;

@Path("/")
@Stateless
public class AliasesEndpoint {

	@Inject
	AliasesManager manager;

	@GET
	@Path("/Aliases")
	public ListBean<SQLAlias> getItems() {
		List<SQLAlias> list = manager.getAliases();
		long count = list.size();
		// If 0, may raise HTTP 404
		return new ListBean<>(list, count);
	}

	@GET
	@Path("/Aliases({identifier})")
	public ValueBean<SQLAlias> getItem(@PathParam("identifier") String stringId) {
		SQLAlias item = manager.getAliasById(stringId);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Aliases")
	public ValueBean<SQLAlias> createItem(SQLAlias item) throws ValidationException {
		item = manager.createNewAlias(item);
		return new ValueBean<>(item);
	}

	@PUT
	@Path("/Aliases({identifier})")
	public ValueBean<SQLAlias> updateItem(@PathParam("identifier") String stringId, SQLAlias item)
			throws ValidationException {
		item = manager.updateAlias(item, stringId);
		return new ValueBean<>(item);
	}

	@DELETE
	@Path("/Aliases({identifier})")
	public void deleteItem(@PathParam("identifier") String stringId) {
		manager.removeAlias(stringId);
	}

}
