package net.sourceforge.squirrel_sql.client;

/*

This class was decompiled and modified by squirrel-sql-web

*/

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.PopupFactory;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.ActionRegistry;
import net.sourceforge.squirrel_sql.client.edtwatcher.EventDispatchThreadWatcher;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.SquirrelSplashScreen;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.gui.db.GlobalSQLAliasVersioner;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesManager;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToStartupAliasesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginLoadInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.LocaleWrapper;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteHistory;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutManager;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellImportExportInfoSaver;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow.RowsWindowFrameRegistry;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationStorage;
import net.sourceforge.squirrel_sql.fw.props.PropsImpl;
import net.sourceforge.squirrel_sql.fw.resources.LazyResourceBundle;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.BareBonesBrowserLaunch;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

public class Application implements IApplication {
	private static ILogger s_log = LoggerController.createLogger(Application.class);
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(Application.class);
	private SquirrelPreferences _prefs;
	private DesktopStyle _desktopStyle;
	private SQLDriverManager _driverMgr;
	private AliasesAndDriversManager _aliasesAndDriversManager;
	private ActionRegistry _actionRegistry;
	private IPluginManager _pluginManager;
	private final DummyAppPlugin _dummyPlugin = new DummyAppPlugin();
	private SquirrelResources _resources;
	private final TaskThreadPool _threadPool = new TaskThreadPool();
	private SessionManager _sessionManager;
	private WindowManager _windowManager;
	private LoggerController _loggerFactory;
	private ISQLEntryPanelFactory _sqlEntryFactory = new DefaultSQLEntryPanelFactory();
	private PrintStream _jdbcDebugOutputStream;
	private PrintWriter _jdbcDebugOutputWriter;
	private final FontInfoStore _fontInfoStore = new FontInfoStore();
	private SQLHistory _sqlHistory;
	private IWikiTableConfigurationFactory wikiTableConfigFactory = WikiTableConfigurationFactory.getInstance();
	private int _jdbcDebugType = 0;
	private ApplicationFiles _appFiles = null;
	private EditWhereCols editWhereCols = new EditWhereCols();
	private List<ApplicationListener> _listeners = new ArrayList<>();
	private IShutdownTimer _shutdownTimer = new ShutdownTimer();
	private MultipleWindowsHandler _multipleWindowsHandler = new MultipleWindowsHandler(this);
	private RecentFilesManager _recentFilesManager = new RecentFilesManager();
	private PasteHistory _pasteHistory = new PasteHistory();
	private RowsWindowFrameRegistry _rowsWindowFrameRegistry = new RowsWindowFrameRegistry();
	private ShortcutManager _shortcutManager = new ShortcutManager();
	private PropsImpl _propsImpl;
	private GlobalSQLAliasVersioner _globalSQLAliasVersioner = new GlobalSQLAliasVersioner();

