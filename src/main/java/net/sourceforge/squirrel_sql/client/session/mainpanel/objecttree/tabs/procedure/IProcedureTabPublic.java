package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;

/**
 * An IProcedureTab with public access to underlying createDataSet() method
 * 
 * @author lv 2020
 */
public interface IProcedureTabPublic extends IProcedureTab {
    public IDataSet createDataSet() throws DataSetException;
}
