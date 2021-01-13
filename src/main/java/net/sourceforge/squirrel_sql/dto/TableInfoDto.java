package net.sourceforge.squirrel_sql.dto;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public class TableInfoDto {

    ITableInfo tableInfo;

    public TableInfoDto(ITableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getQualifiedName() {
        return tableInfo.getQualifiedName();
    }

    public String getCatalogName() {
        return tableInfo.getCatalogName();
    }

    public String getRemarks() {
        return tableInfo.getRemarks();
    }

    public String getSchemaName() {
        return tableInfo.getSchemaName();
    }

    public String getSimpleName() {
        return tableInfo.getSimpleName();
    }

    public String getType() {
        return tableInfo.getType();
    }

}
