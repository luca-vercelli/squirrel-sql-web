package net.sourceforge.squirrel_sql.ws;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

@Stateless
public class WebApplication extends Application {

	protected static ApplicationArguments applicationArguments;

	// as Application._sessionManager is private we create another one
	protected SessionManager sessionManager;

	// as Application._pluginManager is private we create another one
	protected IPluginManager pluginManager;

	@Override
	public void startup() {
		throw new java.awt.HeadlessException("This class is not intended to be run in Swing");
	}

	/**
	 * Mimic Main.main() and this.startup()
	 */
	@PostConstruct
	public void postConstruct() {

		if (applicationArguments == null) {
			executeOnce();
		}

		initResourcesAndPrefs();
		initSessionManager();
		initPluginManager();
		if (applicationArguments.getLoadPlugins()) {
			// TODO
		}
		initDriverManager();
		initAppFiles();
		// no way to access _appFiles here, and check them
		initDataCache();
	}

	protected void executeOnce() {
		// This code shall be executed exactly once

		// why not log4j?
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(true));

		// should be in some cfg file
		String[] cliArgs = { "--no-splash" };
		ApplicationArguments.initialize(cliArgs);
		applicationArguments = ApplicationArguments.getInstance();
		applicationArguments.validateArgs(true);

	}

	protected void initPluginManager() {
		pluginManager = new PluginManager(this);
	}

	protected void initSessionManager() {
		this.sessionManager = new SessionManager(this);
	}

	@PreDestroy
	public void preDestroy() {
		// mimic this.shutdown(false) ?
	}

	public ApplicationArguments getApplicationArguments() {
		return applicationArguments;
	}

	public void setApplicationArguments(ApplicationArguments args) {
		applicationArguments = args;
	}

	@Override
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	@Override
	public IPluginManager getPluginManager() {
		return pluginManager;
	}
}
