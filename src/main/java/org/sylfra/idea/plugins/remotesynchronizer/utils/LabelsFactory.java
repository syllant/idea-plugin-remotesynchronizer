package org.sylfra.idea.plugins.remotesynchronizer.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Factory for ui labels
 * <p/>
 * Labels are stored in properties files located in <b>RemoteSynchronizerPlugin</b>
 * package
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class LabelsFactory
{
  private static ResourceBundle labelsBundle;

  public static final String BN_ADD_PATH = "BN_ADD_PATH";
  public static final String BN_ADD_TARGET = "BN_ADD_TARGET";
  public static final String BN_EDIT_TARGET_NAME = "BN_EDIT_TARGET_NAME";
  public static final String BN_EDIT_PATH = "BN_EDIT_PATH";
  public static final String BN_MOVE_TARGET_TO_LEFT = "BN_MOVE_TARGET_TO_LEFT";
  public static final String BN_MOVE_TARGET_TO_RIGHT = "BN_MOVE_TARGET_TO_RIGHT";
  public static final String BN_REMOVE_PATH = "BN_REMOVE_PATH";
  public static final String BN_REMOVE_TARGET = "BN_REMOVE_TARGET";
  public static final String BN_EXPORT = "BN_EXPORT";
  public static final String BN_IMPORT = "BN_IMPORT";
  public static final String COL_DEST_PATH = "COL_DEST_PATH";
  public static final String COL_EXCLUDED_COPY_PATH = "COL_EXCLUDED_COPY_PATH";
  public static final String COL_EXCLUDED_DELETE_PATH = "COL_EXCLUDED_DELETE_PATH";
  public static final String COL_SRC_PATH = "COL_SRC_PATH";
  public static final String FRAME_EXCLUDED_COPY_PATH = "FRAME_EXCLUDED_COPY_PATH";
  public static final String FRAME_EXCLUDED_DELETE_PATH = "FRAME_EXCLUDED_DELETE_PATH";
  public static final String FRAME_INCLUDED_PATH = "FRAME_INCLUDED_PATH";
  public static final String LB_ACTIVE = "LB_ACTIVE";
  public static final String LB_ALLOW_CONCURRENT_RUNS = "LB_ALLOW_CONCURRENT_RUNS";
  public static final String LB_AUTO_POPUP_LOGS = "LB_AUTO_POPUP_LOGS";
  public static final String LB_CLEAR_BEFORE_SYNCHRO = "LB_CLEAR_BEFORE_SYNCHRO";
  public static final String LB_CREATE_MISSING_DIRS = "LB_CREATE_MISSING_DIRS";
  public static final String LB_DEST_PATH = "LB_DEST_PATH";
  public static final String LB_DELETE_OBSOLETE = "LB_DELETE_OBSOLETE";
  public static final String LB_EXCLUDED_COPY_PATH = "LB_EXCLUDED_COPY_PATH";
  public static final String LB_EXCLUDED_DELETE_PATH = "LB_EXCLUDED_DELETE_PATH";
  public static final String LB_FONT_FAMILY = "LB_FONT_FAMILY";
  public static final String LB_FONT_SIZE = "LB_FONT_SIZE";
  public static final String LB_NAME = "LB_NAME";
  public static final String LB_NO_ACTIVE_TARGET = "LB_NO_ACTIVE_TARGET";
  public static final String LB_STORE_RELATIVE_PATHS = "LB_STORE_RELATIVE_PATHS";
  public static final String LB_SAVE_BEFORE_COPY = "LB_SAVE_BEFORE_COPY";
  public static final String LB_SHOW_EXCLUDED_PATHS = "LB_SHOW_EXCLUDED_PATHS";
  public static final String LB_SHOW_IDENTICAL_PATHS = "LB_SHOW_IDENTICAL_PATHS";
  public static final String LB_SIMULATION_MODE = "LB_SIMULATION_MODE";
  public static final String LB_SHOW_SOURCE_PATHS = "LB_SHOW_SOURCE_PATHS";
  public static final String LB_SRC_PATH = "LB_SRC_PATH";
  public static final String LB_TITLE_IMPORT_SELECT = "LB_TITLE_IMPORT_SELECT";
  public static final String LB_DESC_IMPORT_SELECT = "LB_DESC_IMPORT_SELECT";
  public static final String MSG_EXPORT_FAILED = "MSG_EXPORT_FAILED";
  public static final String MSG_IMPORT_FAILED = "MSG_IMPORT_FAILED";
  public static final String MSG_CANT_DELETE_DIR = "MSG_CANT_DELETE_DIR";
  public static final String MSG_CANT_DELETE_FILE = "MSG_CANT_DELETE_FILE";
  public static final String MSG_CANT_COPY_FILE = "MSG_CANT_COPY_FILE";
  public static final String MSG_COPY_INTERRUPTED = "MSG_COPY_INTERRUPTED";
  public static final String MSG_COPY_STOPPED = "MSG_COPY_STOPPED";
  public static final String MSG_FROM = "MSG_FROM";
  public static final String MSG_INVALID_DEST_PATH = "MSG_INVALID_DEST_PATH";
  public static final String MSG_INVALID_PATH = "MSG_INVALID_PATH";
  public static final String MSG_INVALID_SRC_PATH = "MSG_INVALID_SRC_PATH";
  public static final String MSG_NB_FAILURE = "MSG_NB_FAILURE";
  public static final String MSG_NB_FAILURES = "MSG_NB_FAILURES";
  public static final String MSG_NB_FILES_COPIED = "MSG_NB_FILES_COPIED";
  public static final String MSG_NB_FILES_IGNORED = "MSG_NB_FILES_IGNORED";
  public static final String MSG_NB_FILE_COPIED = "MSG_NB_FILE_COPIED";
  public static final String MSG_NB_FILE_DELETED = "MSG_NB_FILE_DELETED";
  public static final String MSG_NB_FILES_DELETED = "MSG_NB_FILES_DELETED";
  public static final String MSG_NB_FILE_IGNORED = "MSG_NB_FILE_IGNORED";
  public static final String MSG_NO_FILE_COPIED = "MSG_NO_FILE_COPIED";
  public static final String MSG_PATH_NOT_FOUND = "MSG_PATH_NOT_FOUND";
  public static final String MSG_CANT_MAKE_DIRS = "MSG_CANT_MAKE_DIRS";
  public static final String MSG_PATH_NOT_IN_PROJECT = "MSG_PATH_NOT_IN_PROJECT";
  public static final String MSG_SRC_IN_DEST_PATH = "MSG_SRC_IN_DEST_PATH";
  public static final String MSG_SIMULATION_ACTIVED = "MSG_SIMULATION_ACTIVED";
  public static final String MSG_SIMULATION_DEACTIVED = "MSG_SIMULATION_DEACTIVED";
  public static final String MSG_SYMBOLS = "MSG_SYMBOLS";
  public static final String MSG_SYMBOL_DELETED = "MSG_SYMBOL_DELETED";
  public static final String MSG_SYMBOL_EQUAL = "MSG_SYMBOL_EQUAL";
  public static final String MSG_SYMBOL_EXCLUDED = "MSG_SYMBOL_EXCLUDED";
  public static final String MSG_SYMBOL_NEW = "MSG_SYMBOL_NEW";
  public static final String MSG_SYMBOL_NO_CLASS = "MSG_SYMBOL_NO_CLASS";
  public static final String MSG_SYMBOL_REPLACE = "MSG_SYMBOL_REPLACE";
  public static final String PANEL_EXCLUDED_COPY_PATHS = "PANEL_EXCLUDED_COPY_PATHS";
  public static final String PANEL_EXCLUDED_DELETE_PATHS = "PANEL_EXCLUDED_DELETE_PATHS";
  public static final String PANEL_FONT = "PANEL_FONT";
  public static final String PANEL_GENERAL = "PANEL_GENERAL";
  public static final String PANEL_INCLUDED_PATHS = "PANEL_INCLUDED_PATHS";
  public static final String PANEL_LOGS = "PANEL_LOGS";
  public static final String PANEL_SHOW_PATHS = "PANEL_SHOW_PATHS";
  public static final String PANEL_TARGETS = "PANEL_TARGETS";
  public static final String TITLE_DEFAULT_TARGET_NAME = "TITLE_DEFAULT_TARGET_NAME";
  public static final String TITLE_NEW_TARGET_NAME = "TITLE_NEW_TARGET_NAME";
  public static final String TITLE_CONSOLE_BUSY = "TITLE_CONSOLE_BUSY";
  public static final String TITLE_CONSOLE_INTERRUPTED = "TITLE_CONSOLE_INTERRUPTED";

  static
  {
    labelsBundle = ResourceBundle.getBundle("org.sylfra.idea.plugins.remotesynchronizer.Labels", Locale.getDefault());
  }

  /**
   * Provides label for specified key
   */
  public static String get(String key)
  {
    try
    {
      return labelsBundle.getString(key);
    }
    catch (MissingResourceException e)
    {
      return "?" + key + "?";
    }
  }

  public static String get(String key, Object... replaceStrings)
  {
    try
    {
      return MessageFormat.format(labelsBundle.getString(key),
        replaceStrings);
    }
    catch (MissingResourceException e)
    {
      return "?" + key + "?";
    }
  }
}