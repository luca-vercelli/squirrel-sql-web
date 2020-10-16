package net.sourceforge.squirrel_sql.ws;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.SwingApplication;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

@Stateless
public class WebApp {

	private ApplicationArguments swingArguments;

	private SwingApplication swingApp;

	@PostConstruct
	public void init() {
		
		// should be in some cfg file
		String[] cliArgs = {};
		
		ApplicationArguments.initialize(cliArgs);
		swingArguments = ApplicationArguments.getInstance();
		
		// e usare log4j?
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(true));

		swingApp = new SwingApplication();
	}

	public ApplicationArguments getSwingArguments() {
		return swingArguments;
	}

	public void setSwingArguments(ApplicationArguments swingArguments) {
		this.swingArguments = swingArguments;
	}

	public SwingApplication getSwingApp() {
		return swingApp;
	}

	public void setSwingApp(SwingApplication swingApp) {
		this.swingApp = swingApp;
	}
}
