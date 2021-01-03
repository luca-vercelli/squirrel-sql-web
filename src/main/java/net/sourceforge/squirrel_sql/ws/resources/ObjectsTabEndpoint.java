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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

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

@Path("/Session({sessionId})")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ObjectsTabEndpoint {

	@Inject
	ObjectsTabManager manager;
	@Inject
	SessionsManager sessionsManager;

	@GET
	@Path("SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("sessionId") String sessionId)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("SchemaInfo/TableInfo")
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
	@Path("RootNode")
	public ValueBean<ObjectTreeNodeDto> getRootNode(@PathParam("sessionId") String sessionId)
			throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode rootNode = manager.createAndExpandRootNode(session);
		ObjectTreeNodeDto rootNodeDto = manager.node2Dto(rootNode);
		return new ValueBean<>(rootNodeDto);
	}

	@POST
	@Path("ExpandNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public ListBean<ObjectTreeNodeDto> expandNode(@PathParam("sessionId") String sessionId,
			ObjectTreeNodeDto parentNodeDto) throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode node = manager.dto2Node(parentNodeDto, session);
		List<ObjectTreeNode> list = manager.expandNode(node);
		return new ListBean<>(manager.node2Dto(list));
	}

	@GET
	@Path("TableContent")
	public ValueBean<IDataSet> tableContent(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {

		// FIXME what about row limits? -> SessionProperties.getSQLNbrRowsToShow

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableContent(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableRowCount")
	public ValueBean<IDataSet> tableRowCount(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableRowCount(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TablePk")
	public ValueBean<IDataSet> tablePk(@PathParam("sessionId") String sessionId, @QueryParam("catalog") String catalog,
			@QueryParam("schema") String schema, @QueryParam("tableName") String tableName,
			@QueryParam("tableType") String tableType) throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTablePk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableColumns")
	public ValueBean<IDataSet> tableColumns(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableColumns(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableIndexes")
	public ValueBean<IDataSet> tableIndexes(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableIndexes(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TablePrivileges")
	public ValueBean<IDataSet> tablePrivileges(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTablePrivileges(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableImportedFk")
	public ValueBean<IDataSet> tableImportedFk(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableImportedFk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableExportedFk")
	public ValueBean<IDataSet> tableExportedFk(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableExportedFk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableRowId")
	public ValueBean<IDataSet> tableRowId(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableRowID(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableVersionColumns")
	public ValueBean<IDataSet> tableVersionColumns(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("tableName") String tableName, @QueryParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableVersionColumns(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("MetaData")
	public ValueBean<IDataSet> dbMetaData(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getMetaData(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("ConnectionStatus")
	public ValueBean<IDataSet> dbConnectionStatus(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getConnectionStatus(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Catalogs")
	public ValueBean<IDataSet> dbCatalogs(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getCatalogs(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Schemas")
	public ValueBean<IDataSet> dbSchemas(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getSchemas(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TableTypes")
	public ValueBean<IDataSet> dbTableTypes(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTableTypes(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("DataTypes")
	public ValueBean<IDataSet> dbDataTypes(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getDataTypes(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("NumericFunctions")
	public ValueBean<IDataSet> dbNumericFunctions(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getNumericFunctions(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("StringFunctions")
	public ValueBean<IDataSet> dbStringFunctions(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getStringFunctions(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("SystemFunctions")
	public ValueBean<IDataSet> dbSystemFunctions(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getSystemFunctions(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("TimeDateFunctions")
	public ValueBean<IDataSet> dbTimeDateFunctions(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getTimeDateFunctions(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Keywords")
	public ValueBean<IDataSet> dbKeywords(@PathParam("sessionId") String sessionId,
			@QueryParam("catalog") String catalog, @QueryParam("schema") String schema,
			@QueryParam("simpleName") String simpleName, @QueryParam("objectType") String objectType)
			throws AuthorizationException {

		ISession session = sessionsManager.getSessionById(sessionId);
		IDataSet dataset;
		try {
			dataset = manager.getKeywords(session, catalog, schema, simpleName, objectType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	/**
	 * Convert a DataSetException into a WebApplicationException
	 * 
	 * @param e
	 * @return
	 */
	private WebApplicationException webAppException(DataSetException e) {
		if (e.getCause() != null) {
			// this is probably a SQLException
			return new WebApplicationException(e.getCause().getMessage(), Status.BAD_REQUEST);
		} else {
			return new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
		}
	}
}
