package net.sourceforge.squirrel_sql.ws.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class TestEndpoint {

	@GET
	@Path("/HelloWorld")
	public String helloWorld() {
		return "Hello World";
	}
}
