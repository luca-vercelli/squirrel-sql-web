package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
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
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
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
	@Inject
	DriversManager driversManager;

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
	 * Return SELECT * FROM given table
	 * 
	 * @param skip items to skip
	 * @param top  items to show
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableContentV2(ISession session, String catalog, String schema, String table, String type,
			Integer skip, Integer top) throws DataSetException {

		// FIXME should better use EntityManager API (not in Hibernate 3.2 ...)
		SessionFactory sessionFactory = openSession(session);
		Session hbSession = sessionFactory.openSession();
		String sql = ""; // TODO, cfr. ContentsTab
		SQLQuery query = hbSession.createSQLQuery(sql); // NativeQuery, in more recent Hibernate
		query.setFirstResult(skip);
		query.setMaxResults(top);
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.list();

		sessionFactory.close(); // or not?

		// TODO convert list into IDataSet
		return null;
	}

	protected SessionFactory openSession(ISession session) {
		ISQLAliasExt alias = session.getAlias();
		SQLDriver driver = driversManager.getDriverById(alias.getDriverIdentifier());

		// Hibernate configuration
		Configuration c = new Configuration();
		c.configure();
		c.setProperty("connection.driver_class", driver.getDriverClassName());
		c.setProperty("hibernate.connection.url", alias.getUrl());
		c.setProperty("hibernate.connection.username", alias.getName());
		c.setProperty("hibernate.connection.password", alias.getPassword());
		c.setProperty("show_sql", "true");
		// should also set property "dialect"

		// FIXME should better use EntityManager API
		return c.buildSessionFactory();
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

	// ==========================================================================================================

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

	public String getTableSelectScript(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException, DataSetException {

		List<String> columns = getNonBlobColumns(session, catalog, schema, tableName, tableType);

		StringBuilder sb = new StringBuilder("SELECT");
		boolean first = true;
		for (String column : columns) {
			if (first) {
				first = false;
				sb.append("\n    ");
			} else {
				sb.append(",\n    ");
			}
			sb.append(column);
		}
		sb.append("\nFROM ").append(tableName); // FIXME schema? catalog?
		return sb.toString();
	}

	public String getTableDeleteScript(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException, DataSetException {
		List<String> whereColumns = getKeyColumns(session, catalog, schema, tableName, tableType);
		if (whereColumns.isEmpty()) {
			whereColumns = getNonBlobColumns(session, catalog, schema, tableName, tableType);
		}

		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(tableName); // FIXME schema? catalog?
		appendWhereClause(sb, whereColumns);
		return sb.toString();
	}

	private void appendWhereClause(StringBuilder sb, List<String> columns) {
		if (!columns.isEmpty()) {
			sb.append("\nWHERE");
			boolean first = true;
			for (String colName : columns) {
				if (first) {
					sb.append("\n   ");
					first = false;
				} else {
					sb.append(" AND\n   ");
				}
				sb.append(colName).append(" = 'xxx'"); // this is /very/ naive
			}
		}
	}

	public String getTableInsertScript(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException, DataSetException {

		List<String> columns = getNonBlobColumns(session, catalog, schema, tableName, tableType);
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(tableName); // FIXME schema? catalog?
		sb.append('(');
		boolean first = true;
		for (String colName : columns) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(colName);
		}
		sb.append(")\nVALUES(");
		first = true;
		for (int i = 0; i < columns.size(); ++i) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("NULL");
		}
		sb.append(')');

		return sb.toString();
	}

	public String getTableUpdateScript(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException, DataSetException {

		List<String> columns = getNonBlobColumns(session, catalog, schema, tableName, tableType);

		List<String> whereColumns = getKeyColumns(session, catalog, schema, tableName, tableType);
		if (whereColumns.isEmpty()) {
			whereColumns = columns;
		}

		StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(tableName); // FIXME schema? catalog?
		sb.append("\nSET");
		boolean first = true;
		for (String colName : columns) {
			if (first) {
				sb.append("\n   ");
				first = false;
			} else {
				sb.append(",\n   ");
			}
			sb.append(colName).append(" = 'xxx'"); // this is /very/ naive
		}

		appendWhereClause(sb, whereColumns);
		return sb.toString();
	}

	/**
	 * Retrieve all columns names, but BLOB and CLOB ones.
	 */
	public List<String> getNonBlobColumns(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException {

		final ISQLConnection conn = session.getSQLConnection();
		SQLDatabaseMetaData md = conn.getSQLMetaData();
		TableColumnInfo[] columnsMetadata = md
				.getColumnInfo(new TableInfo(catalog, schema, tableName, tableType, null, md));

		List<String> columns = new ArrayList<>();
		for (TableColumnInfo col : columnsMetadata) {
			if (col.getDataType() != Types.BLOB && col.getDataType() != Types.CLOB) {
				columns.add(col.getColumnName());
			}
		}
		return columns;
	}

	/**
	 * Retrieve all key columns names. ones.
	 * 
	 * @return list of key columns. May be empty.
	 */
	public List<String> getKeyColumns(ISession session, String catalog, String schema, String tableName,
			String tableType) throws SQLException {

		final ISQLConnection conn = session.getSQLConnection();
		SQLDatabaseMetaData md = conn.getSQLMetaData();
		PrimaryKeyInfo[] columnsMetadata = md
				.getPrimaryKey(new TableInfo(catalog, schema, tableName, tableType, null, md));

		List<String> columns = new ArrayList<>();
		if (columnsMetadata != null) {
			for (PrimaryKeyInfo col : columnsMetadata) {
				columns.add(col.getColumnName());
			}
		}
		return columns;
	}
}
