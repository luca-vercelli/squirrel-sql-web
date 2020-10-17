package net.sourceforge.squirrel_sql.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DriversEndpoint {

	@GET
	@Path("/Drivers")
	public List<String> getItems() {
		return new ArrayList<>();
	}

	@GET
	@Path("/Drivers/{name}")
	public List<String> getItem(String name) {
		return null;
	}
}
