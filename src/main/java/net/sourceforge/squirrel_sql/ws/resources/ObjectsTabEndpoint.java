package net.sourceforge.squirrel_sql.ws.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeModel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.dto.SchemaInfoDto;
import net.sourceforge.squirrel_sql.dto.TableInfoDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.ObjectsTabManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;
import net.sourceforge.squirrel_sql.ws.managers.TokensManager;

@Path("/")
@Stateless
public class ObjectsTabEndpoint {

	@Inject
	SessionsManager sessionsManager;
	@Inject
	ObjectsTabManager manager;
	@Inject
	TokensManager tokensManager;
	@Context
	HttpServletRequest request;

	/**
	 * Return token in current Request.
	 * 
	 * @return
	 */
	protected String getCurrentToken() {
		try {
			return tokensManager.extractTokenFromRequest(request);
		} catch (AuthorizationException e) {
			throw new IllegalStateException("Error retrieving token. This should not happen.", e);
		}
	}

	// JSUT FOR TEST, this a single static model
	static ObjectTreeModel model;

	@GET
	@Path("/Sessions({identifier})/SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("/Sessions({identifier})/SchemaInfo/TableInfo")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos();
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst, (long) lst.size());
	}

	@GET
	@Path("/Sessions({sessionIdentifier})/Tree")
	public ValueBean<ObjectTreeNodeDto> getTree(@PathParam("sessionIdentifier") String identifier) {
		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());

		// @see ObjectTree

		if (model == null) {
			model = new ObjectTreeModel(session);
		}

		return new ValueBean<>(new ObjectTreeNodeDto((ObjectTreeNode) model.getRoot()));
	}

	@POST
	@Path("/Sessions({sessionIdentifier})/Expand({xpath})")
	public ValueBean<ObjectTreeNodeDto> expandTree(@PathParam("sessionIdentifier") String identifier,
			@PathParam("xpath") String xpath) {
		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());

		// @see ObjectTree::expandNode
		// @see ObjectTreeModel::constructor::run for expanders

		return new ValueBean<>(new ObjectTreeNodeDto((ObjectTreeNode) model.getRoot()));
	}
}
