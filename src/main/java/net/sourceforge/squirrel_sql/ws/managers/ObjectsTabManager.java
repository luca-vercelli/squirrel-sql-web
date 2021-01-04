package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ProcedureTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.UDTTypeExpander;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
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

	private boolean initialized = false;

	public void initialize() {
		// Cannot put this in @PostConstruct
		// because at that time DatabaseObjectType.* is not initialized

		if (!initialized) {
			databaseObjectTypes.put("SESSION", DatabaseObjectType.SESSION);
			databaseObjectTypes.put("CATALOG", DatabaseObjectType.CATALOG);
			databaseObjectTypes.put("SCHEMA", DatabaseObjectType.SCHEMA);
			databaseObjectTypes.put("TABLE", DatabaseObjectType.TABLE);
			databaseObjectTypes.put("VIEW", DatabaseObjectType.VIEW);
			databaseObjectTypes.put("TRIGGERTYPE", DatabaseObjectType.TRIGGER_TYPE_DBO);
			databaseObjectTypes.put("TRIGGER", DatabaseObjectType.TRIGGER);
			databaseObjectTypes.put("SYNONYMTYPE", DatabaseObjectType.SYNONYM_TYPE_DBO);
			databaseObjectTypes.put("SYNONYM", DatabaseObjectType.SYNONYM);
			databaseObjectTypes.put("SEQUENCE", DatabaseObjectType.SEQUENCE);
			databaseObjectTypes.put("SEQUENCETYPE", DatabaseObjectType.SEQUENCE_TYPE_DBO);
			databaseObjectTypes.put("DATATYPE", DatabaseObjectType.DATATYPE);
			databaseObjectTypes.put("DATABASETYPE", DatabaseObjectType.DATABASE_TYPE_DBO);
			databaseObjectTypes.put("TABLETYPE", DatabaseObjectType.TABLE_TYPE_DBO);
			databaseObjectTypes.put("TABLE", DatabaseObjectType.TABLE);
			databaseObjectTypes.put("INDEX", DatabaseObjectType.INDEX);
			databaseObjectTypes.put("INDEXTYPE", DatabaseObjectType.INDEX_TYPE_DBO);
			databaseObjectTypes.put("FK", DatabaseObjectType.FOREIGN_KEY);
			databaseObjectTypes.put("PK", DatabaseObjectType.PRIMARY_KEY);
			databaseObjectTypes.put("SEQUENCE", DatabaseObjectType.SEQUENCE);
			databaseObjectTypes.put("PROCTYPE", DatabaseObjectType.PROC_TYPE_DBO);
			databaseObjectTypes.put("FUNCTION", DatabaseObjectType.FUNCTION);
			databaseObjectTypes.put("PROCEDURE", DatabaseObjectType.PROCEDURE);
			databaseObjectTypes.put("UDT", DatabaseObjectType.UDT);
			databaseObjectTypes.put("UDTTYPE", DatabaseObjectType.UDT_TYPE_DBO);
			databaseObjectTypes.put("USER", DatabaseObjectType.USER);
			databaseObjectTypes.put("COLUMN", DatabaseObjectType.COLUMN);
			databaseObjectTypes.put("BESTROWID", DatabaseObjectType.BEST_ROW_ID);
			databaseObjectTypes.put("OTHER", DatabaseObjectType.OTHER);
			for (Entry<String, DatabaseObjectType> entry : databaseObjectTypes.entrySet()) {
				inverseDatabaseObjectTypes.put(entry.getValue(), entry.getKey());
			}
			initialized = true;
		}
	}

	/**
	 * Convert a ObjectTreeNode into a ObjectTreeNodeDto. Recursively with all
	 * children.
	 */
	public ObjectTreeNodeDto node2Dto(ObjectTreeNode node) {
		initialize();
		ObjectTreeNodeDto dto = new ObjectTreeNodeDto();
		IDatabaseObjectInfo info = node.getDatabaseObjectInfo();
		dto.setCatalog(info.getCatalogName());
		dto.setQualifiedName(info.getQualifiedName());
		dto.setSchemaName(info.getSchemaName());
		dto.setSimpleName(info.getSimpleName());
		dto.setObjectTypeI18n(info.getDatabaseObjectType().getName());
		dto.setObjectType(inverseDatabaseObjectTypes.get(info.getDatabaseObjectType()));

		if (info instanceof ProcedureInfo) {
			dto.setProcedureType(((ProcedureInfo) info).getProcedureType());
			dto.setProcedureTypeDescription(((ProcedureInfo) info).getProcedureTypeDescription());
		}

		for (int i = 0; i < node.getChildCount(); ++i) {
			dto.getChildren().add(node2Dto((ObjectTreeNode) node.getChildAt(i)));
		}
		return dto;
	}

	/**
	 * Convert a list of ObjectTreeNode into a list of ObjectTreeNodeDto.
	 * Recursively with all children.
	 */
	public List<ObjectTreeNodeDto> node2Dto(List<ObjectTreeNode> list) {
		initialize();
		List<ObjectTreeNodeDto> listDto = new ArrayList<>();
		for (ObjectTreeNode obj : list) {
			listDto.add(node2Dto(obj));
		}
		return listDto;
	}

	/**
	 * Convert a ObjectTreeNodeDto into a ObjectTreeNode. Recursively with all
	 * children.
	 */
	public ObjectTreeNode dto2Node(ObjectTreeNodeDto dto, ISession session) {
		DatabaseObjectInfo info;
		if (dto.getProcedureType() != null) {
			// this is a procedure, I guess
			info = new ProcedureInfo(dto.getCatalog(), dto.getSchemaName(), dto.getSimpleName(), null,
					dto.getProcedureType(), (SQLDatabaseMetaData) session.getMetaData());
		} else {
			info = new DatabaseObjectInfo(dto.getCatalog(), dto.getSchemaName(), dto.getSimpleName(),
					databaseObjectTypes.get(dto.getObjectType()), session.getMetaData());
		}
		ObjectTreeNode node = new ObjectTreeNode(session, info);
		for (ObjectTreeNodeDto child : dto.getChildren()) {
			node.add(dto2Node(child, session));
		}
		return node;
	}

	/**
	 * Convert a list of ObjectTreeNodeDto into a list of ObjectTreeNode.
	 * Recursively with all children.
	 */
	public List<ObjectTreeNode> dto2Node(List<ObjectTreeNodeDto> listDto, ISession session) {
		List<ObjectTreeNode> list = new ArrayList<>();
		for (ObjectTreeNodeDto dto : listDto) {
			list.add(dto2Node(dto, session));
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

	/**
	 * Create a ObjectTreeNode of type SESSION, then fill all its first level
	 * children
	 * 
	 * @param session
	 * @return
	 * @throws SQLException
	 */
	public ObjectTreeNode createAndExpandRootNode(ISession session) throws SQLException {
		ObjectTreeNode rootNode = createRootNode(session);
		List<ObjectTreeNode> children = expandNode(rootNode);
		for (ObjectTreeNode child : children) {
			rootNode.add(child);
		}
		return rootNode;
	}

	/**
	 * Create a new INodeExpander suitable for given DatabaseObjectType
	 * 
	 * @param type
	 * @param session
	 * @return
	 */
	public INodeExpander getExpanderForType(DatabaseObjectType type, ISession session) {
		if (type == DatabaseObjectType.SESSION || type == DatabaseObjectType.CATALOG
				|| type == DatabaseObjectType.SCHEMA) {
			return new DatabaseExpander(session);
		} else if (type == DatabaseObjectType.TABLE_TYPE_DBO) {
			return new TableTypeExpander();
		} else if (type == DatabaseObjectType.PROC_TYPE_DBO) {
			return new ProcedureTypeExpander();
		} else if (type == DatabaseObjectType.UDT_TYPE_DBO) {
			return new UDTTypeExpander();
		}
		logger.error("Unsupported DatabaseObjectType: " + (type == null ? "null" : type.getName()));
		return null;
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
		INodeExpander expander = getExpanderForType(type, node.getSession());
		if (expander == null) {
			return Collections.emptyList();
		}
		return expander.createChildren(node.getSession(), node);
	}

}
