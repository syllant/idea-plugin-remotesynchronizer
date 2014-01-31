package org.sylfra.idea.plugins.remotesynchronizer.utils;

import org.jdom.Element;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes configuration object
 * For 1.5 compatibility
 * @deprecated should be removed with next major release. Only kept to be able to read
 *             old settings
 */
public class _OldConfigExternalizer
{
  private static final String TAG_TARGETS = "targets";
  private static final String TAG_TARGET = "target";
  private static final String TAG_SYNCHRO_MAPPINGS = "synchro-mappings";
  private static final String TAG_EXCLUDED_COPY_PATHS = "excluded-copy-paths";
  private static final String TAG_EXCLUDED_DELETE_PATHS = "excluded-delete-paths";
  private static final String TAG_PATH = "path";
  private static final String TAG_LOG_OPTIONS = "log-options";
  private static final String TAG_GENERAL_OPTIONS = "general-options";
  private static final String ATT_SIMULATION_MODE = "simulation-mode";
  private static final String ATT_SAVE_BEFORE_COPY = "save-before-copy";
  private static final String ATT_CREATE_MISSING_DIRS = "create-missing-dirs";
  private static final String ATT_ALLOW_CONCURRENT_RUNS = "allow_concurrent_runs";
  private static final String ATT_LOG_AUTO_POPUP = "autoPopup";
  private static final String ATT_LOG_CLEAR_BEFORE_SYNCHRO = "clearBeforeSynchro";
  private static final String ATT_LOG_EXCLUDED_PATHS = "showExcludedPaths";
  private static final String ATT_LOG_IDENTICAL_PATHS = "showIdenticalPaths";
  private static final String ATT_LOG_SRC_PATHS = "showSrcPaths";
  private static final String ATT_LOG_FONT_FAMILY = "fontFamily";
  private static final String ATT_LOG_FONT_SIZE = "fontSize";
  private static final String ATT_DELETE_OBSOLETE_FILES = "delete-obsolete-files";
  private static final String ATT_NAME = "name";
  private static final String ATT_VALUE = "value";
  private static final String ATT_ACTIVE = "active";
  private static final String ATT_DEST = "dest";
  private static final String ATT_SRC = "src";

  // For compatibility with RemoteCopier
  private static final String TAG_OLD_INCLUDED_PATHS = "included-paths";
  private static final String TAG_OLD_EXCLUDED_PATHS = "excluded-paths";
  private static final String TAG_OLD_SAVE_BEFORE_COPY = "save-before-copy";
  private static final String TAG_OLD_CREATE_MISSING_DIRS = "create-missing-dirs";

