package net.sourceforge.squirrel_sql.ws.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.dto.ListBean;
import net.sourceforge.squirrel_sql.dto.ObjectTreeNodeDto;
import net.sourceforge.squirrel_sql.dto.SchemaInfoDto;
import net.sourceforge.squirrel_sql.dto.TableInfoDto;
import net.sourceforge.squirrel_sql.dto.ValueBean;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.managers.ObjectsTabManager;
import net.sourceforge.squirrel_sql.ws.managers.SessionsManager;

@Path("/")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ObjectsTabEndpoint {

	@Inject
	ObjectsTabManager manager;
	@Inject
	SessionsManager sessionsManager;

	@GET
	@Path("/Session({sessionId})/SchemaInfo")
	public ValueBean<SchemaInfoDto> getSchemaInfo(@PathParam("sessionId") String sessionId)
			throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// If null, may raise HTTP 404
		return new ValueBean<>(new SchemaInfoDto(schemaInfo));
	}

	@GET
	@Path("/Session({sessionId})/SchemaInfo/TableInfo")
	public ListBean<TableInfoDto> getTableInfo(@PathParam("sessionId") String sessionId) throws AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos();
		List<TableInfoDto> lst = new ArrayList<>();
		for (ITableInfo t : tableInfos) {
			lst.add(new TableInfoDto(t));
		}
		// If null, may raise HTTP 404
		return new ListBean<>(lst);
	}

	@GET
	@Path("/Session({sessionId})/RootNode")
	public ValueBean<ObjectTreeNodeDto> getRootNode(@PathParam("sessionId") String sessionId)
			throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode rootNode = manager.createAndExpandRootNode(session);
		ObjectTreeNodeDto rootNodeDto = manager.node2Dto(rootNode);
		return new ValueBean<>(rootNodeDto);
	}

	@POST
	@Path("/Session({sessionId})/ExpandNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public ListBean<ObjectTreeNodeDto> expandNode(@PathParam("sessionId") String sessionId,
			ObjectTreeNodeDto parentNodeDto) throws SQLException, AuthorizationException {
		ISession session = sessionsManager.getSessionById(sessionId);
		ObjectTreeNode node = manager.dto2Node(parentNodeDto, session);
		List<ObjectTreeNode> list = manager.expandNode(node);
		return new ListBean<>(manager.node2Dto(list));
	}

}
