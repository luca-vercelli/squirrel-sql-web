package net.sourceforge.squirrel_sql.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class AliasesEndpoint {

	@GET
	@Path("/Aliases")
	public List<String> getItems() {
		return new ArrayList<>();
	}

	@GET
	@Path("/Aliases/{name}")
	public List<String> getItem(String name) {
		return null;
	}
}
