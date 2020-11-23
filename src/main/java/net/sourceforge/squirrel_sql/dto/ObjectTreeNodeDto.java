package net.sourceforge.squirrel_sql.dto;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

// Both the ObjectTreeNode and the DatabaseObjectInfo cannot be rendered in
// JSON, due to the presence of an Icon
public class ObjectTreeNodeDto {

	private List<ObjectTreeNodeDto> children = new ArrayList<>();
	private String catalog;
	private String qualifiedName;
	private String schemaName;
	private String simpleName;
	private String objectType;

	public ObjectTreeNodeDto() {
	}

	public ObjectTreeNodeDto(ObjectTreeNode node) {
		IDatabaseObjectInfo info = node.getDatabaseObjectInfo();
		this.catalog = info.getCatalogName();
		this.qualifiedName = info.getQualifiedName();
		this.schemaName = info.getSchemaName();
		this.simpleName = info.getSimpleName();
		this.objectType = info.getDatabaseObjectType().getName();
		for (int i = 0; i < node.getChildCount(); ++i) {
			children.add(new ObjectTreeNodeDto((ObjectTreeNode) node.getChildAt(i)));
		}
	}

	public List<ObjectTreeNodeDto> getChildren() {
		return children;
	}

	public void setChildren(List<ObjectTreeNodeDto> children) {
		this.children = children;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
