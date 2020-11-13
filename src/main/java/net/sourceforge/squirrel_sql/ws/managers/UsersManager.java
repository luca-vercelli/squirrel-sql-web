package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.ws.filters.AuthFilter;
import net.sourceforge.squirrel_sql.ws.model.User;

/**
 * Handle users in Users.xml file
 * 
 * @author lv 2020
 *
 */
@Stateless
public class UsersManager {

	private File usersFile;

	@Inject
	WebApplication webapp;

	Logger logger = Logger.getLogger(UsersManager.class);

	@PostConstruct
	public void init() {
		usersFile = new File(webapp.getAppFiles().getUserSettingsDirectory(), "Users.xml");
		if (!usersFile.exists()) {
			createDefaultUsersFile();
		}
	}

	/**
	 * Get full Users.xml location. This should better be inside ApplicationFiles.
	 * 
	 * @return
	 */
	public File getUsersFile() {
		return usersFile;
	}

	/**
	 * Write (or rewrite) a Users.xml containg a single default user.
	 */
	protected void createDefaultUsersFile() {
		// cfr. code from AliasListHolder

		User u = new User();
		u.setUsername("admin");
		u.setPassword("admin");
		u.setSurname("John");
		u.setName("Doe");
		u.setEmail("john.doe@example.com");
		u.setRoles(new String[] { "admin" });

		List<User> list = new ArrayList<>();
		list.add(u);

		saveList(list, getUsersFile());
	}

	/**
	 * Save given beans to Users.xml file.
	 * 
	 * @param list
	 * @param file
	 */
	protected void saveList(List<User> list, File file) {
		// cfr. code from AliasListHolder
		try {
			XMLBeanWriter xmlBeanWriter = new XMLBeanWriter();
			xmlBeanWriter.addIteratorToRoot(list.iterator());
			xmlBeanWriter.save(file);
		} catch (Exception e) {
			logger.error(e);
			throw Utilities.wrapRuntime(e);
		}
	}

	/**
	 * Load beans list from Users.xml file.
	 * 
	 * @param file
	 * @return
	 */
	protected List<User> readList(File file) {
		// cfr. code from AliasListHolder
		try {
			List<User> list = new ArrayList<>();

			XMLBeanReader xmlBeanReader = new XMLBeanReader();
			xmlBeanReader.load(file, getClass().getClassLoader());

			for (Object bean : xmlBeanReader.getBeans()) {
				final User user = (User) bean;
				if (false == list.contains(user)) {
					list.add(user);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e);
			throw Utilities.wrapRuntime(e);
		}
	}

	/**
	 * Check if given credentials are present on Users.xml file.
	 * 
	 * @param username
	 * @param password
	 * @return User, or null if not found
	 */
	public User authenticate(String username, String password) {

		List<User> list = readList(getUsersFile());

		for (User u : list) {
			if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
				list.clear();
				return u;
			}
		}

		// not found
		return null;
	}

	/**
	 * Set User into HTTP Session
	 */
	public void setLoggedUser(User user, HttpServletRequest request) {
		request.getSession().setAttribute(AuthFilter.USER_SESSION_ATTRIBUTE, user);
	}

	/**
	 * Get User form HTTP Session
	 */
	public User getLoggedUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(AuthFilter.USER_SESSION_ATTRIBUTE);
	}

	/**
	 * Remove User form HTTP Session, if any
	 */
	public void clearLoggedUser(HttpServletRequest request) {
		request.getSession().removeAttribute(AuthFilter.USER_SESSION_ATTRIBUTE);
	}
}
