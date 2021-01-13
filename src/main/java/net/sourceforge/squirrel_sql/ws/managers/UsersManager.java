package net.sourceforge.squirrel_sql.ws.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
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
        u.setIdentifier(1);
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
    protected synchronized void saveList(List<User> list, File file) {
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
                if (!list.contains(user)) {
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
    public User findByUsernamePassword(String username, String password) {
        User u = findByUsername(username);
        if (u != null && u.getPassword() != null && u.getPassword().equals(password)) {
            return u;
        } else {
            // not found
            return null;
        }
    }

    /**
     * Search for user on Users.xml file.
     * 
     * @param username
     * @return User, or null if not found
     */
    public User findByUsername(String username) {

        List<User> list = readList(getUsersFile());
        for (User u : list) {
            if (u.getUsername().equals(username)) {
                list.clear();
                return u;
            }
        }

        // not found
        return null;
    }

    /**
     * Search for user on Users.xml file.
     * 
     * @param identifier
     * @return User, or null if not found
     */
    public User findByIdentifier(Integer identifier) {

        List<User> list = readList(getUsersFile());
        for (User u : list) {
            if (u.getIdentifier() == identifier) {
                list.clear();
                return u;
            }
        }

        // not found
        return null;
    }

    /**
     * Return all users in Users.xml file.
     */
    public List<User> findAll() {
        return readList(getUsersFile());
    }

    public User createNewUser(User item) {
        item.setIdentifier(newIdentifier());
        List<User> list = readList(getUsersFile());
        checkDuplicatedUserName(list, item);
        list.add(item);
        saveList(list, getUsersFile());
        list.clear();
        return item;
    }

    public User updateUser(User item, int identifier) {
        List<User> list = readList(getUsersFile());
        int index = findUserIndex(list, identifier);
        if (index == -1) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        User oldItem = list.get(index);
        oldItem.setEmail(item.getEmail());
        oldItem.setName(item.getName());
        oldItem.setRoles(item.getRoles());
        oldItem.setSurname(item.getSurname());
        oldItem.setUsername(item.getUsername());

        checkDuplicatedUserName(list, oldItem);
        saveList(list, getUsersFile());
        return oldItem;
    }

    public void removeUser(Integer identifier) {
        List<User> list = readList(getUsersFile());
        int index = findUserIndex(list, identifier);
        if (index == -1) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        list.remove(index);
        saveList(list, getUsersFile());
        list.clear();
    }

    private int findUserIndex(List<User> list, int identifier) {
        int index = -1;
        for (User u : list) {
            index++;
            if (u.getIdentifier() == identifier) {
                break;
            }
        }
        return index;
    }

    private void checkDuplicatedUserName(List<User> list, User user) {
        for (User u : list) {
            if (u.getUsername().equals(user.getUsername()) && !u.equals(user)) {
                throw new WebApplicationException("Duplicated username", Status.BAD_REQUEST);
            }
        }
    }

    // this is static for concurrency reasons
    private static Integer maxId = 0;

    public synchronized Integer newIdentifier() {
        List<User> list = readList(getUsersFile());
        for (User u : list) {
            if (u.getIdentifier() > maxId) {
                maxId = u.getIdentifier();
            }
        }
        return ++maxId;
    }
}
