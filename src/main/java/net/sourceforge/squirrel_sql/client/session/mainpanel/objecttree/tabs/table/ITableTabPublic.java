package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;

/**
 * An ITableTab with public access to underlying createDataSet() method
 * 
 * @author lv 2020
 */
public interface ITableTabPublic extends ITableTab {
	public IDataSet createDataSet() throws DataSetException;
}