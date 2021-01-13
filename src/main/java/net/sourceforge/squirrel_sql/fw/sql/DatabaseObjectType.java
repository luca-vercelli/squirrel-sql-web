package net.sourceforge.squirrel_sql.fw.sql;

/*

This class was decompiled and modified by squirrel-sql-web

*/

import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DatabaseObjectType implements IHasIdentifier, Serializable {
    private static final long serialVersionUID = 1792032999029273091L;

    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DatabaseObjectType.class);

    private static final IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

    public static DatabaseObjectType OTHER;

    public static DatabaseObjectType BEST_ROW_ID;

    public static DatabaseObjectType CATALOG;

    public static DatabaseObjectType COLUMN;

    public static DatabaseObjectType SESSION;

    public static DatabaseObjectType DATABASE_TYPE_DBO;

    public static DatabaseObjectType DATATYPE;

    public static DatabaseObjectType PRIMARY_KEY;

    public static DatabaseObjectType FOREIGN_KEY;

    public static DatabaseObjectType FUNCTION;

    public static DatabaseObjectType INDEX_TYPE_DBO;

    public static DatabaseObjectType INDEX;

    public static DatabaseObjectType PROCEDURE;

    public static DatabaseObjectType PROC_TYPE_DBO;

    public static DatabaseObjectType SCHEMA;

    public static DatabaseObjectType SEQUENCE_TYPE_DBO;

    public static DatabaseObjectType SEQUENCE;

    public static DatabaseObjectType SYNONYM_TYPE_DBO;

    public static DatabaseObjectType SYNONYM;

    public static DatabaseObjectType TABLE;

    public static DatabaseObjectType TABLE_TYPE_DBO;

    public static DatabaseObjectType VIEW;

    public static DatabaseObjectType TRIGGER_TYPE_DBO;

    public static DatabaseObjectType TRIGGER;

    public static DatabaseObjectType UDT;

    public static DatabaseObjectType UDT_TYPE_DBO;

    public static DatabaseObjectType UDF;

    public static DatabaseObjectType UDF_TYPE_DBO;

    public static DatabaseObjectType USER;

    private final IIdentifier _id;

    private final String _name;

    private String _keyForSerializationReplace;

    private Icon _icon;

    private boolean _isContainerNode;

    // squirrel-sql-web
    // In squirrel-sql-core, these objects are initialized in a static code
    // In squirrel-sql-web, this is not possible, at that time you get Exception:
    // "Must not create bundle before locale was initalized"
    public static void initialize() {

        OTHER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.other");

        BEST_ROW_ID = createNewDatabaseObjectTypeI18n("DatabaseObjectType.bestRowID");

        CATALOG = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog", "dot.catalog");

        COLUMN = createNewDatabaseObjectTypeI18n("DatabaseObjectType.column");

        SESSION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.database", "dot.database");

        DATABASE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.DATABASE_TYPE_DBO", "dot.database");

        DATATYPE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.datatype", "dot.datatype");

        PRIMARY_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.primarykey");

        FOREIGN_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.foreignkey");

        FUNCTION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.function", "dot.function");

        INDEX_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.INDEX_TYPE_DBO", "dot.indexes", true);

        INDEX = createNewDatabaseObjectTypeI18n("DatabaseObjectType.index", "dot.index");

        PROCEDURE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.storproc", "dot.procedure");

        PROC_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.PROC_TYPE_DBO", "dot.procedures", true);

        SCHEMA = createNewDatabaseObjectTypeI18n("DatabaseObjectType.schema", "dot.schema");

        SEQUENCE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SEQUENCE_TYPE_DBO", "dot.sequence");

        SEQUENCE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.sequence", "dot.sequences", true);

        SYNONYM_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SYNONYM_TYPE_DBO");

        SYNONYM = createNewDatabaseObjectTypeI18n("DatabaseObjectType.synonym");

        TABLE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.table", "dot.table");

        TABLE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TABLE_TYPE_DBO", "dot.tables", true);

        VIEW = createNewDatabaseObjectTypeI18n("DatabaseObjectType.view", "dot.view");

        TRIGGER_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TRIGGER_TYPE_DBO", "dot.triggers", true);

        TRIGGER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog", "dot.trigger");

        UDT = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udt", "dot.datatype");

        UDT_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDT_TYPE_DBO", "dot.datatypes", true);

        UDF = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udf");

        UDF_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDF_TYPE_DBO");

        USER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.user", "dot.user");
    }

    private DatabaseObjectType(String name, String keyForSerializationReplace, Icon icon) {
        this._keyForSerializationReplace = keyForSerializationReplace;
        this._icon = icon;
        this._id = s_idFactory.createIdentifier();
        this._name = (name != null) ? name : this._id.toString();
    }

    public IIdentifier getIdentifier() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public String getKeyForSerializationReplace() {
        return this._keyForSerializationReplace;
    }

    public Icon getIcon() {
        return this._icon;
    }

    public String toString() {
        return getName();
    }

    private static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key, String imageName) {
        return createNewDatabaseObjectTypeI18n(key, imageName, false);
    }

    private static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key, String imageName,
            boolean isContainerNode) {
        ImageIcon icon = null;
        if (null != imageName)
            icon = (new LibraryResources()).getIcon(imageName);
        DatabaseObjectType ret = createNewDatabaseObjectType(s_stringMgr.getString(key), icon);
        ret.setContainerNode(isContainerNode);
        return ret;
    }

    private void setContainerNode(boolean isContainerNode) {
        this._isContainerNode = isContainerNode;
    }

    public boolean isContainerNode() {
        return this._isContainerNode;
    }

    private static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key) {
        return createNewDatabaseObjectTypeI18n(key, null);
    }

    public static DatabaseObjectType createNewDatabaseObjectType(String key) {
        return createNewDatabaseObjectType(key, null);
    }

    public static DatabaseObjectType createNewDatabaseObjectType(String key, Icon icon) {
        return new DatabaseObjectType(key, key, icon);
    }
}
