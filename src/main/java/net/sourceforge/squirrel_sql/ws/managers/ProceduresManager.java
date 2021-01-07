package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure.IProcedureTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure.ProcedureColumnsTabPublic;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

/**
 * Manager for both procedure tabs and pocedure scripts.
 * 
 * @author lv 2020-2021
 *
 */
@Stateless
public class ProceduresManager {

	@Inject
	WebApplication webapp;
	@Inject
	SessionsManager sessionsManager;

	Logger logger = Logger.getLogger(ProceduresManager.class);

	/**
	 * Return "columns", i.e. parameters, of given procedure / function / ...
	 * 
	 * @param session
	 * @param catalog
	 * @param schema
	 * @param procedure
	 * @param procType  DatabaseMetaData.procedureNoResult|DatabaseMetaData.procedureReturnsResult|...
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getProcedureColumns(ISession session, String catalog, String schema, String procedure, int procType)
			throws DataSetException {
		ProcedureInfo info = new ProcedureInfo(catalog, schema, procedure, null, procType,
				(SQLDatabaseMetaData) session.getMetaData());
		IProcedureTabPublic tab = new ProcedureColumnsTabPublic();
		tab.setSession(session);
		tab.setProcedureInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}
}
