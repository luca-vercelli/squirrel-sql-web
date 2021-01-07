package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ColumnsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ExportedKeysTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ITableTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ImportedKeysTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.IndexesTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PrimaryKeyTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.RowCountTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.RowIDTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.TablePriviligesTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.VersionColumnsTabPublic;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

/**
 * Manager for both table tabs and table scripts.
 * 
 * @author lv 2020-2021
 *
 */
@Stateless
public class TablesManager {

	@Inject
	WebApplication webapp;
	@Inject
	SessionsManager sessionsManager;

	Logger logger = Logger.getLogger(TablesManager.class);

	/**
	 * Return SELECT * FROM given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableContent(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new ContentsTabPublic(session));
	}

	/**
	 * Return SELECT COUNT(*) FROM given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableRowCount(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new RowCountTabPublic());
	}

	/**
	 * Return primary keys of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTablePk(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new PrimaryKeyTabPublic());
	}

	/**
	 * Return columns definitions of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableColumns(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new ColumnsTabPublic());
	}

	/**
	 * Return indexes of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableIndexes(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new IndexesTabPublic());
	}

	/**
	 * Return privileges on given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTablePrivileges(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new TablePriviligesTabPublic());
	}

	/**
	 * Return imported keys for given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableImportedFk(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new ImportedKeysTabPublic());
	}

	/**
	 * Return exported keys for given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableExportedFk(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new ExportedKeysTabPublic());
	}

	/**
	 * Return what???
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableRowID(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new RowIDTabPublic());
	}

	/**
	 * Return what???
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableVersionColumns(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {
		return commonGetDataSet(session, catalog, schema, table, type, new VersionColumnsTabPublic());
	}

	private IDataSet commonGetDataSet(ISession session, String catalog, String schema, String table, String type,
			ITableTabPublic tab) throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Retrieve table DDL script using internal HibernateDialect stuff
	 * 
	 * @param session
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @param tableType
	 * @return
	 * @throws SQLException
	 */
	public String getTableDdl(ISession session, String catalog, String schema, String tableName, String tableType)
			throws SQLException {

		HibernateDialect dialect = DialectFactory.getDialect(session.getMetaData());

		TableInfo info = new TableInfo(catalog, schema, tableName, tableType, null, session.getMetaData());
		List<ITableInfo> infos = new ArrayList<>();
		infos.add(info);

		// TODO: How to let the user customize this??
		CreateScriptPreferences prefs = new CreateScriptPreferences();

		// FIXME: who kows?
		boolean isJdbcOdbc = false;

		List<String> ddls = dialect.getCreateTableSQL(infos, session.getMetaData(), prefs, isJdbcOdbc);

		if (ddls == null || ddls.isEmpty()) {
			logger.error("Got empty DDL for " + tableType + " " + catalog + "." + schema + "." + tableName);
			throw new SQLException("Cannot retrieve DDL");
		}
		return ddls.isEmpty() ? null : ddls.get(0);
	}
}
