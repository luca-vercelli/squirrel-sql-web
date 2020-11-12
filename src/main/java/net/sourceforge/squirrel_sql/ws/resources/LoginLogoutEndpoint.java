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

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.filters.AuthFilter;
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
			throw new WebApplicationException("Invalid credentials", 404);
		} else {
			request.getSession().setAttribute(AuthFilter.USER_SESSION_ATTRIBUTE, user);
			return new ValueBean<>(user);
		}
	}

	@GET
	@Path("/CurrentUser")
	public ValueBean<User> user() {
		User user = (User) request.getSession().getAttribute(AuthFilter.USER_SESSION_ATTRIBUTE);
		return new ValueBean<>(user);
	}

	@POST
	@Path("/Logout")
	public void logout() {
		request.getSession().removeAttribute(AuthFilter.USER_SESSION_ATTRIBUTE);
	}
}
