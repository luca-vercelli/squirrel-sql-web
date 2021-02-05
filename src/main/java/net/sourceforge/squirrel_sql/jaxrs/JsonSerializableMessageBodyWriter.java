package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonSerializableMessageBodyWriter extends AbstractMessageBodyReaderWriter<JsonSerializable> {

}
