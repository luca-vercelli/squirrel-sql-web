package net.sourceforge.squirrel_sql.ws.filters;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.TokensManager;

@Provider
@Stateless
public class AuthFilter implements ContainerRequestFilter {

	@Inject
	TokensManager tokensManager;
	@Context
	HttpServletResponse response;

	Logger logger = Logger.getLogger(AuthFilter.class);

	@Override
	public void filter(ContainerRequestContext context) {

		if (context.getUriInfo().getPath().equals("Authenticate") || tokensManager.isDebugMode()) {
			return;
		}

		String token;
		try {
			// Get the Authorization header from the request context
			token = tokensManager.extractTokenFromContext(context);
		} catch (AuthorizationException e) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException(e.getMessage(), Status.UNAUTHORIZED);
		}

		try {
			// Check if token is valid
			tokensManager.validateToken(token);
		} catch (AuthorizationException e) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException(e.getMessage(), Status.UNAUTHORIZED);
		}
	}
}
