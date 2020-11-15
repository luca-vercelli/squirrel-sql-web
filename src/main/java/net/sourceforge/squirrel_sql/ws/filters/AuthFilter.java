package net.sourceforge.squirrel_sql.ws.filters;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import net.sourceforge.squirrel_sql.ws.managers.TokensManager;

@Provider
public class AuthFilter implements ContainerRequestFilter {

	@Inject
	TokensManager tokensManager;
	@Context
	HttpServletResponse response;

	@Override
	public void filter(ContainerRequestContext context) {

		String token;
		try {
			// Get the Authorization header from the request context
			token = tokensManager.extractTokenFromContext(context);
		} catch (IllegalArgumentException e) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException(e.getMessage(), Status.UNAUTHORIZED);
		}

		// Check if token is valid
		if (!tokensManager.validateToken(token)) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException("Invalid authorization token", Status.UNAUTHORIZED);
		}
	}
}
