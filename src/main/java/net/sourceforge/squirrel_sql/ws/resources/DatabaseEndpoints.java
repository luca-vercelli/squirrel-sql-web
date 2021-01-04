package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.DatabasesManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/Session({sessionId})/Database({catalog},{schema},{simpleName},{objectType})")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class DatabaseEndpoints {

	@Inject
	SessionsManager sessionsManager;
	@Inject
	DatabasesManager manager;

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
