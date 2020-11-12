package net.sourceforge.squirrel_sql.ws.managers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

@Singleton
public class WebApplication extends Application {

	protected static ApplicationArguments applicationArguments;

	Logger logger = Logger.getLogger(WebApplication.class);

	@Override
	public void startup() {
		throw new java.awt.HeadlessException("This class is not intended to be run in Swing");
	}

	/**
	 * Mimic Main.main() and this.startup()
	 */
	@PostConstruct
	public void postConstruct() {

		// why not log4j?
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(true));

		logger.info("******************APPLICATION STARTUP*****************");

		// should be in some cfg file
		String[] cliArgs = { "--no-splash" };
		ApplicationArguments.initialize(cliArgs);

		// by default, files are saved in ~/.squirrel-sql

		applicationArguments = ApplicationArguments.getInstance();
		applicationArguments.validateArgs(true);

		initResourcesAndPrefs();
		initSessionManager();
		initPluginManager();
		if (applicationArguments.getLoadPlugins()) {
			// TODO
		}
		initDriverManager();
		initAppFiles();

		getSquirrelPreferences().setSavePreferencesImmediately(true);

		Main.setApplication(this); // some classes look at this
		getPropsImpl(); // this is not only a getter

		initDataCache();
		loadRecentFileHistory();
		loadSQLHistory();
		loadCellImportExportInfo();
		loadEditWhereColsInfo();
		loadDTProperties();
		loadUserSpecificWikiTableConfigurations();

		saveApplicationState(); // force file creation ?
	}

	@PreDestroy
	public void preDestroy() {
		// mimic this.shutdown()

		saveApplicationState();
		SchemaInfoCacheSerializer.waitTillStoringIsDone();
		// cannot call closeOutputStreams()

		logger.info("******************APPLICATION SHUTDOWN*****************");
		LoggerController.shutdown();
	}

	public ApplicationArguments getApplicationArguments() {
		return applicationArguments;
	}

	public void setApplicationArguments(ApplicationArguments args) {
		applicationArguments = args;
	}

	@Override
	public void showErrorDialog(String msg, Throwable th) {
		logger.error(msg, th);
		// do *not* try to open dialog boxes
	}
}