	public void startup() {
		initResourcesAndPrefs();

		ApplicationArguments args = ApplicationArguments.getInstance();

		setupLookAndFeel(args);

		this._desktopStyle = new DesktopStyle(this._prefs);

		preferencesHaveChanged(null);
		this._prefs.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				preferencesHaveChanged(evt);
			}
		});

		SquirrelSplashScreen splash = null;
		if (args.getShowSplashScreen()) {
			splash = new SquirrelSplashScreen(this._prefs, 18);
		}
		executeStartupTasks(splash, args);
		if (args.detectLongRunningSwingEDTEvents()) {
			new EventDispatchThreadWatcher();
		}
	}

	public void initResourcesAndPrefs() {
		this._prefs = SquirrelPreferences.load();

		Locale locale = LocaleWrapper.constructPreferredLocale(this._prefs);
		if (null != locale) {
			Locale.setDefault(locale);
		}
		LazyResourceBundle.setLocaleInitialized();

		this._resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
	}

	public boolean shutdown(boolean updateLaunchScript) {
		long begin = System.currentTimeMillis();

		s_log.info("Application.shutdown: BEGIN: " + Calendar.getInstance().getTime());

		_saveApplicationState_beforeWidgetClosing(begin);
		s_log.info("Application.shutdown: saveApplicationState() ELAPSED: " + (System.currentTimeMillis() - begin));
		if (!closeAllSessions()) {
			return false;
		}
		_saveApplicationState_afterWidgetClosing(begin);

		this._pluginManager.unloadPlugins();
		s_log.info("Application.shutdown: _pluginManager.unloadPlugins() ELAPSED: "
				+ (System.currentTimeMillis() - begin));

		closeAllViewers();
		s_log.info("Application.shutdown: closeAllViewers() ELAPSED: " + (System.currentTimeMillis() - begin));

		closeOutputStreams();
		s_log.info("Application.shutdown: closeOutputStreams() ELAPSED: " + (System.currentTimeMillis() - begin));

		SchemaInfoCacheSerializer.waitTillStoringIsDone();
		s_log.info("Application.shutdown: SchemaInfoCacheSerializer.waitTillStoringIsDone() ELAPSED: "
				+ (System.currentTimeMillis() - begin));

		s_log.info("Application.shutdown END: " + Calendar.getInstance().getTime());
		LoggerController.shutdown();

		return true;
	}

	public void saveApplicationState() {
		long begin = System.currentTimeMillis();
		_saveApplicationState_beforeWidgetClosing(begin);
		_saveApplicationState_afterWidgetClosing(begin);
	}

	private void _saveApplicationState_beforeWidgetClosing(long begin) {
		this._prefs.setFirstRun(false);
		s_log.info("saveApplicationState: _prefs.setFirstRun(false) ELAPSED: " + (System.currentTimeMillis() - begin));
		for (ApplicationListener l : (ApplicationListener[]) this._listeners.toArray(new ApplicationListener[0])) {
			l.saveApplicationState();
		}
		s_log.info("saveApplicationState: _listeners ELAPSED: " + (System.currentTimeMillis() - begin));

		saveDrivers();
		s_log.info("saveApplicationState: saveDrivers() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveAliases();
		s_log.info("saveApplicationState: saveAliases() ELAPSED: " + (System.currentTimeMillis() - begin));

		this._recentFilesManager.saveJsonBean(this._appFiles.getRecentFilesJsonBeanFile());
		s_log.info("saveApplicationState: saveRecentFiles() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveSQLHistory();
		s_log.info("saveApplicationState: saveSQLHistory() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveCellImportExportInfo();
		s_log.info("saveApplicationState: saveCellImportExportInfo() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveEditWhereColsInfo();
		s_log.info("saveApplicationState: saveEditWhereColsInfo() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveDataTypePreferences();
		s_log.info("saveApplicationState: saveDataTypePreferences() ELAPSED: " + (System.currentTimeMillis() - begin));

		saveUserSpecificWikiConfigurations();
		s_log.info("saveApplicationState: saveUserSpecificWikiConfigurations() ELAPSED: "
				+ (System.currentTimeMillis() - begin));

		this._prefs.save();
	}

	private void _saveApplicationState_afterWidgetClosing(long begin) {
		this._propsImpl.saveProperties();
		s_log.info(
				"saveApplicationState: _propsImpl.saveProperties() ELAPSED: " + (System.currentTimeMillis() - begin));
	}

	private void closeOutputStreams() {
		if (this._jdbcDebugOutputStream != null) {
			this._jdbcDebugOutputStream.close();
			this._jdbcDebugOutputStream = null;
		}
		if (this._jdbcDebugOutputWriter != null) {
			this._jdbcDebugOutputWriter.close();
			this._jdbcDebugOutputWriter = null;
		}
	}

	private void saveAliases() {
		try {
			File file = this._appFiles.getDatabaseAliasesFile();
			this._aliasesAndDriversManager.saveAliases(file);
		} catch (Throwable th) {
			String thMsg = th.getMessage();
			if (thMsg == null) {
				thMsg = "";
			}
			String msg = s_stringMgr.getString("Application.error.aliassave", new Object[] { th.getMessage() });
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
	}

	private void saveDrivers() {
		try {
			File file = this._appFiles.getDatabaseDriversFile();
			this._aliasesAndDriversManager.saveDrivers(file);
		} catch (Throwable th) {
			System.err.println("Throwable=" + th);
			String msg = s_stringMgr.getString("Application.error.driversave", new Object[] { th.getMessage() });
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
	}

	private void closeAllViewers() {
		try {
			FileViewerFactory.getInstance().closeAllViewers();
		} catch (Throwable t) {
			s_log.error(s_stringMgr.getString("Application.error.closeFileViewers"), t);
		}
	}

	private boolean closeAllSessions() {
		boolean result = false;
		try {
			if (!this._sessionManager.closeAllSessions()) {
				s_log.info(s_stringMgr.getString("Application.shutdownCancelled",
						new Object[] { Calendar.getInstance().getTime() }));
			} else {
				result = true;
			}
		} catch (Throwable t) {
			String msg = s_stringMgr.getString("Application.error.closeAllSessions", new Object[] { t.getMessage() });
			s_log.error(msg, t);
		}
		return result;
	}

	public IPluginManager getPluginManager() {
		return this._pluginManager;
	}

	// squirrel-sql-web
	protected void initPluginManager() {
		this._pluginManager = new PluginManager(this);
	}

	// squirrel-sql-web
	protected void initSessionManager() {
		this._sessionManager = new SessionManager(this);
	}

	public WindowManager getWindowManager() {
		return this._windowManager;
	}

	public ActionCollection getActionCollection() {
		return this._actionRegistry.getActionCollection();
	}

	public ActionRegistry getActionRegistry() {
		return this._actionRegistry;
	}

	public SQLDriverManager getSQLDriverManager() {
		return this._driverMgr;
	}

	public AliasesAndDriversManager getAliasesAndDriversManager() {
		return this._aliasesAndDriversManager;
	}

	public IPlugin getDummyAppPlugin() {
		return this._dummyPlugin;
	}

	public SquirrelResources getResources() {
		return this._resources;
	}

	public IMessageHandler getMessageHandler() {
		return getMainFrame().getMessagePanel();
	}

	public SquirrelPreferences getSquirrelPreferences() {
		return this._prefs;
	}

	public DesktopStyle getDesktopStyle() {
		return this._desktopStyle;
	}

	public ShortcutManager getShortcutManager() {
		return this._shortcutManager;
	}

	public MainFrame getMainFrame() {
		return this._windowManager.getMainFrame();
	}

	public SessionManager getSessionManager() {
		return this._sessionManager;
	}

	// squirrel-sql-web: created setter
	protected void setSessionManager(SessionManager sessionManager) {
		this._sessionManager = sessionManager;
	}

	public void showErrorDialog(String msg) {
		s_log.error(msg);
		new ErrorDialog(getMainFrame(), msg).setVisible(true);
	}

	public void showErrorDialog(Throwable th) {
		s_log.error(th);
		new ErrorDialog(getMainFrame(), th).setVisible(true);
	}

	public void showErrorDialog(String msg, Throwable th) {
		s_log.error(msg, th);
		new ErrorDialog(getMainFrame(), msg, th).setVisible(true);
	}

	public FontInfoStore getFontInfoStore() {
		return this._fontInfoStore;
	}

	public TaskThreadPool getThreadPool() {
		return this._threadPool;
	}

	public LoggerController getLoggerFactory() {
		return this._loggerFactory;
	}

	public ISQLEntryPanelFactory getSQLEntryPanelFactory() {
		return this._sqlEntryFactory;
	}

	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory) {
		this._sqlEntryFactory = (factory != null ? factory : new DefaultSQLEntryPanelFactory());
	}

	public SQLHistory getSQLHistory() {
		return this._sqlHistory;
	}

	public synchronized void addToMenu(int menuId, JMenu menu) {
		MainFrame mf = getMainFrame();
		if (mf != null) {
			mf.addToMenu(menuId, menu);
		} else {
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	public synchronized void addToMenu(int menuId, Action action) {
		MainFrame mf = getMainFrame();
		if (mf != null) {
			mf.addToMenu(menuId, action);
		} else {
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	public void addToStatusBar(JComponent comp) {
		MainFrame mf = getMainFrame();
		if (mf != null) {
			mf.addToStatusBar(comp);
		} else {
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compadding"));
		}
	}

	public void removeFromStatusBar(JComponent comp) {
		MainFrame mf = getMainFrame();
		if (mf != null) {
			mf.removeFromStatusBar(comp);
		} else {
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compremoving"));
		}
	}

	public void openURL(String url) {
		BareBonesBrowserLaunch.openURL(url);
	}

	private void executeStartupTasks(SquirrelSplashScreen splash, ApplicationArguments args) {
		if (args == null) {
			throw new IllegalArgumentException("ApplicationArguments == null");
		}
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createSessionManager"));

		this._sessionManager = new SessionManager(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingprefs"));

		boolean loadPlugins = args.getLoadPlugins();
		if (loadPlugins) {
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingplugins"));
		} else {
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.notloadingplugins"));
		}
		UIFactory.initialize(this._prefs, this);
		this._pluginManager = new PluginManager(this);
		if (args.getLoadPlugins()) {
			if ((null != splash) && (this._prefs.getShowPluginFilesInSplashScreen())) {
				ClassLoaderListener listener = splash.getClassLoaderListener();
				this._pluginManager.setClassLoaderListener(listener);
			}
			if (args.getPluginList() != null) {
				this._pluginManager.loadPluginsFromList(args.getPluginList());
			} else {
				this._pluginManager.loadPlugins();
			}
		}
		args.validateArgs(true);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingactions"));
		this._actionRegistry = new ActionRegistry();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadinguseracc"));
		this._actionRegistry.loadActionKeys(this._prefs.getActionKeys());

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createjdbcmgr"));
		initDriverManager();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingjdbc"));
		initAppFiles();

		String errMsg = FileTransformer.transform(this._appFiles);
		if (null != errMsg) {
			System.err.println(errMsg);
			JOptionPane.showMessageDialog(null, errMsg, "SQuirreL failed to start", 0);
			System.exit(-1);
		}
		initDataCache();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createWindowManager"));
		this._windowManager = new WindowManager(this, args.getUserInterfaceDebugEnabled());

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.uifactoryinit"));

		String initializingPlugins = s_stringMgr.getString("Application.splash.initializingplugins");
		String notloadingplugins = s_stringMgr.getString("Application.splash.notloadingplugins");
		String task = loadPlugins ? initializingPlugins : notloadingplugins;
		indicateNewStartupTask(splash, task);
		Iterator<PluginLoadInfo> it;
		if (loadPlugins) {
			this._pluginManager.initializePlugins();
			for (it = this._pluginManager.getPluginLoadInfoIterator(); it.hasNext();) {
				PluginLoadInfo pli = (PluginLoadInfo) it.next();
				long created = pli.getCreationTime();
				long load = pli.getLoadTime();
				long init = pli.getInitializeTime();
				Object[] params = { pli.getInternalName(), Long.valueOf(created), Long.valueOf(load),
						Long.valueOf(init), Long.valueOf(created + load + init) };
				String pluginLoadMsg = s_stringMgr.getString("Application.splash.loadplugintime", params);
				s_log.info(pluginLoadMsg);
			}
		}
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.recentfiles"));
		loadRecentFileHistory();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadsqlhistory"));
		loadSQLHistory();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadcellselections"));
		loadCellImportExportInfo();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadeditselections"));
		loadEditWhereColsInfo();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loaddatatypeprops"));
		loadDTProperties();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadUserSpecificWikiConfiguration"));
		loadUserSpecificWikiTableConfigurations();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.showmainwindow"));
		this._windowManager.moveToFront(this._windowManager.getMainFrame());
		this._threadPool.setParentForMessages(this._windowManager.getMainFrame());

		new ConnectToStartupAliasesCommand(this).execute();
		if (this._prefs.isFirstRun()) {
			try {
				new ViewHelpCommand(this).execute();
			} catch (BaseException ex) {
				s_log.error(s_stringMgr.getString("Application.error.showhelpwindow"), ex);
			}
		}
		this._actionRegistry.registerMissingActionsToShortcutManager();
		if (args.getShutdownTimerSeconds() != null) {
			this._shutdownTimer.setShutdownSeconds(args.getShutdownTimerSeconds().intValue());
			this._shutdownTimer.setApplication(this);
			this._shutdownTimer.start();
		}
	}

	public void initDriverManager() {
		this._driverMgr = new SQLDriverManager();
	}

	public void initAppFiles() {
		this._appFiles = new ApplicationFiles();
	}

	public void initDataCache() {
		this._aliasesAndDriversManager = new AliasesAndDriversManager(this._driverMgr,
				this._appFiles.getDatabaseDriversFile(), this._appFiles.getDatabaseAliasesFile(),
				this._resources.getDefaultDriversUrl());
	}

	private void indicateNewStartupTask(SquirrelSplashScreen splash, String taskDescription) {
		if (splash != null) {
			splash.indicateNewTask(taskDescription);
		}
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt) {
		String propName = evt != null ? evt.getPropertyName() : null;
		if ((propName == null) || (propName.equals("showToolTips"))) {
			ToolTipManager.sharedInstance().setEnabled(this._prefs.getShowToolTips());
		}
		if ((propName == null) || (propName.equals("jdbcDebugtype"))) {
			setupJDBCLogging();
		}
		if ((propName == null) || (propName.equals("loginTimeout"))) {
			DriverManager.setLoginTimeout(this._prefs.getLoginTimeout());
		}
		if ((propName == null) || (propName == "proxyPerferences")) {
			new ProxyHandler().apply(this._prefs.getProxySettings());
		}
	}

	// squirrel-sql-web: made protected
	protected void loadSQLHistory() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getUserSQLHistoryFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext()) {
				this._sqlHistory = ((SQLHistory) it.next());
			}
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.loadsqlhistory"), ex);
		} finally {
			if (this._sqlHistory == null) {
				this._sqlHistory = new SQLHistory();
			}
		}
	}

	// squirrel-sql-web: made protected
	protected void loadRecentFileHistory() {
		getRecentFilesManager().initJSonBean(new ApplicationFiles().getRecentFilesJsonBeanFile());
	}

	// squirrel-sql-web: made protected
	protected void loadUserSpecificWikiTableConfigurations() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getUserSpecificWikiConfigurationsFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext()) {
				WikiTableConfigurationStorage data = (WikiTableConfigurationStorage) it.next();
				this.wikiTableConfigFactory.replaceUserSpecificConfigurations(data.configurationsAsList());
			}
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.loadUserSpecificWikiConfiguration"), ex);
		} finally {
			if (this._sqlHistory == null) {
				this._sqlHistory = new SQLHistory();
			}
		}
	}

	private void saveSQLHistory() {
		try {
			if (this._prefs.getSessionProperties().getLimitSQLEntryHistorySize()) {
				SQLHistoryItem[] data = this._sqlHistory.getData();

				int maxSize = this._prefs.getSessionProperties().getSQLEntryHistorySize();
				if (data.length > maxSize) {
					SQLHistoryItem[] reducedData = new SQLHistoryItem[maxSize];
					System.arraycopy(data, 0, reducedData, 0, maxSize);
					this._sqlHistory.setData(reducedData);
				}
			}
			XMLBeanWriter wtr = new XMLBeanWriter(this._sqlHistory);
			wtr.save(new ApplicationFiles().getUserSQLHistoryFile());
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.savesqlhistory"), ex);
		}
	}

	private void saveUserSpecificWikiConfigurations() {
		try {
			WikiTableConfigurationStorage data = new WikiTableConfigurationStorage(
					this.wikiTableConfigFactory.getUserSpecificConfigurations());

			XMLBeanWriter wtr = new XMLBeanWriter(data);
			wtr.save(new ApplicationFiles().getUserSpecificWikiConfigurationsFile());
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.saveUserSpecificWikiConfiguration"), ex);
		}
	}

	// squirrel-sql-web: made protected
	protected void loadCellImportExportInfo() {
		CellImportExportInfoSaver saverInstance = null;
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getCellImportExportSelectionsFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext()) {
				saverInstance = (CellImportExportInfoSaver) it.next();
			}
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.loadcellselections"), ex);
		} finally {
			CellImportExportInfoSaver.setInstance(saverInstance);
		}
	}

	private void saveCellImportExportInfo() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(CellImportExportInfoSaver.getInstance());
			wtr.save(new ApplicationFiles().getCellImportExportSelectionsFile());
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.writecellselections"), ex);
		}
	}

	// squirrel-sql-web: made protected
	protected void loadEditWhereColsInfo() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getEditWhereColsFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext()) {
				this.editWhereCols = ((EditWhereCols) it.next());
			}
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException =

					localFileNotFoundException;
		} catch (Exception ex) {
			ex =

					ex;
			s_log.error(s_stringMgr.getString("Application.error.loadcolsinfo"), ex);
		} finally {
		}
	}

	private void saveEditWhereColsInfo() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(this.editWhereCols);
			wtr.save(new ApplicationFiles().getEditWhereColsFile());
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.savecolsinfo"), ex);
		}
	}

	// squirrel-sql-web: made protected
	protected void loadDTProperties() {
		DTProperties saverInstance = null;
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getDTPropertiesFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext()) {
				saverInstance = (DTProperties) it.next();
				DTProperties localDTProperties1 = saverInstance;
			}
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException =

					localFileNotFoundException;
		} catch (Exception ex) {
			ex =

					ex;
			s_log.error(s_stringMgr.getString("Application.error.loaddatatypeprops"), ex);
		} finally {
		}
	}

	private void saveDataTypePreferences() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(new DTProperties());
			wtr.save(new ApplicationFiles().getDTPropertiesFile());
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.savedatatypeprops"), ex);
		}
	}

	public void savePreferences(PreferenceType preferenceType) {
		if (!this._prefs.getSavePreferencesImmediately()) {
			return;
		}
		switch (preferenceType.ordinal()) {
		case 0:
			saveAliases();
			break;
		case 1:
			saveDrivers();
			break;
		case 2:
			saveDataTypePreferences();
			break;
		case 3:
			saveCellImportExportInfo();
			break;
		case 4:
			saveSQLHistory();
			break;
		case 5:
			saveEditWhereColsInfo();
			break;
		case 6:
			saveUserSpecificWikiConfigurations();
			break;
		default:
			s_log.error("Unknown preference type: " + preferenceType);
		}
	}

	public void addApplicationListener(ApplicationListener l) {
		this._listeners.add(l);
	}

	public void removeApplicationListener(ApplicationListener l) {
		this._listeners.remove(l);
	}

	protected void setupLookAndFeel(IApplicationArguments args) {
		String userSpecifiedOverride = System.getProperty("swing.defaultlaf");
		if ((userSpecifiedOverride != null) && (!"".equals(userSpecifiedOverride))) {
			return;
		}
		String lafClassName = args.useNativeLAF() ? UIManager.getSystemLookAndFeelClassName()
				: MetalLookAndFeel.class.getName();
		if (!args.useDefaultMetalTheme()) {
			MetalLookAndFeel.setCurrentTheme(new AllBluesBoldMetalTheme());
		}
		try {
			PopupFactory.setSharedInstance(new PopupFactory());

			UIManager.setLookAndFeel(lafClassName);
		} catch (Exception ex) {
			s_log.error(s_stringMgr.getString("Application.error.setlaf"), ex);
		}
	}

	private void setupJDBCLogging() {
		if (this._jdbcDebugType != this._prefs.getJdbcDebugType()) {
			ApplicationFiles appFiles = new ApplicationFiles();
			File outFile = appFiles.getJDBCDebugLogFile();

			DriverManager.setLogStream(null);
			if (this._jdbcDebugOutputStream != null) {
				this._jdbcDebugOutputStream.close();
				this._jdbcDebugOutputStream = null;
			}
			DriverManager.setLogWriter(null);
			if (this._jdbcDebugOutputWriter != null) {
				this._jdbcDebugOutputWriter.close();
				this._jdbcDebugOutputWriter = null;
			}
			if (this._prefs.isJdbcDebugToStream()) {
				try {
					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglog"));
					this._jdbcDebugOutputStream = new PrintStream(new FileOutputStream(outFile));
					DriverManager.setLogStream(this._jdbcDebugOutputStream);

					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglogsuccess"));
				} catch (IOException ex) {
					String msg = s_stringMgr.getString("Application.error.jdbcstream");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogStream(System.out);
				}
			}
			if (this._prefs.isJdbcDebugToWriter()) {
				try {
					s_log.debug(s_stringMgr.getString("Application.info.jdbcwriter"));
					this._jdbcDebugOutputWriter = new PrintWriter(new FileWriter(outFile));
					DriverManager.setLogWriter(this._jdbcDebugOutputWriter);

					s_log.debug(s_stringMgr.getString("Application.info.jdbcwritersuccess"));
				} catch (IOException ex) {
					String msg = s_stringMgr.getString("Application.error.jdbcwriter");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogWriter(new PrintWriter(System.out));
				}
			}
			this._jdbcDebugType = this._prefs.getJdbcDebugType();
		}
	}

	public IWikiTableConfigurationFactory getWikiTableConfigFactory() {
		return this.wikiTableConfigFactory;
	}

	public MultipleWindowsHandler getMultipleWindowsHandler() {
		return this._multipleWindowsHandler;
	}

	public RecentFilesManager getRecentFilesManager() {
		return this._recentFilesManager;
	}

	public PasteHistory getPasteHistory() {
		return this._pasteHistory;
	}

	public RowsWindowFrameRegistry getRowsWindowFrameRegistry() {
		return this._rowsWindowFrameRegistry;
	}

	public PropsImpl getPropsImpl() {
		if (null == this._propsImpl) {
			this._propsImpl = new PropsImpl();
		}
		return this._propsImpl;
	}

	public GlobalSQLAliasVersioner getGlobalSQLAliasVersioner() {
		return this._globalSQLAliasVersioner;
	}

	// squirrel-sql-web
	public ApplicationFiles getAppFiles() {
		return this._appFiles;
	}

}
