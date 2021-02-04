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

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.SQLAliasDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.ws.managers.AliasesManager;

@Path("/")
@Stateless
public class AliasesEndpoint {

    @Inject
    AliasesManager manager;

    @GET
    @Path("/Aliases")
    public ListBean<SQLAliasDto> getItems() {
        List<SQLAlias> list = manager.getAliases();
        // If 0, may raise HTTP 404

        List<SQLAliasDto> listDto = list.stream().map(x -> new SQLAliasDto(x)).collect(Collectors.toList());
        return new ListBean<>(listDto);
    }

    @GET
    @Path("/Aliases({identifier})")
    public ValueBean<SQLAliasDto> getItem(@PathParam("identifier") String stringId) {
        SQLAlias item = manager.getAliasById(stringId);
        // If null, may raise HTTP 404

        SQLAliasDto dto = new SQLAliasDto(item);
        return new ValueBean<>(dto);
    }

    @POST
    @Path("/Aliases")
    public ValueBean<SQLAliasDto> createItem(SQLAliasDto dto) throws ValidationException {
        SQLAlias item = manager.createNewAlias(dto.getAlias());
        dto = new SQLAliasDto(item);
        return new ValueBean<>(dto);
    }

    @PUT
    @Path("/Aliases({identifier})")
    public ValueBean<SQLAliasDto> updateItem(@PathParam("identifier") String stringId, SQLAliasDto dto)
            throws ValidationException {
        SQLAlias item = manager.updateAlias(dto.getAlias(), stringId);
        dto = new SQLAliasDto(item);
        return new ValueBean<>(dto);
    }

    @DELETE
    @Path("/Aliases({identifier})")
    public void deleteItem(@PathParam("identifier") String stringId) {
        manager.removeAlias(stringId);
    }

}
