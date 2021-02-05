package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonSerializableMessageBodyReader extends AbstractMessageBodyReaderWriter<JsonSerializable> {

}
