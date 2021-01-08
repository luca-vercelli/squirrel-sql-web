package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;
import net.sourceforge.squirrel_sql.ws.managers.TablesManager;

@Path("/Session({sessionId})/Table({catalog : ([^,/]+)?},{schema : ([^,/]+)?},{tableName},{tableType})")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class TableEndpoints {

	@Inject
	SessionsManager sessionsManager;
	@Inject
	TablesManager manager;

	@GET
	@Path("Content")
	public ValueBean<IDataSet> tableContent(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {

		// FIXME what about row limits? -> SessionProperties.getSQLNbrRowsToShow

		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableContent(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("RowCount")
	public ValueBean<IDataSet> tableRowCount(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableRowCount(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Pk")
	public ValueBean<IDataSet> tablePk(@PathParam("sessionId") String sessionId, @PathParam("catalog") String catalog,
			@PathParam("schema") String schema, @PathParam("tableName") String tableName,
			@PathParam("tableType") String tableType) throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTablePk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Columns")
	public ValueBean<IDataSet> tableColumns(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableColumns(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Indexes")
	public ValueBean<IDataSet> tableIndexes(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableIndexes(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Privileges")
	public ValueBean<IDataSet> tablePrivileges(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTablePrivileges(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("ImportedFk")
	public ValueBean<IDataSet> tableImportedFk(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableImportedFk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("ExportedFk")
	public ValueBean<IDataSet> tableExportedFk(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableExportedFk(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("RowId")
	public ValueBean<IDataSet> tableRowId(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableRowID(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("VersionColumns")
	public ValueBean<IDataSet> tableVersionColumns(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		IDataSet dataset;
		try {
			dataset = manager.getTableVersionColumns(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(dataset);
	}

	@GET
	@Path("Ddl")
	public ValueBean<String> tableDdl(@PathParam("sessionId") String sessionId, @PathParam("catalog") String catalog,
			@PathParam("schema") String schema, @PathParam("tableName") String tableName,
			@PathParam("tableType") String tableType) throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		String ddl = manager.getTableDdl(session, catalog, schema, tableName, tableType);
		return new ValueBean<>(ddl);
	}

	@GET
	@Path("ScriptSelect")
	public ValueBean<String> getTableSelectScript(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		String ddl;
		try {
			ddl = manager.getTableSelectScript(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(ddl);
	}

	@GET
	@Path("ScriptInsert")
	public ValueBean<String> getTableInsertScript(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		String ddl;
		try {
			ddl = manager.getTableInsertScript(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(ddl);
	}

	@GET
	@Path("ScriptUpdate")
	public ValueBean<String> getTableUpdateScript(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		String ddl;
		try {
			ddl = manager.getTableUpdateScript(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(ddl);
	}

	@GET
	@Path("ScriptDelete")
	public ValueBean<String> getTableDeleteScript(@PathParam("sessionId") String sessionId,
			@PathParam("catalog") String catalog, @PathParam("schema") String schema,
			@PathParam("tableName") String tableName, @PathParam("tableType") String tableType)
			throws AuthorizationException, SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);
		checkSession(session);
		String ddl;
		try {
			ddl = manager.getTableDeleteScript(session, catalog, schema, tableName, tableType);
		} catch (DataSetException e) {
			throw webAppException(e);
		}
		return new ValueBean<>(ddl);
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

	/**
	 * Throw exception if session is null
	 * 
	 * @param session
	 */
	private void checkSession(ISession session) {
		if (session == null) {
			throw new WebApplicationException("Invalid session id", Status.BAD_REQUEST);
		}
	}
}
