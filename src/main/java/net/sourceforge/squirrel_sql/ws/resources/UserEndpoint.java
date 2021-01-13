package net.sourceforge.squirrel_sql.ws.resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.ws.managers.UsersManager;
import net.sourceforge.squirrel_sql.ws.model.User;

/**
 * Users endpoints.
 * 
 * @author lv 2021
 */
@Stateless
@Path("/")
public class UserEndpoint {

    Logger logger = Logger.getLogger(SessionsEndpoint.class);

    @Inject
    UsersManager manager;

    @GET
    @Path("/Users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> findAll() {

        return manager.findAll();
    }

    @POST
    @Path("/Users")
    public ValueBean<User> createItem(User item) {
        item = manager.createNewUser(item);
        return new ValueBean<>(item);
    }

    @PUT
    @Path("/Users({identifier})")
    public ValueBean<User> updateItem(@PathParam("identifier") Integer identifier, User item) {
        item = manager.updateUser(item, identifier);
        return new ValueBean<>(item);
    }

    @DELETE
    @Path("/Users({identifier})")
    public void deleteItem(@PathParam("identifier") Integer identifier) {
        manager.removeUser(identifier);
    }
}
