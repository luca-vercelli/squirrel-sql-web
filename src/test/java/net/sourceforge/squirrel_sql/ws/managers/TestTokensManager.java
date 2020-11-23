package net.sourceforge.squirrel_sql.ws.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.model.User;

public class TestTokensManager {

	TokensManager manager;

	@BeforeEach
	public void setUp() {
		// mimic EJB creation
		manager = new TokensManager();
		manager.postConstructEJB();
		manager.setDebugMode(false);
	}

	@Test
	public void testPostConstruct() {
		File f = new File(TokensManager.SECRET_FILENAME);
		assertTrue(f.exists());
		assertTrue(f.length() > 0);
	}

	@Test
	public void testRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		try {
			manager.extractTokenFromRequest(request);
			fail("Should not find token in blank request");
			return;
		} catch (AuthorizationException exc) {
			// ok
		}

		// now mock request
		when(request.getHeader("Authorization")).thenReturn("Bearer mickey");
		String token;
		try {
			token = manager.extractTokenFromRequest(request);
		} catch (AuthorizationException e) {
			e.printStackTrace();
			fail("Should find token");
			return;
		}
		assertEquals("mickey", token);
	}

	@Test
	public void testDebugMode() throws AuthorizationException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		manager.setDebugMode(true);
		String token = manager.extractTokenFromRequest(request);
		assertEquals("*", token);
	}

	@Test
	public void testIssueAndValidate() {
		User u = new User();
		u.setName("Goofy");
		u.setUsername("goofy");
		u.setRoles(new String[] { "admin" });

		String token = manager.issueToken(u);
		assertNotNull(token);

		try {
			manager.validateToken(token);
		} catch (AuthorizationException e) {
			e.printStackTrace();
			fail("Token should be validated");
		}

		String subject = manager.getSubject(token);
		assertEquals("goofy", subject);
	}
}
