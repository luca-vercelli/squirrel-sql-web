package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;

/**
 * An BaseDataSetTab with public access to underlying createDataSet() method
 * 
 * @author lv 2020
 */
public interface IBaseDataSetTabPublic extends IObjectTab {
	public IDataSet createDataSet() throws DataSetException;
}