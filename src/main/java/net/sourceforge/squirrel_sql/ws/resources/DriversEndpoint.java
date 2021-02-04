package net.sourceforge.squirrel_sql.ws.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.SQLDriverDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.ws.managers.DriversManager;

@Path("/")
@Stateless
public class DriversEndpoint {

    @Inject
    DriversManager manager;

    @GET
    @Path("/Drivers")
    public ListBean<SQLDriverDto> getItems() {
        List<SQLDriver> list = manager.getDrivers();
        // If 0, may raise HTTP 404

        List<SQLDriverDto> listDto = list.stream().map(x -> new SQLDriverDto(x)).collect(Collectors.toList());
        return new ListBean<>(listDto);
    }

    @GET
    @Path("/Drivers({identifier})")
    public ValueBean<SQLDriverDto> getItem(@PathParam("identifier") String stringId) {
        SQLDriver item = manager.getDriverById(stringId);
        // If null, may raise HTTP 404
        return new ValueBean<>(new SQLDriverDto(item));
    }

    @POST
    @Path("/Drivers")
    public ValueBean<SQLDriverDto> createItem(SQLDriverDto dto) throws ValidationException {
        SQLDriver item = manager.createNewDriver(dto.getDriver());
        return new ValueBean<>(new SQLDriverDto(item));
    }

    @PUT
    @Path("/Drivers({identifier})")
    public ValueBean<SQLDriverDto> updateItem(@PathParam("identifier") String stringId, SQLDriverDto dto)
            throws ValidationException {
        SQLDriver item = manager.updateDriver(dto.getDriver(), stringId);
        return new ValueBean<>(new SQLDriverDto(item));
    }

    @DELETE
    @Path("/Drivers({identifier})")
    public void deleteItem(@PathParam("identifier") String stringId) {
        manager.removeDriver(stringId);
    }

    @POST
    @Path("/CheckAllDrivers")
    public void checkAllDrivers() {
        manager.searchJDBCDriversInClasspath();
    }
}