  public static void readExternal(Config config, Element element)
  {
    List<TargetMappings> targetMappings = new ArrayList<TargetMappings>();
    config.setTargetMappings(targetMappings);
    Element parent = element.getChild(TAG_TARGETS);

    if (parent != null)
    {
      List children = parent.getChildren(TAG_TARGET);
      for (Object aChildren : children)
      {
        Element e = (Element) aChildren;
        TargetMappings target = new TargetMappings();
        target.setName(e.getAttributeValue(ATT_NAME));
        target.setActive("true".equals(e.getAttributeValue(ATT_ACTIVE)));
        readTarget(target, e);
        targetMappings.add(target);
      }
    }

    // Compatibility 1.0.* (no targets level)
    if (targetMappings.isEmpty())
    {
      TargetMappings defaultTarget = config.addDefaultTarget();
      readTarget(defaultTarget, element);
    }

    parent = element.getChild(TAG_GENERAL_OPTIONS);
    if (parent != null)
    {
      Config.GeneralOptions generalOptions = config.getGeneralOptions();
      generalOptions
        .setSaveBeforeCopy("true".equals(parent.getAttributeValue(ATT_SAVE_BEFORE_COPY)));
      generalOptions
        .setCreateMissingDirs("true".equals(parent.getAttributeValue(ATT_CREATE_MISSING_DIRS)));
      generalOptions
        .setSimulationMode("true".equals(parent.getAttributeValue(ATT_SIMULATION_MODE)));
      generalOptions
        .setAllowConcurrentRuns("true".equals(parent.getAttributeValue(ATT_ALLOW_CONCURRENT_RUNS)));
    }
    else
    {
      // Compatibility RemoteCopier
      parent = element.getChild(TAG_OLD_SAVE_BEFORE_COPY);
      if (parent != null)
      {
        config.getGeneralOptions()
          .setSaveBeforeCopy("true".equals(parent.getAttributeValue(ATT_VALUE)));
      }

      parent = element.getChild(TAG_OLD_CREATE_MISSING_DIRS);
      if (parent != null)
      {
        config.getGeneralOptions()
          .setCreateMissingDirs("true".equals(parent.getAttributeValue(ATT_VALUE)));
      }
    }

    // Log options
    parent = element.getChild(TAG_LOG_OPTIONS);
    if (parent != null)
    {
      Config.LogOptions logOptions = config.getLogOptions();
      logOptions
        .setLogExludedPaths("true".equals(parent.getAttributeValue(ATT_LOG_EXCLUDED_PATHS)));
      logOptions
        .setLogIdenticalPaths("true".equals(parent.getAttributeValue(ATT_LOG_IDENTICAL_PATHS)));
      logOptions.setLogSrcPaths("true".equals(parent.getAttributeValue(ATT_LOG_SRC_PATHS)));
      logOptions.setAutoPopup("true".equals(parent.getAttributeValue(ATT_LOG_AUTO_POPUP)));
      logOptions.setClearBeforeSynchro(
        "true".equals(parent.getAttributeValue(ATT_LOG_CLEAR_BEFORE_SYNCHRO)));

      String fontFamily = parent.getAttributeValue(ATT_LOG_FONT_FAMILY);
      if (fontFamily != null)
      {
        logOptions.setLogFontFamily(fontFamily);
      }

      int fontSize = checkFontSize(parent.getAttributeValue(ATT_LOG_FONT_SIZE));
      if (fontSize > -1)
      {
        logOptions.setLogFontSize(fontSize);
      }
    }
  }

  public static void readTarget(TargetMappings target, Element element)
  {
    // Synchro mappings
    List<SynchroMapping> synchroMappings = new ArrayList<SynchroMapping>();
    Element parent = element.getChild(TAG_SYNCHRO_MAPPINGS);
    if (parent == null)
    {
      parent = element.getChild(TAG_OLD_INCLUDED_PATHS);
    }

    if (parent != null)
    {
      List children = parent.getChildren(TAG_PATH);
      for (Object aChildren : children)
      {
        Element e = (Element) aChildren;
        synchroMappings.add(new SynchroMapping(e.getAttributeValue(ATT_DEST),
          e.getAttributeValue(ATT_SRC),
          "true".equals(e.getAttributeValue(ATT_DELETE_OBSOLETE_FILES))));
      }
    }
    target.setSynchroMappings(synchroMappings.toArray(new SynchroMapping[synchroMappings.size()]));

    // Excluded copy paths
    List<String> paths = new ArrayList<String>();
    parent = element.getChild(TAG_EXCLUDED_COPY_PATHS);
    if (parent == null)
    {
      parent = element.getChild(TAG_OLD_EXCLUDED_PATHS);
    }

    if (parent != null)
    {
      List children = parent.getChildren(TAG_PATH);
      for (Object aChildren : children)
      {
        Element e = (Element) aChildren;
        paths.add(e.getAttributeValue(ATT_SRC));
      }
    }
    target.setExcludedCopyPaths(paths.toArray(new String[paths.size()]));

    // Excluded delete paths
    paths.clear();
    parent = element.getChild(TAG_EXCLUDED_DELETE_PATHS);
    if (parent != null)
    {
      List children = parent.getChildren(TAG_PATH);
      for (Object aChildren : children)
      {
        Element e = (Element) aChildren;
        paths.add(e.getAttributeValue(ATT_SRC));
      }
    }
    target.setExcludedDeletePaths(paths.toArray(new String[paths.size()]));
  }

  private static int checkFontSize(String s)
  {
    if (s == null)
    {
      return -1;
    }

    try
    {
      return Integer.parseInt(s);
    }
    catch (NumberFormatException e)
    {
      return -1;
    }
  }
}
