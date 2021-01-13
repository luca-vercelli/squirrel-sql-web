package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.Statement;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.OrderByClausePanel;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.WhereClausePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

// JUST TRIALS USING HIBERNATE

@Stateless
public class TableContentsManager {

    @Inject
    DriversManager driversManager;

    ILogger s_log = LoggerController.createLogger(TableContentsManager.class);

    /**
     * Return SELECT * FROM given table
     * 
     * @param skip items to skip
     * @param top  items to show
     * @return
     * @throws DataSetException
     */
    public IDataSet getTableContent(ISession session, String catalog, String schema, String table, String type,
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
        // should use existing connection
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

    protected String composeSelectQuery(ITableInfo ti, Statement stmt, String columnsExpression) {
        // cfr. ContentsTab
        final StringBuffer buf = new StringBuffer();
        final SQLFilterClauses sqlFilterClauses = new SQLFilterClauses();

        buf.append("select ").append(columnsExpression).append(" from ").append(ti.getQualifiedName()).append(" tbl");

        String clause = sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
        if ((clause != null) && (clause.length() > 0)) {
            buf.append(" where ").append(clause);
        }
        clause = sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
        if ((clause != null) && (clause.length() > 0)) {
            buf.append(" order by ").append(clause);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("createDataSet running SQL: " + buf.toString());
        }
        return buf.toString();

    }
}
