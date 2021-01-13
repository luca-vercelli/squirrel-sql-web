package net.sourceforge.squirrel_sql.ws.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;

/**
 * Manages preferences on XML database
 * 
 * @author lv 2020
 *
 */
@Stateless
public class PreferencesManager {

    @Inject
    WebApplication webapp;
    @Inject
    DriversManager driversManager;

    public SquirrelPreferences get() {
        return webapp.getSquirrelPreferences();
    }

    public SquirrelPreferences update(SquirrelPreferences newPrefs) {

        SquirrelPreferences prefs = webapp.getSquirrelPreferences();

        // see GeneralPreferencesGUI.applyChanges()

        prefs.setTabbedStyle(newPrefs.getTabbedStyle());
        prefs.setUseScrollableTabbedPanesForSessionTabs(newPrefs.getUseScrollableTabbedPanesForSessionTabs());
        prefs.setShowContentsWhenDragging(newPrefs.getShowContentsWhenDragging());
        prefs.setShowTabbedStyleHint(newPrefs.getShowTabbedStyleHint());
        prefs.setShowToolTips(newPrefs.getShowToolTips());
        prefs.setUseScrollableTabbedPanes(newPrefs.getUseScrollableTabbedPanes());
        prefs.setShowMainStatusBar(newPrefs.getShowMainStatusBar());
        prefs.setShowMainToolBar(newPrefs.getShowMainToolBar());
        prefs.setShowAliasesToolBar(newPrefs.getShowAliasesToolBar());
        prefs.setShowDriversToolBar(newPrefs.getShowDriversToolBar());
        prefs.setMaximizeSessionSheetOnOpen(newPrefs.getMaximizeSessionSheetOnOpen());
        prefs.setShowColoriconsInToolbar(newPrefs.getShowColoriconsInToolbar());
        prefs.setShowPluginFilesInSplashScreen(newPrefs.getShowPluginFilesInSplashScreen());
        prefs.setUseShortSessionTitle(newPrefs.getUseShortSessionTitle());
        prefs.setRememberValueOfPopup(newPrefs.isRememberValueOfPopup());
        prefs.setConfirmSessionClose(newPrefs.getConfirmSessionClose());
        prefs.setWarnJreJdbcMismatch(newPrefs.getWarnJreJdbcMismatch());
        prefs.setWarnForUnsavedFileEdits(newPrefs.getWarnForUnsavedFileEdits());
        prefs.setWarnForUnsavedBufferEdits(newPrefs.getWarnForUnsavedBufferEdits());
        prefs.setShowSessionStartupTimeHint(newPrefs.getShowSessionStartupTimeHint());
        prefs.setSavePreferencesImmediately(newPrefs.getSavePreferencesImmediately());
        prefs.setSelectOnRightMouseClick(newPrefs.getSelectOnRightMouseClick());
        prefs.setShowPleaseWaitDialog(newPrefs.getShowPleaseWaitDialog());
        prefs.setPreferredLocale(newPrefs.getPreferredLocale());
        prefs.setMaxColumnAdjustLengthDefined(newPrefs.getMaxColumnAdjustLengthDefined());
        prefs.setMaxColumnAdjustLength(newPrefs.getMaxColumnAdjustLength());

        // see SQLPreferencesController.applyChanges()

        prefs.setLoginTimeout(newPrefs.getLoginTimeout());
        prefs.setLargeScriptStmtCount(newPrefs.getLargeScriptStmtCount());
        prefs.setCopyQuotedSqlsToClip(newPrefs.getCopyQuotedSqlsToClip());
        prefs.setAllowRunAllSQLsInEditor(newPrefs.getAllowRunAllSQLsInEditor());

        prefs.setMarkCurrentSql(newPrefs.isMarkCurrentSql());
        prefs.setCurrentSqlMarkColorRGB(newPrefs.getCurrentSqlMarkColorRGB());

        prefs.setReloadSqlContents(newPrefs.isReloadSqlContents());

        if (newPrefs.getMaxTextOutputColumnWidth() >= IDataSetViewer.MIN_COLUMN_WIDTH) {
            prefs.setMaxTextOutputColumnWidth(newPrefs.getMaxTextOutputColumnWidth());
        }

        prefs.setJdbcDebugType(newPrefs.getJdbcDebugType());

        prefs.setFileOpenInPreviousDir(newPrefs.isFileOpenInPreviousDir());
        prefs.setFileOpenInSpecifiedDir(newPrefs.isFileOpenInSpecifiedDir());
        String specDir = newPrefs.getFileSpecifiedDir();
        prefs.setFileSpecifiedDir(null == specDir ? "" : specDir);

        webapp.savePreferences(PreferenceType.DATATYPE_PREFERENCES);
        return prefs;
    }

}
