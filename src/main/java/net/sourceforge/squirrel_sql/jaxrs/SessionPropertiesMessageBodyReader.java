package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SessionPropertiesMessageBodyReader extends AbstractMessageBodyReaderWriter<SessionProperties> {

}
