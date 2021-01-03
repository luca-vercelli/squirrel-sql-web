package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

/**
 * This kind of tab is not present in original SQuirreL.
 * 
 * Works for TABLE type only, I think.
 * 
 * @author lv 2021
 *
 */
@Stateless
public class DdlManager {

	Logger logger = Logger.getLogger(DdlManager.class);

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
