package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
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

	protected Map<String, DatabaseObjectType> databaseObjectTypes = new HashMap<>();
	protected Map<DatabaseObjectType, String> inverseDatabaseObjectTypes = new HashMap<>();

	@PostConstruct
	protected void init() {
		databaseObjectTypes.put("SESSION", DatabaseObjectType.SESSION);
		databaseObjectTypes.put("CATALOG", DatabaseObjectType.CATALOG);
		databaseObjectTypes.put("SCHEMA", DatabaseObjectType.SCHEMA);
		databaseObjectTypes.put("TABLE", DatabaseObjectType.TABLE);
		databaseObjectTypes.put("DATATYPE", DatabaseObjectType.DATATYPE);
		databaseObjectTypes.put("TABLE", DatabaseObjectType.TABLE);
		databaseObjectTypes.put("INDEX", DatabaseObjectType.INDEX);
		databaseObjectTypes.put("SEQUENCE", DatabaseObjectType.SEQUENCE);
		databaseObjectTypes.put("FUNCTION", DatabaseObjectType.FUNCTION);
		databaseObjectTypes.put("PROCEDURE", DatabaseObjectType.PROCEDURE);
		databaseObjectTypes.put("OTHER", DatabaseObjectType.OTHER);
		for (Entry<String, DatabaseObjectType> entry : databaseObjectTypes.entrySet()) {
			inverseDatabaseObjectTypes.put(entry.getValue(), entry.getKey());
		}
	}

	/**
	 * Convert a ObjectTreeNode into a ObjectTreeNodeDto. Recursively with all
	 * children.
	 */
	public ObjectTreeNodeDto convert(ObjectTreeNode node) {
		ObjectTreeNodeDto dto = new ObjectTreeNodeDto();
		IDatabaseObjectInfo info = node.getDatabaseObjectInfo();
		dto.setCatalog(info.getCatalogName());
		dto.setQualifiedName(info.getQualifiedName());
		dto.setSchemaName(info.getSchemaName());
		dto.setSimpleName(info.getSimpleName());
		dto.setObjectTypeI18n(info.getDatabaseObjectType().getName());
		dto.setObjectType(inverseDatabaseObjectTypes.get(info.getDatabaseObjectType()));
		for (int i = 0; i < node.getChildCount(); ++i) {
			dto.getChildren().add(convert((ObjectTreeNode) node.getChildAt(i)));
		}
		return dto;
	}

	public List<ObjectTreeNodeDto> convert(List<ObjectTreeNode> list) {
		List<ObjectTreeNodeDto> listDto = new ArrayList<>();
		for (ObjectTreeNode obj : list) {
			listDto.add(convert(obj));
		}
		return listDto;
	}

	/**
	 * Convert a ObjectTreeNodeDto into a ObjectTreeNode. Recursively with all
	 * children.
	 */
	public ObjectTreeNode convert(ObjectTreeNodeDto dto, ISession session) {
		DatabaseObjectInfo info = new DatabaseObjectInfo(dto.getCatalog(), dto.getSchemaName(), dto.getSimpleName(),
				databaseObjectTypes.get(dto.getObjectType()), session.getMetaData());
		ObjectTreeNode node = new ObjectTreeNode(session, info);
		for (ObjectTreeNodeDto child : dto.getChildren()) {
			node.add(convert(child, session));
		}
		return node;
	}

	public List<ObjectTreeNode> convert(List<ObjectTreeNodeDto> listDto, ISession session) {
		List<ObjectTreeNode> list = new ArrayList<>();
		for (ObjectTreeNodeDto dto : listDto) {
			list.add(convert(dto, session));
		}
		return list;
	}

	/**
	 * Create a ObjectTreeNode of type SESSION
	 * 
	 * This is essentially a RootNode.
	 * 
	 * @param session
	 * @return
	 */
	public ObjectTreeNode createRootNode(ISession session) {
		DatabaseObjectInfo info = new DatabaseObjectInfo(null, null, session.getAlias().getName(),
				DatabaseObjectType.SESSION, session.getMetaData());
		ObjectTreeNode node = new ObjectTreeNode(session, info);
		return node;
	}

	public List<ObjectTreeNode> expandSessionNode(ISession session) throws SQLException {
		ObjectTreeNode node = createRootNode(session);
		return expandDatabaseNode(node);
	}

	/**
	 * Create a ObjectTreeNode of type SESSION, then fill all its first level
	 * children
	 * 
	 * @param session
	 * @return
	 * @throws SQLException
	 */
	public ObjectTreeNode createExpandedRootNode(ISession session) throws SQLException {
		ObjectTreeNode rootNode = createRootNode(session);
		List<ObjectTreeNode> children = expandDatabaseNode(rootNode);
		for (ObjectTreeNode child : children) {
			rootNode.add(child);
		}
		return rootNode;
	}

	/**
	 * Expand given node, assuming it is of type SESSION, or CATALOG, or SCHEMA
	 * 
	 * @param node
	 * @return
	 * @throws SQLException
	 */
	protected List<ObjectTreeNode> expandDatabaseNode(ObjectTreeNode node) throws SQLException {
		INodeExpander expander = new DatabaseExpander(node.getSession());
		return expander.createChildren(node.getSession(), node);
	}

	/**
	 * Expand given node
	 * 
	 * @param node
	 * @return
	 * @throws SQLException
	 */
	public List<ObjectTreeNode> expandNode(ObjectTreeNode node) throws SQLException {
		DatabaseObjectType type = node.getDatabaseObjectType();
		if (type == DatabaseObjectType.SESSION || type == DatabaseObjectType.CATALOG
				|| type == DatabaseObjectType.SCHEMA) {
			return expandDatabaseNode(node);
		}
		// TODO
		return null;
	}

}
