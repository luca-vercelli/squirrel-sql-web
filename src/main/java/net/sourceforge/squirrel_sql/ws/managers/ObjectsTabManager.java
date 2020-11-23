package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manages retrieval of database metadata
 * 
 * @author lv 2020
 *
 */
@Stateless
public class ObjectsTabManager {

	@Inject
	WebApplication webapp;
	@Inject
	SessionsManager sessionsManager;

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	public ObjectTreeNode createRootNode(ISession session) {
		DatabaseObjectInfo info = new DatabaseObjectInfo(null, null, session.getAlias().getName(),
				DatabaseObjectType.SESSION, session.getMetaData());
		// this is essentially a RootNode
		ObjectTreeNode node = new ObjectTreeNode(session, info);
		return node;
	}

	public List<ObjectTreeNode> expandSessionNode(ISession session) throws SQLException {
		ObjectTreeNode node = createRootNode(session);
		return expandSessionNode(node);
	}

	protected List<ObjectTreeNode> expandSessionNode(ObjectTreeNode node) throws SQLException {
		INodeExpander expander = new DatabaseExpander(node.getSession());
		return expander.createChildren(node.getSession(), node);
	}

	protected List<ObjectTreeNode> expandNode(ObjectTreeNode node) throws SQLException {
		if (node.getDatabaseObjectType() == DatabaseObjectType.SESSION) {
			return expandSessionNode(node);
		}
		// TODO
		return null;
	}

}
