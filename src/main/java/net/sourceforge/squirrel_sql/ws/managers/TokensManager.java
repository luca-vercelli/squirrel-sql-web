package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.ws.model.User;
import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manager (EJB) for token-based (JWT) authentications. This class require that
 * a secret key is stored in a "server.key" file. You can use
 * generateSecretKey(). Currently implemented through JJWT.
 * 
 * @author lv 2017
 */
// TODO use keystore instead of file
@Stateless
public class TokensManager {

	/**
	 * The server private key used for signing and encoding messages.
	 */
	public Key serverKey = null;

	/**
	 * Token validity in ms
	 */
	public static final long DELAY = 24 * 3600 * 1000; // 1 day

	public final static String SECRET_FILENAME = "server.key";

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	@PostConstruct
	public void postConstructEJB() {
		// either generate or load
		if (!new File(SECRET_FILENAME).exists()) {
			serverKey = generateSecretKey();
		} else {
			serverKey = loadSecretKey();
		}
	}

	/**
	 * Issue a JWT token associated to given userid , then record it to database.
	 * 
	 * @see https://github.com/jwtk/jjwt
	 */
	public String issueToken(User user) {

		Date now = new Date();

		String compactJwts = Jwts.builder() //
				.setSubject(user.getUsername()) //
				.claim("name", user.getName()) //
				.claim("surname", user.getSurname()) //
				.claim("email", user.getEmail()) //
				.claim("roles", user.getRoles()) //
				.setIssuedAt(now) //
				.setExpiration(new Date(now.getTime() + DELAY)) //
				.signWith(SignatureAlgorithm.HS512, serverKey) //
				.compact();

		return compactJwts;
	}

	/**
	 * Check if it was issued by the server and if it's not expired.
	 * 
	 * @see https://github.com/jwtk/jjwt
	 * @param token
	 * @return true if token is valid
	 */
	public boolean validateToken(String token) {

		Jws<Claims> jws;

		try {
			jws = Jwts.parser().setSigningKey(serverKey).parseClaimsJws(token);
		} catch (Exception e) {
			return false;
		}

		// OK, we can trust this JWT (well, JWS). What about its content?

		Claims claims = jws.getBody();

		if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
			return false;
		}

		return true;
	}

	/**
	 * Create User object using token claims.
	 * 
	 * Do not check if token is valid or not.
	 * 
	 * @param token
	 * @return null if given string is not a token
	 */
	public User fromToken(String token) {

		Jws<Claims> jws;

		try {
			jws = Jwts.parser().setSigningKey(serverKey).parseClaimsJws(token);
		} catch (Exception e) {
			return null;
		}

		Claims claims = jws.getBody();
		User user = new User();
		user.setUsername(claims.getSubject());
		user.setName((String) claims.get("name"));
		user.setSurname((String) claims.get("surname"));
		user.setEmail((String) claims.get("email"));
		user.setRoles((String[]) claims.get("roles"));
		return user;
	}

	/**
	 * The new key is a random sequence generated through MacProvider.generateKey(),
	 * to be used for SHA-256.
	 * 
	 * Use this in local to generate key, then save key into server.
	 * 
	 * @see https://stackoverflow.com/questions/11410770
	 */
	public Key generateSecretKey() {
		String filename = SECRET_FILENAME;
		Key key = MacProvider.generateKey();
		FileOutputStream fos;
		try {
			File f = new File(filename).getAbsoluteFile();
			logger.info("Writing server key to " + f.getPath());
			fos = new FileOutputStream(f, false);

			try {
				fos.write(key.getEncoded());
			} finally {
				fos.close();
			}
			return key;

		} catch (IOException e) {
			logger.error("Error generating secret key in " + SECRET_FILENAME, e);
			throw Utilities.wrapRuntime(e);
		}
	}

	/**
	 * Load secret key from server file
	 * 
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/11410770
	 */
	public SecretKey loadSecretKey() {
		String filename = SECRET_FILENAME;
		byte[] keyBytes;
		try {
			keyBytes = Files.readAllBytes(Paths.get(filename));
			return new SecretKeySpec(keyBytes, "SHA-512");
		} catch (IOException e) {
			logger.error("Error loading secret key from " + SECRET_FILENAME, e);
			throw Utilities.wrapRuntime(e);
		}
	}
}