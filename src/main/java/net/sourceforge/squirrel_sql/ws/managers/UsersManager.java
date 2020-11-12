package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.ws.model.User;

@Stateless
public class UsersManager {

	private File usersFile;

	@Inject
	WebApplication webapp;

	Logger logger = Logger.getLogger(UsersManager.class);

	@PostConstruct
	public void init() {
		logger.info("UsersManager::init");
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
		logger.info("UsersManager::createDefaultUsersFile");

		// cfr. code from AliasListHolder
		User u = new User();
		u.setUsername("admin");
		u.setSurname("John");
		u.setName("Doe");
		u.setEmail("john.doe@example.com");
		u.getRoles().add("admin");

		List<User> list = new ArrayList<>();
		list.add(u);

		saveList(list, getUsersFile());

		logger.info("UsersManager::createDefaultUsersFile END");
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
			logger.info("UsersManager::saveList");
			XMLBeanWriter xmlBeanWriter = new XMLBeanWriter();
			xmlBeanWriter.addIteratorToRoot(list.iterator());
			xmlBeanWriter.save(file);
			logger.info("UsersManager::saveList END");
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

		logger.info("UsersManager::authenticate");

		List<User> list = readList(getUsersFile());

		for (User u : list) {
			if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
				list.clear();
				// so that GC cleans objects ASAP
				return u;
			}
		}

		// not found
		return null;
	}
}
