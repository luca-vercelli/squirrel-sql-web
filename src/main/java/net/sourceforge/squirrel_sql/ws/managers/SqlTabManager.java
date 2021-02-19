package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.ExportFileWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportPreferences;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportPreferencesDAO;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.ResultSetExportData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

/**
 * Manages statements execution.
 * 
 * @author lv 2020
 *
 */
@Stateless
public class SqlTabManager {

    @Inject
    WebApplication webapp;

    Logger logger = Logger.getLogger(SqlTabManager.class);

    // In SQuirreLSQL Core, the SQLExecuterTask is reached through the chain:
    // Session -> SessionPanel -> MainPanel -> SQLPanel.runCurrentExecuter() ->
    // SQLResultExecuterPanel -> SQLExecutionHandler -> SQLExecuterTask
    // and the Runnable is executed within a ThreadPool
    // We don't need such a mess

    /**
     * Execute every kind of query (SELECT, UPDATE, CREATE TABLE, ...).
     * 
     * Execute only single queries, not multiple queries.
     * 
     * @param session
     * @param query
     * @param skip    How many rows to skip from beginning (may override Session's
     *                param)
     * @param top     How many rows to show
     * @return a TableDto in case of SELECT, null otherwise
     * @throws SQLException
     * @throws DataSetException
     */
    public IDataSet executeSqlCommand(String query, ISession session, Integer skip, Integer top)
            throws DataSetException {

        query = StringUtilities.cleanString(query);
        logger.info("Running query: " + query);

        if (skip == null) {
            skip = 0;
        }

        // Following code is essentially copied from RowCountTab.java

        addSQLToHistory(query, session);

        final ISQLConnection conn = session.getSQLConnection();
        try {
            final Statement stmt = conn.createStatement();
            try {
                final SessionProperties props = session.getProperties();
                logger.info("DEBUG :" + props.getSQLLimitRows() + "; " + skip + "; " + props.getSQLNbrRowsToShow());
                if (props.getSQLLimitRows() || top != null) {
                    try {
                        if (top != null) {
                            stmt.setMaxRows(skip + top);
                        } else {
                            stmt.setMaxRows(skip + props.getSQLNbrRowsToShow());
                        }
                    } catch (Exception ex) {
                        logger.error("Error on Statement.setMaxRows()", ex);
                    }
                }

                final boolean returnResultSet = stmt.execute(query);
                // StatementWrapper honours getSQLNbrRowsToShow....
                if (!returnResultSet) {
                    // Not a SELECT
                    return null;
                }
                final ResultSet rs = stmt.getResultSet();
                skip(rs, skip);
                try {
                    final ResultSetDataSet rsds = new ResultSetDataSet();
                    rsds.setResultSet(rs, getDialectType(session));
                    return rsds;
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } catch (SQLException ex) {
            throw new DataSetException(ex);
        }
    }

    public DialectType getDialectType(ISession session) {
        return DialectFactory.getDialectType(session.getMetaData());
    }

    /**
     * Save in history, if Preferences say that
     * 
     * @param sql
     * @param session
     */
    public void addSQLToHistory(String sql, ISession session) {

        // cfr. SQLPanelAPI.addSQLToHistory()

        if (session.getProperties().getSQLShareHistory()) {
            final SQLHistoryItem shi = new SQLHistoryItem(sql, session.getAlias().getName());
            session.getApplication().getSQLHistory().add(shi);
            session.getApplication().savePreferences(PreferenceType.SQLHISTORY);
        }
    }

    /**
     * Skip first <code>skip</code> records in <code>rs</code>
     * 
     * @param rs
     * @param skip: not null
     * @throws SQLException
     */
    private void skip(ResultSet rs, Integer skip) throws SQLException {
        int i = 0;
        while (i < skip && rs.next()) {
            // this is slow and resource expensive...
            // If I am not wrong Hibernate does the same, doesn't it?
            ++i;
        }
    }

    /**
     * Return History
     * 
     * @param session
     * 
     * @return
     */
    public List<SQLHistoryItem> getHistory(ISession session) {

        SQLHistory sqlHistory = webapp.getSQLHistory();
        SQLHistoryItem[] data = sqlHistory.getData();
        List<SQLHistoryItem> list = Arrays.asList(data);

        // FIXME how can we filter by session.alias ?!?

        String aliasName = session.getAlias().getName();
        return list.stream().filter(x -> x.getAliasName() == null || x.getAliasName().equals(aliasName))
                .collect(Collectors.toList());
    }

    public File exportExecuteSqlCommand(String query, ISession session) throws SQLException, IOException {

        // cfr. ResultSetExportController, AbstractExportCommand, ExportFileWriter,
        // SquirrelCli

        File outputFile = File.createTempFile("export-", ".xlsx");

        final ISQLConnection sqlConnection = session.getSQLConnection();
        final Statement stat = sqlConnection.getConnection().createStatement();
        final DialectType dialectType = DialectFactory.getDialectType(sqlConnection.getSQLMetaData());

        TableExportPreferences exportPrefs = createExportPreferencesForFile(outputFile.getAbsolutePath());

        ExportFileWriter.writeFile(exportPrefs, new ResultSetExportData(stat.executeQuery(query), dialectType), null);
        return outputFile;
    }

    // This method is from some recent SQuirreL core, not present in 4.1.0
    public static TableExportPreferences createExportPreferencesForFile(String fileName) {
        TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

        prefs.setFile(fileName);

        if (fileName.toUpperCase().endsWith("CSV")) {
            prefs.setFormatCSV(true);
            prefs.setFormatXLSOld(false);
            prefs.setFormatXLS(false);
            prefs.setFormatXML(false);
            prefs.setFormatJSON(false);
        } else if (fileName.toUpperCase().endsWith("XLS")) {
            prefs.setFormatCSV(false);
            prefs.setFormatXLSOld(true);
            prefs.setFormatXLS(false);
            prefs.setFormatXML(false);
            prefs.setFormatJSON(false);
        } else if (fileName.toUpperCase().endsWith("XLSX")) {
            prefs.setFormatCSV(false);
            prefs.setFormatXLSOld(false);
            prefs.setFormatXLS(true);
            prefs.setFormatXML(false);
            prefs.setFormatJSON(false);
        } else if (fileName.toUpperCase().endsWith("XML")) {
            prefs.setFormatCSV(false);
            prefs.setFormatXLSOld(false);
            prefs.setFormatXLS(false);
            prefs.setFormatXML(true);
            prefs.setFormatJSON(false);
        } else if (fileName.toUpperCase().endsWith("JSON")) {
            prefs.setFormatCSV(false);
            prefs.setFormatXLSOld(false);
            prefs.setFormatXLS(false);
            prefs.setFormatXML(false);
            prefs.setFormatJSON(true);
        }
        // else use the prefs predefined format

        return prefs;
    }
}
