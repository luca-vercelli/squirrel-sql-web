package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manages retrieval of database metadata
 * 
 * @author lv 2020
 *
 */
@Stateless
public class ObjectsTabManager {

	@Inject
	WebApplication webapp;
	@Inject
	SessionsManager sessionsManager;

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

}
