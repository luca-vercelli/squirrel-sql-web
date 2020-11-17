package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.TokensManager;
import net.sourceforge.squirrel_sql.ws.managers.UsersManager;
import net.sourceforge.squirrel_sql.ws.model.User;

/**
 * Authentication endpoint for token-based (JWT) security.
 * 
 * @author lv 2017-2020
 */
@Stateless
@Path("/")
public class TokenAuthenticationEndPoint {

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	@Inject
	UsersManager usersManager;
	@Inject
	TokensManager tokensManager;
	@Context
	HttpServletResponse response;

	/**
	 * Plain-test authentication point.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("Authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticate(@FormParam("username") String username, @FormParam("password") String password) {

		return internalAuthenticate(username, password);
	}

	/**
	 * JSON authentication point.
	 * 
	 * @param credentials
	 * @return
	 */
	@POST
	@Path("Authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ValueBean<String> authenticate(Credentials credentials) {

		String token = internalAuthenticate(credentials.getUsername(), credentials.getPassword());
		return new ValueBean<>(token);
	}

	/**
	 * Return the user that the Authorization token was issued for.
	 * 
	 * @param request
	 * @return
	 * @throws AuthorizationException if token is invalid (this should not happen,
	 *                                if filter is cconfigured properly)
	 */
	@GET
	@Path("/CurrentUser")
	public ValueBean<User> getCurrentUser(@Context HttpServletRequest request) throws AuthorizationException {
		String token = tokensManager.extractTokenFromRequest(request);
		User user = usersManager.findByUsername(tokensManager.getSubject(token));
		return new ValueBean<>(user);
	}

	/**
	 * Common code
	 * 
	 * @param credentials
	 * @return
	 */
	private String internalAuthenticate(String username, String password) {

		if (username == null || username.isEmpty()) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException("Missing credentials", Status.UNAUTHORIZED);
		}

		User user = usersManager.findByUsernamePassword(username, password);

		if (user == null) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, TokensManager.AUTHENTICATION_SCHEME);
			throw new WebApplicationException("Invalid credentials", Status.UNAUTHORIZED);
		}

		// At last, user is authenticated
		logger.info("User authenticated: " + user);

		return tokensManager.issueToken(user);
	}

	/**
	 * This bean can be used from frontend for JSON authentication
	 */
	public static class Credentials {
		private String username;
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
