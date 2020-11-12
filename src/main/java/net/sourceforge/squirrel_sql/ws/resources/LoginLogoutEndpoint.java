package net.sourceforge.squirrel_sql.ws.resources;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.UsersManager;
import net.sourceforge.squirrel_sql.ws.model.User;

@Path("/")
@Stateless
public class LoginLogoutEndpoint {

	@Inject
	UsersManager manager;

	@POST
	@Path("/Login")
	public ValueBean<User> login(@FormParam("username") String username, @FormParam("password") String password)
			throws IOException {
		User user = manager.authenticate(username, password);
		if (user == null) {
			throw new WebApplicationException("Invalid credentials", 404);
		}
		return new ValueBean<>(user);
	}

	@POST
	@Path("/Logout")
	public void logout() {
	}
}
