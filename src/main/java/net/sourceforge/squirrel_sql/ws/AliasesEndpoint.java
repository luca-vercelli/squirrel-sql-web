package net.sourceforge.squirrel_sql.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

@Path("/")
@Stateless
public class AliasesEndpoint {

	@Inject
	WebApplication webapp;

	Logger logger = Logger.getLogger(AliasesEndpoint.class);

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
	@Path("/Aliases/{identifier}")
	public ValueBean<ISQLAlias> getItem(@PathParam("identifier") String stringId) {
		ISQLAlias item = get(stringId);
		// If null, may raise HTTP 404
		return new ValueBean<>(item);
	}

	@POST
	@Path("/Aliases")
	public void createItem(ISQLAlias item) {
		getManager().addAlias(item);
		webapp.savePreferences(PreferenceType.ALIAS_DEFINITIONS);
	}

	@PUT
	@Path("/Aliases/{identifier}")
	public void updateItem(@PathParam("identifier") String stringId, ISQLAlias item) {
		// TODO
	}

	@DELETE
	@Path("/Aliases/{identifier}")
	public void deleteItem(@PathParam("identifier") String stringId) {
		ISQLAlias item = get(stringId);
		getManager().removeAlias((SQLAlias) item);
		webapp.savePreferences(PreferenceType.ALIAS_DEFINITIONS);
	}

	private UidIdentifier createId(String stringId) {
		UidIdentifier id = new UidIdentifier();
		id.setString(stringId);
		return id;
	}
	
	private ISQLAlias get(String stringId) {
		UidIdentifier id = createId(stringId);
		return getManager().getAlias(id);
	}
}
