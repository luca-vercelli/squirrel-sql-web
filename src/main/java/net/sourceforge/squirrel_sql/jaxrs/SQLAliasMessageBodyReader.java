package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SQLAliasMessageBodyReader extends AbstractMessageBodyReaderWriter<SQLAlias> {

}
