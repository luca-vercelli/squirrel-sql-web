package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*
 * 
 *  squirrel-sql-web: remove references to ObjectTreePanel
 *  
 *  
 */

import java.awt.Component;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.DataSetUpdateableTableModelImpl;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.PleaseWaitDialog;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.OrderByClausePanel;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.WhereClausePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This is the tab showing the contents (data) of the table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ContentsTabPublic extends BaseTableTab implements IDataSetUpdateableTableModel, ITableTabPublic {
    private final DataSetUpdateableTableModelImpl _dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ContentsTab.class);

    /**
     * Name of the table that this tab displayed last time it was loaded. This is
     * needed to prevent an on-demand edit operation from turning all data into
     * editable tables. The initial value of "" allows us to dispense with a check
     * for null on the first pass.
     */
    String previousTableName = "";

    private final SQLFilterClauses _sqlFilterClauses = new SQLFilterClauses();

    /** Logger for this class. */
    private static final ILogger s_log = LoggerController.createLogger(ContentsTab.class);

    private PleaseWaitDialog _waitDialog = null;

    private SquirrelPreferences _prefs = null;

    private Integer _skip = 0;
    private Integer _top = null;

    public ContentsTabPublic(ISession session, Integer skip, Integer top) {
        _prefs = session.getApplication().getSquirrelPreferences();
        if (skip != null) {
            _skip = skip;
        }
        _top = top;
    }

    /**
     * Return the title for the tab.
     *
     * @return The title for the tab.
     */
    @Override
    public String getTitle() {
        return getContentsTabTitle();
    }

    /**
     * Return the title for the tab.
     *
     * @return The title for the tab.
     */
    public static String getContentsTabTitle() {
        // i18n[ContentsTab.title=Content]
        return s_stringMgr.getString("ContentsTab.title");
    }

    /**
     * Return the hint for the tab.
     *
     * @return The hint for the tab.
     */
    @Override
    public String getHint() {
        // i18n[ContentsTab.hint=View the contents of the selected table]
        return s_stringMgr.getString("ContentsTab.hint");
    }

    public SQLFilterClauses getSQLFilterClauses() {
        return _sqlFilterClauses;
    }

    /**
     * Create the <TT>IDataSet</TT> to be displayed in this tab.
     */
    @Override
    public IDataSet createDataSet() throws DataSetException {
        final ISession session = getSession();
        final ISQLConnection conn = session.getSQLConnection();
        ISQLDatabaseMetaData md = session.getMetaData();

        try {
            final Statement stmt = conn.createStatement();
            try {
                final SessionProperties props = session.getProperties();
                if (props.getContentsLimitRows() || _top != null) {
                    try {
                        if (_top != null) {
                            stmt.setMaxRows(_skip + _top);
                        } else {
                            stmt.setMaxRows(_skip + props.getContentsNbrRowsToShow());
                        }
                    } catch (Exception ex) {
                        s_log.error("Error on Statement.setMaxRows()", ex);
                    }
                }
                final ITableInfo ti = getTableInfo();

                /**
                 * When the SessionProperties are set to read-only (either table or text) but
                 * the user has selected "Make Editable" on the Popup menu, we want to limit the
                 * edit capability to only that table, and only for as long as the user is
                 * looking at that one table. When the user switches away to another table, that
                 * new table should not be editable.
                 */
                final String currentTableName = ti.getQualifiedName();
                if (!currentTableName.equals(previousTableName)) {
                    previousTableName = currentTableName; // needed to prevent an infinite loop
                    _dataSetUpdateableTableModel.setEditModeForced(false);

                    /**
                     * Tell the GUI to rebuild itself. Unfortunately, this has the side effect of
                     * calling this same function another time. The second call does not seem to be
                     * a problem, but we need to have reset the previousTableName before makeing
                     * this call or we will be in an infinite loop.
                     */
                    // props.forceTableContentsOutputClassNameChange();
                }

                /**
                 * If the table has a pseudo-column that is the best unique identifier for the
                 * rows (like Oracle's rowid), then we want to include that field in the query
                 * so that it will be available if the user wants to edit the data later.
                 */
                String pseudoColumn = "";

                try {
                    BestRowIdentifier[] rowIDs = md.getBestRowIdentifier(ti);
                    for (int i = 0; i < rowIDs.length; ++i) {
                        short pseudo = rowIDs[i].getPseudoColumn();
                        if (pseudo == DatabaseMetaData.bestRowPseudo) {
                            pseudoColumn = " ," + rowIDs[i].getColumnName();
                            break;
                        }
                    }
                }

                // Some DBMS's (EG Think SQL) throw an exception on a call to
                // getBestRowIdentifier.
                catch (Throwable th) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("getBestRowIdentifier not supported for table " + currentTableName, th);
                    }
                }

                // of objects for getBestRowIdentifier. For PostgreSQL put this kludge in
                // the new function. THis way all the kludges are kept in one place.
                //
                // KLUDGE!!!!!!
                //
                // For some DBs (e.g. PostgreSQL) there is actually a pseudo-column
                // providing the rowId, but the getBestRowIdentifier function is not
                // implemented. This kludge hardcodes the knowledge that specific
                // DBs use a specific pseudo-column. Additionally, as of pg 8.1,
                // you must create the table using "WITH OID" appended to the create
                // statement. Otherwise, OID column is not available by default.
                //
                if (pseudoColumn.length() == 0) {
                    String pc = md.getOptionalPseudoColumnForDataSelection(ti);
                    if (pc != null) {
                        pseudoColumn = ", " + pc;
                    }
                }

                ResultSet rs = null;
                String coded = gatherColumnsForContentSelect(md, ti).toString();

                rs = createResultSet(ti, stmt, coded + pseudoColumn);

                if (null == rs) {
                    pseudoColumn = "";
                    rs = createResultSet(ti, stmt, coded);

                    if (null == rs) {
                        rs = createResultSet(ti, stmt, "*");

                        if (null == rs) {
                            throw new IllegalStateException(
                                    "Failed any way to execute content SQL. See former warning log entries for details.");
                        }
                    }
                }

                // KLUDGE:
                // We want some info about the columns to be available for validating the
                // user input during cell editing operations. Ideally we would get that
                // info inside the ResultSetDataSet class during the creation of the
                // columnDefinition objects by using various functions in ResultSetMetaData
                // such as isNullable(idx). Unfortunately, in at least some DBMSs (e.g.
                // Postgres, HSDB) the results of those calls are not the same (and are less
                // accurate
                // than) the SQLMetaData.getColumns() call used in ColumnsTab to get the column
                // info.
                // Even more unfortunate is the fact that the set of attributes reported on by
                // the two
                // calls is not the same, with the ResultSetMetadata listing things not provided
                // by
                // getColumns. Most of the data provided by the ResultSetMetaData calls is
                // correct.
                // However, the nullable/not-nullable property is not set correctly in at least
                // two
                // DBMSs, while it is correct for those DBMSs in the getColumns() info.
                // Therefore,
                // we collect the collumn nullability information from getColumns() and pass
                // that
                // info to the ResultSet to override what it got from the ResultSetMetaData.
                final ResultSetDataSet rsds = new ResultSetDataSet(md.getColumnInfo(getTableInfo()));
                rsds.setLimitDataRead(true);

                // to allow the fw to save and reload user options related to
                // specific columns, we construct a unique name for the table
                // so the column can be associcated with only that table.
                // Some drivers do not provide the catalog or schema info, so
                // those parts of the name will end up as null. That's ok since
                // this string is never viewed by the user and is just used to
                // distinguish this table from other tables in the DB.
                // We also include the URL used to connect to the DB so that
                // the same table/DB on different machines is treated differently.
                rsds.setContentsTabResultSet(rs, _dataSetUpdateableTableModel.getFullTableName(),
                        DialectFactory.getDialectType(md));
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }

                // ?? remember which column is the rowID (if any) so we can
                // ?? prevent editing on it
                if (pseudoColumn.length() > 0) {
                    _dataSetUpdateableTableModel.setRowIDCol(rsds.getColumnCount() - 1);
                }

                return rsds;
            } finally {
                SQLUtilities.closeStatement(stmt);
            }

        } catch (SQLException ex) {
            throw new DataSetException(ex);
        } finally {
            disposeWaitDialog();
        }
    }

    private ResultSet createResultSet(ITableInfo ti, Statement stmt, String columnsExpression) {
        final StringBuffer buf = new StringBuffer();
        try {

            buf.append("select ").append(columnsExpression).append(" from ").append(ti.getQualifiedName())
                    .append(" tbl");

            String clause = _sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
            if ((clause != null) && (clause.length() > 0)) {
                buf.append(" where ").append(clause);
            }
            clause = _sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
            if ((clause != null) && (clause.length() > 0)) {
                buf.append(" order by ").append(clause);
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("createDataSet running SQL: " + buf.toString());
            }

            // showWaitDialog(stmt);

            ResultSet rs = stmt.executeQuery(buf.toString());
            skip(rs);
            return rs;
        } catch (Throwable e) {
            s_log.warn("Failed to execute content SQL: " + buf.toString(), e);

            // throw (e instanceof RuntimeException ? (RuntimeException)e : new
            // RuntimeException(e));
            return null;
        }
    }

    /**
     * Skip first <code>_skip</code> records in <code>rs</code>
     * 
     * @param rs
     * @throws SQLException
     */
    private void skip(ResultSet rs) throws SQLException {
        int i = 0;
        while (i < _skip && rs.next()) {
            // this is slow and resource expensive...
            // If I am not wrong Hibernate does the same, doesn't it?
            ++i;
        }
    }

    private StringBuilder gatherColumnsForContentSelect(ISQLDatabaseMetaData md, final ITableInfo ti)
            throws SQLException {
        DialectType dialectType = DialectFactory.getDialectType(md);
        TableColumnInfo[] columnInfo = md.getColumnInfo(ti);
        StringBuilder coded = new StringBuilder();
        Component component = getComponent().getViewer().getComponent();

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            for (int i = 0; i < columnInfo.length; ++i) {
                coded.append(CellComponentFactory.getColumnForContentSelect(table, columnInfo[i], dialectType, "tbl."));
                if (i < columnInfo.length - 1) {
                    coded.append(',');
                }
            }
        } else {
            coded.append("*");
        }

        return coded;
    }

    /**
     * Hide the dialog if one is shown
     * 
     * @param _waitDialog the PleaseWaitDialog to close - can be null.
     */
    private void disposeWaitDialog() {
        if (!_prefs.getShowPleaseWaitDialog())
            return;
        GUIUtils.processOnSwingEventThread(new Runnable() {
            @Override
            public void run() {
                if (_waitDialog != null) {
                    _waitDialog.dispose();
                }
            }
        });
    }

    @Override
    public void setDatabaseObjectInfo(IDatabaseObjectInfo value) {
        super.setDatabaseObjectInfo(value);
        _dataSetUpdateableTableModel.setTableInfo(getTableInfo());
    }

    @Override
    public void setSession(ISession session) throws IllegalArgumentException {
        super.setSession(session);
        _dataSetUpdateableTableModel.setSession(session);
    }

    /**
     * return the name of the table that is unambiguous across DB accesses,
     * including the same DB on different machines. This function is static because
     * it is used elsewhere to generate the same name as is used within instances of
     * this class.
     * 
     * @return the name of the table that is unique for this DB access
     */
    public static String getUnambiguousTableName(ISession session, String name) {
        return DataSetUpdateableTableModelImpl.getUnambiguousTableName(session, name);
    }

    ////////////////////////////////////////////////////////
    // Implementataion of IDataSetUpdateableTableModel:
    // Delegation to _dataSetUpdateableTableModel
    @Override
    public String getWarningOnCurrentData(Object[] values, ColumnDisplayDefinition[] colDefs, int col,
            Object oldValue) {
        return _dataSetUpdateableTableModel.getWarningOnCurrentData(values, colDefs, col, oldValue);
    }

    @Override
    public String getWarningOnProjectedUpdate(Object[] values, ColumnDisplayDefinition[] colDefs, int col,
            Object newValue) {
        return _dataSetUpdateableTableModel.getWarningOnProjectedUpdate(values, colDefs, col, newValue);
    }

    @Override
    public Object reReadDatum(Object[] values, ColumnDisplayDefinition[] colDefs, int col, StringBuffer message) {
        return _dataSetUpdateableTableModel.reReadDatum(values, colDefs, col, message);
    }

    @Override
    public String updateTableComponent(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue,
            Object newValue) {
        return _dataSetUpdateableTableModel.updateTableComponent(values, colDefs, col, oldValue, newValue);
    }

    @Override
    public int getRowidCol() {
        return _dataSetUpdateableTableModel.getRowidCol();
    }

    @Override
    public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs) {
        return _dataSetUpdateableTableModel.deleteRows(rowData, colDefs);
    }

    @Override
    public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs) {
        return _dataSetUpdateableTableModel.getDefaultValues(colDefs);
    }

    @Override
    public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs) {
        return _dataSetUpdateableTableModel.insertRow(values, colDefs);
    }

    @Override
    public void addListener(DataSetUpdateableTableModelListener l) {
        _dataSetUpdateableTableModel.addListener(l);
    }

    @Override
    public void removeListener(DataSetUpdateableTableModelListener l) {
        _dataSetUpdateableTableModel.removeListener(l);
    }

    @Override
    public void forceEditMode(boolean mode) {
        _dataSetUpdateableTableModel.forceEditMode(mode);
    }

    @Override
    public boolean editModeIsForced() {
        return _dataSetUpdateableTableModel.editModeIsForced();
    }

    @Override
    protected String getDestinationClassName() {
        return _dataSetUpdateableTableModel.getDestinationClassName();
    }
    //
    //////////////////////////////////////////////////////////////////////////////////
}
