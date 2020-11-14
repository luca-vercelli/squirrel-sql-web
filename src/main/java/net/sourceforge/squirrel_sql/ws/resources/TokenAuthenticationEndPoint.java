package net.sourceforge.squirrel_sql.ws.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.dto.ValueBean;
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

	/**
	 * Plain-test authentication point.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticate(@FormParam("username") String username, @FormParam("password") String password) {

		return internalGetToken(username, password);
	}

	/**
	 * JSON authentication point.
	 * 
	 * @param credentials
	 * @return
	 */
	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ValueBean<String> authenticate(Credentials credentials) {

		String token = internalGetToken(credentials.getUsername(), credentials.getPassword());
		return new ValueBean<>(token);
	}

	/**
	 * Common code
	 * 
	 * @param credentials
	 * @return
	 */
	private String internalGetToken(String username, String password) {

		if (username == null || username.isEmpty()) {
			// FIXME what's the best error code?
			throw new WebApplicationException("Missing credentials", Status.UNAUTHORIZED);
		}

		User user = usersManager.authenticate(username, password);

		if (user == null) {
			// FIXME what's the best error code?
			throw new WebApplicationException("Invalid credentials", Status.UNAUTHORIZED);
		}

		// At last, user is authenticated
		logger.info("User authenticated: " + user);

		return tokensManager.issueToken(user);
	}

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
