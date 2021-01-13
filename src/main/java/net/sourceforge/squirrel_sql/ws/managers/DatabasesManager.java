package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.CatalogsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.ConnectionStatusTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.DataTypesTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.IBaseDataSetTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.KeywordsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.MetaDataTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.NumericFunctionsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.SchemasTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.StringFunctionsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.SystemFunctionsTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.TableTypesTabPublic;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.TimeDateFunctionsTabPublic;
import net.sourceforge.squirrel_sql.dto.PlainDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.MetaDataDecoratorDataSet;

/**
 * Manager for catalog/schema tabs.
 * 
 * @author lv 2020-2021
 *
 */
@Stateless
public class DatabasesManager {

    Logger logger = Logger.getLogger(DatabasesManager.class);

    public IDataSet getMetaData(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new MetaDataTabPublic());
    }

    public IDataSet getConnectionStatus(ISession session, String catalog, String schema, String simpleName,
            String objectType) throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new ConnectionStatusTabPublic());
    }

    public IDataSet getCatalogs(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new CatalogsTabPublic());
    }

    public IDataSet getSchemas(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new SchemasTabPublic());
    }

    public IDataSet getTableTypes(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new TableTypesTabPublic());
    }

    public IDataSet getDataTypes(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new DataTypesTabPublic());
    }

    public IDataSet getNumericFunctions(ISession session, String catalog, String schema, String simpleName,
            String objectType) throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new NumericFunctionsTabPublic());
    }

    public IDataSet getStringFunctions(ISession session, String catalog, String schema, String simpleName,
            String objectType) throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new StringFunctionsTabPublic());
    }

    public IDataSet getSystemFunctions(ISession session, String catalog, String schema, String simpleName,
            String objectType) throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new SystemFunctionsTabPublic());
    }

    public IDataSet getTimeDateFunctions(ISession session, String catalog, String schema, String simpleName,
            String objectType) throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new TimeDateFunctionsTabPublic());
    }

    public IDataSet getKeywords(ISession session, String catalog, String schema, String simpleName, String objectType)
            throws DataSetException {
        return commonGetDataSetDb(session, catalog, schema, simpleName, objectType, new KeywordsTabPublic());
    }

    private IDataSet commonGetDataSetDb(ISession session, String catalog, String schema, String simpleName,
            String objectType, IBaseDataSetTabPublic tab) throws DataSetException {

        DatabaseObjectInfo info = new DatabaseObjectInfo(catalog, schema, simpleName);
        tab.setSession(session);
        tab.setDatabaseObjectInfo(info);
        IDataSet result = tab.createDataSet();

        if (result instanceof ObjectArrayDataSet || result instanceof MetaDataDecoratorDataSet
                || result instanceof JavabeanDataSet || result instanceof DatabaseTypesDataSet) {
            // Well, more or less every kind of IDataSet but the ResultSetDataSet
            result = new PlainDataSet(result);
        }

        return result;
    }
}
