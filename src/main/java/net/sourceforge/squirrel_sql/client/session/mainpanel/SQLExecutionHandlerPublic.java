package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;

/**
 * Just make public the class SQLExecutionHandler
 * 
 * @author lv 2020
 *
 */
public class SQLExecutionHandlerPublic extends SQLExecutionHandler {

	public SQLExecutionHandlerPublic(IResultTab resultTabToReplace, ISession session, String sql,
			ISQLExecutionHandlerListener executionHandlerListener, ISQLExecutionListener[] executionListeners,
			String tableToBeEdited) {
		super(resultTabToReplace, session, sql, executionHandlerListener, executionListeners, tableToBeEdited);
	}

	public SQLExecutionHandlerPublic(IResultTab resultTabToReplace, ISession session, String sql,
			ISQLExecutionHandlerListener executionHandlerListener, ISQLExecutionListener[] executionListeners) {
		super(resultTabToReplace, session, sql, executionHandlerListener, executionListeners);
	}

}
