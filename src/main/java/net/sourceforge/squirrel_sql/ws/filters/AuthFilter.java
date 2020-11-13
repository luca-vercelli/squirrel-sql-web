package net.sourceforge.squirrel_sql.ws.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthFilter implements ContainerRequestFilter {

	@Context
	private HttpServletRequest request;

	public final static String USER_SESSION_ATTRIBUTE = "_user";

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		if (!context.getUriInfo().getPath().endsWith("Login")
				&& request.getSession().getAttribute(USER_SESSION_ATTRIBUTE) == null) {
			throw new WebApplicationException("User not authenticated", Status.FORBIDDEN);
		}
	}

}
