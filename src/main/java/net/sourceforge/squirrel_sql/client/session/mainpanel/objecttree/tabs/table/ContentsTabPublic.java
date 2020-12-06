package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;

public class ContentsTabPublic extends ContentsTab implements ITableTabPublic {
	// FIXME this does not work, we cannot use ObjectTreePanel's at all
	public ContentsTabPublic(ISession session) {
		super(new ObjectTreePanel(session, null));
		super.setSession(session);
	}

	@Override
	public IDataSet createDataSet() throws DataSetException {
		return super.createDataSet();
	}
}