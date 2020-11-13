package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.UsersManager;
import net.sourceforge.squirrel_sql.ws.model.User;

@Path("/")
@Stateless
public class LoginLogoutEndpoint {

	@Inject
	UsersManager manager;

	@Context
	private HttpServletRequest request;

	@POST
	@Path("/Login")
	public ValueBean<User> login(@FormParam("username") String username, @FormParam("password") String password) {

		logout();

		User user = manager.authenticate(username, password);
		if (user == null) {
			throw new WebApplicationException("Invalid credentials", Status.FORBIDDEN);
		} else {
			manager.setLoggedUser(user, request);
			return new ValueBean<>(user);
		}
	}

	@GET
	@Path("/CurrentUser")
	public ValueBean<User> user() {
		User user = manager.getLoggedUser(request);
		// technically, this method could return null
		// however, in that case the Filter would not even call this method
		return new ValueBean<>(user);
	}

	@POST
	@Path("/Logout")
	public void logout() {
		manager.clearLoggedUser(request);
	}
}
