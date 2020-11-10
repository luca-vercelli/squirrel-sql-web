package net.sourceforge.squirrel_sql.ws.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.SchemaInfoDto;
import net.sourceforge.squirrel_sql.dto.TableInfoDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.ws.managers.ObjectsTabManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/")
@Stateless
public class ObjectsTabEndpoint {

	@Inject
	SessionsManager sessionsManager;
	@Inject
	ObjectsTabManager manager;

	@GET
	@Path("/Sessions({identifier})/SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier);
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("/Sessions({identifier})/SchemaInfo/TableInfo")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("identifier") String identifier) {

		ISession session = sessionsManager.getSessionById(identifier);
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos();
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst, (long) lst.size());
	}

/*	@GET
	@Path("/Sessions({identifier})/SchemaInfo/TableInfo({catalogName},{schemaName})")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("identifier") String identifier,
			@PathParam("catalogName") String catalogName, @PathParam("schemaName") String schemaName) {

		ISession session = sessionsManager.getSessionById(identifier);
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos(catalogName, schemaName);
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst, (long) lst.size());
	}*/
}
