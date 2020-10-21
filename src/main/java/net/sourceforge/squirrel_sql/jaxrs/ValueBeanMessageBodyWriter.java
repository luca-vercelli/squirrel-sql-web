package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import net.sourceforge.squirrel_sql.dto.ValueBean;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ValueBeanMessageBodyWriter extends AbstractMessageBodyWriter<ValueBean<?>> {

}
