package net.sourceforge.squirrel_sql.dto;

import java.util.ArrayList;
import java.util.List;

// Both the ObjectTreeNode and the DatabaseObjectInfo cannot be rendered in
// JSON, due to the presence of an Icon
public class ObjectTreeNodeDto {

    private List<ObjectTreeNodeDto> children = new ArrayList<>();
    private String catalog;
    private String qualifiedName;
    private String schemaName;
    private String simpleName;
    private String objectTypeI18n;
    private String objectType;
    private Integer procedureType;
    private String procedureTypeDescription;

    public ObjectTreeNodeDto() {
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

    public String getObjectTypeI18n() {
        return objectTypeI18n;
    }

    public void setObjectTypeI18n(String objectTypeI18n) {
        this.objectTypeI18n = objectTypeI18n;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Integer getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(Integer procedureType) {
        this.procedureType = procedureType;
    }

    public String getProcedureTypeDescription() {
        return procedureTypeDescription;
    }

    public void setProcedureTypeDescription(String procedureTypeDescription) {
        this.procedureTypeDescription = procedureTypeDescription;
    }
}
