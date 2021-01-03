package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

/**
 * This kind of tab is not present in original SQuirreL.
 * 
 * Works for TABLE type only, and algorithm is quite naive.
 * 
 * @author lv 2021
 *
 */
@Stateless
public class DdlManager {

	Logger logger = Logger.getLogger(DdlManager.class);
	private static int[] columnIndices = new int[] { //
			4, // name
			6, // type
			7, // size
			9, // decimal digits
			11, // nullable
			17 // position
	};

	public String getTableDdl(ISession session, String catalog, String schema, String tableName)
			throws DataSetException {
		// cfr. ColumnsTab.createDataSet()

		TableInfo info = new TableInfo(catalog, schema, tableName, "TABLE", null, session.getMetaData());

		final ISQLConnection conn = session.getSQLConnection();
		SQLDatabaseMetaData md = conn.getSQLMetaData();
		IDataSet ds = md.getColumns(info, columnIndices, true);

		StringBuilder ddl = new StringBuilder("CREATE TABLE ").append(tableName).append(" (\n");
		boolean first = true;
		while (ds.next(NullMessageHandler.getInstance())) {
			if (!first) {
				ddl.append(",\n");
			} else {
				first = false;
			}
			ddl.append(ds.get(0)).append(' ').append(ds.get(1));
			if (ds.get(2) != null) {
				ddl.append('(').append(ds.get(2));
				if (ds.get(3) != null) {
					ddl.append(',').append(ds.get(3));
				}
				ddl.append(')');
			}
			if (ds.get(4) != null && ds.get(4).equals(0)) {
				ddl.append(" NOT NULL");
			}
		}
		return ddl.append("\n)").toString();
	}
}
