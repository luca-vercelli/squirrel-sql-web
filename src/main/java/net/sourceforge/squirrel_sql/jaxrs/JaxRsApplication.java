package net.sourceforge.squirrel_sql.jaxrs;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationScoped
@ApplicationPath("/ws")
public class JaxRsApplication extends Application {

}
