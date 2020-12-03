package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.dto.SchemaInfoDto;
import net.sourceforge.squirrel_sql.dto.TableDto;
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

	@GET
	@Path("/Session({identifier})/SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("/Session({identifier})/SchemaInfo/TableInfo")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos();
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst);
	}

	@GET
	@Path("/Session({sessionIdentifier})/RootNode")
	public ValueBean<ObjectTreeNodeDto> getRootNode(@PathParam("sessionIdentifier") String identifier)
			throws SQLException {
		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		ObjectTreeNode rootNode = manager.createAndExpandRootNode(session);
		ObjectTreeNodeDto rootNodeDto = manager.node2Dto(rootNode);
		return new ValueBean<>(rootNodeDto);
	}

	@POST
	@Path("/Session({sessionIdentifier})/ExpandNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public ListBean<ObjectTreeNodeDto> expandNode(@PathParam("sessionIdentifier") String identifier,
			ObjectTreeNodeDto parentNodeDto) throws SQLException {
		ISession session = sessionsManager.getSessionById(identifier, getCurrentToken());
		ObjectTreeNode node = manager.dto2Node(parentNodeDto, session);
		List<ObjectTreeNode> list = manager.expandNode(node);
		return new ListBean<>(manager.node2Dto(list));
	}

	@GET
	@Path("/Session({sessionId})/TableContent")
	public ValueBean<TableDto> tableContent(@PathParam("sessionId") String identifier,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("table") String table) throws SQLException {

		// TODO
		return null;
	}

	@GET
	@Path("/Session({sessionId})/TableRowCount")
	public ValueBean<TableDto> tableRowCount(@PathParam("sessionId") String identifier,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("table") String table) throws SQLException {

		// TODO
		return null;
	}

	@GET
	@Path("/Session({sessionId})/TablePk")
	public ValueBean<TableDto> tablePk(@PathParam("sessionId") String identifier,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("table") String table) throws SQLException {

		// TODO
		return null;
	}
}
