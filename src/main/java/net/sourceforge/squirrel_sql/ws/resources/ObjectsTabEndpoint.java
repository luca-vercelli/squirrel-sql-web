package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.dto.SchemaInfoDto;
import net.sourceforge.squirrel_sql.dto.TableInfoDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.ObjectsTabManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/")
@Stateless
public class ObjectsTabEndpoint {

	@Inject
	ObjectsTabManager manager;
	@Inject
	SessionsManager sessionsManager;

	@GET
	@Path("/Session({sessionId})/SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("sessionId") String sessionId)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("/Session({sessionId})/SchemaInfo/TableInfo")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("sessionId") String sessionId) throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos();
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst);
	}

	@GET
	@Path("/Session({sessionId})/RootNode")
	public ValueBean<ObjectTreeNodeDto> getRootNode(@PathParam("sessionId") String sessionId)
			throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode rootNode = manager.createAndExpandRootNode(session);
		ObjectTreeNodeDto rootNodeDto = manager.node2Dto(rootNode);
		return new ValueBean<>(rootNodeDto);
	}

	@POST
	@Path("/Session({sessionId})/ExpandNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public ListBean<ObjectTreeNodeDto> expandNode(@PathParam("sessionId") String sessionId,
			ObjectTreeNodeDto parentNodeDto) throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode node = manager.dto2Node(parentNodeDto, session);
		List<ObjectTreeNode> list = manager.expandNode(node);
		return new ListBean<>(manager.node2Dto(list));
	}

	@GET
	@Path("/Session({sessionId})/TableContent")
	public ValueBean<IDataSet> tableContent(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws DataSetException, AuthorizationException {

		// FIXME what about row limits?

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTableContent(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("/Session({sessionId})/TableRowCount")
	public ValueBean<IDataSet> tableRowCount(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws DataSetException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTableRowCount(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("/Session({sessionId})/TablePk")
	public ValueBean<IDataSet> tablePk(@PathParam("sessionId") String sessionId, @QueryParam("catalog") String catalog,
			@QueryParam("schema") String schema, @QueryParam("tableName") String tableName,
			@QueryParam("tableType") String tableType) throws DataSetException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTablePk(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("/Session({sessionId})/TableColumns")
	public ValueBean<IDataSet> tableColumns(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws DataSetException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTableColumns(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("/Session({sessionId})/TableIndexes")
	public ValueBean<IDataSet> tableIndexes(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws DataSetException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTableIndexes(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("/Session({sessionId})/TablePrivileges")
	public ValueBean<IDataSet> tablePrivileges(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws DataSetException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset = manager.getTablePrivileges(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(dataset);
	}
}
