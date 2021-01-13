package net.sourceforge.squirrel_sql.dto;

import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;

public class SchemaInfoDto {

    SchemaInfo schemaInfo;

    public SchemaInfoDto(SchemaInfo schemaInfo) {
        this.schemaInfo = schemaInfo;
    }

    public String[] getSchemas() {
        return schemaInfo.getSchemas();
    }

    public String[] getCatalogs() {
        return schemaInfo.getCatalogs();
    }

    public String[] getFunctions() {
        return schemaInfo.getFunctions();
    }

    public String[] getDataTypes() {
        return schemaInfo.getDataTypes();
    }

    public String[] getTables() {
        return schemaInfo.getTables();
    }

    public String[] getKeywords() {
        return schemaInfo.getKeywords();
    }
}
