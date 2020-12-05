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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ProcedureTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.UDTTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ColumnsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ITableTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.IndexesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PrimaryKeyTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.RowCountTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.TablePriviligesTab;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
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
		DatabaseObjectInfo info = new DatabaseObjectInfo(dto.getCatalog(), dto.getSchemaName(), dto.getSimpleName(),
				databaseObjectTypes.get(dto.getObjectType()), session.getMetaData());
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

	/**
	 * Return SELECT * FROM given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableContent(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new ContentsTabPublic(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Return SELECT COUNT(*) FROM given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableRowCount(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new RowCountTabPublic();
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Return primary keys of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTablePk(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new PrimaryKeyTabPublic();
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Return columns definitions of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableColumns(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new ColumnsTabPublic();
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Return indexes of given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTableIndexes(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new IndexesTabPublic();
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * Return privileges on given table
	 * 
	 * @return
	 * @throws DataSetException
	 */
	public IDataSet getTablePrivileges(ISession session, String catalog, String schema, String table, String type)
			throws DataSetException {

		// One can also think to take a ObjectTreeNode as input

		TableInfo info = new TableInfo(catalog, schema, table, type, null, session.getMetaData());
		ITableTabPublic tab = new TablePriviligesTabPublic();
		tab.setSession(session);
		tab.setTableInfo(info);
		IDataSet result = tab.createDataSet();
		return result;
	}

	/**
	 * An ITableTab with public access to underlying createDataSet() method
	 * 
	 * @author lv 2020
	 */
	public static interface ITableTabPublic extends ITableTab {
		public IDataSet createDataSet() throws DataSetException;
	}

	private static class ContentsTabPublic extends ContentsTab implements ITableTabPublic {
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

	private static class RowCountTabPublic extends RowCountTab implements ITableTabPublic {
		@Override
		public IDataSet createDataSet() throws DataSetException {
			return super.createDataSet();
		}
	}

	private static class PrimaryKeyTabPublic extends PrimaryKeyTab implements ITableTabPublic {
		@Override
		public IDataSet createDataSet() throws DataSetException {
			return super.createDataSet();
		}
	}

	private static class ColumnsTabPublic extends ColumnsTab implements ITableTabPublic {
		@Override
		public IDataSet createDataSet() throws DataSetException {
			return super.createDataSet();
		}
	}

	private static class IndexesTabPublic extends IndexesTab implements ITableTabPublic {
		@Override
		public IDataSet createDataSet() throws DataSetException {
			return super.createDataSet();
		}
	}

	private static class TablePriviligesTabPublic extends TablePriviligesTab implements ITableTabPublic {
		@Override
		public IDataSet createDataSet() throws DataSetException {
			return super.createDataSet();
		}
	}
}
