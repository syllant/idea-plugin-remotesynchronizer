package org.sylfra.idea.plugins.remotesynchronizer.ui;

import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.ConfigListener;
import org.sylfra.idea.plugins.remotesynchronizer.model.SyncronizingStatsInfo;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThreadListener;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logs component. A thread console is linked to one thread synchronizer
 */
public class ThreadConsole extends JScrollPane
  implements ConfigListener, SynchronizerThreadListener
{
  // Types of symbols
  private static final String INFO_COPY_REPLACE = "(/)";
  private static final String INFO_COPY_NEW = "(+)";
  private static final String INFO_COPY_DELETED = "(-)";
  private static final String INFO_COPY_EQUAL = "(=)";
  private static final String INFO_COPY_NO_CLASS = "(?)";
  private static final String INFO_COPY_EXCLUDED = "(^)";

  private static final String[] COPY_INFOS =
    {
      INFO_COPY_NEW, INFO_COPY_REPLACE, INFO_COPY_EQUAL, INFO_COPY_NO_CLASS,
      INFO_COPY_EXCLUDED
    };

  private final static DateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final static String HEADER_STYLE = "HeaderStyle";
  private final static String DEFAULT_STYLE = "DefaultStyle";
  private final static String SIMULATION_STYLE = "SimulationStyle";
  private final static String ERROR_STYLE = "ErrorStyle";

  private ThreadConsolePane consolePane;
  private SynchronizerThread thread;
  private String title;
  private ThreadConsole mainConsole;
  private JTextPane textPane;
  private Config config;
  private boolean cleared;
  private boolean simulationMode;

  public ThreadConsole(ThreadConsolePane consolePane, Config config,
    SynchronizerThread thread, String title)
  {
    this.consolePane = consolePane;
    this.config = config;
    this.thread = thread;
    this.title = title;
    simulationMode = config.getGeneralOptions().isSimulationMode();

    thread.setListener(this);

    textPane = new JTextPane();
    setViewportView(textPane);
    textPane.setEditable(false);

    updateFont();
    clear();
  }

  public SynchronizerThread getThread()
  {
    return thread;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public boolean isMainConsole()
  {
    return (mainConsole == null);
  }

  public ThreadConsole getMainConsole()
  {
    return mainConsole;
  }

  public void setMainConsole(ThreadConsole mainConsole)
  {
    this.mainConsole = mainConsole;
  }

  public boolean isCleared()
  {
    return cleared;
  }

  public void updateFont()
  {
    Config.LogOptions logOptions = config.getLogOptions();

    Style baseStyle = StyleContext.getDefaultStyleContext().
      getStyle(StyleContext.DEFAULT_STYLE);

    Style defautStyle = textPane.addStyle(DEFAULT_STYLE, baseStyle);
    StyleConstants.setFontFamily(defautStyle, logOptions.getLogFontFamily());
    StyleConstants.setFontSize(defautStyle, logOptions.getLogFontSize());

    Style simulationStyle = textPane.addStyle(SIMULATION_STYLE, baseStyle);
    StyleConstants.setFontFamily(simulationStyle, logOptions.getLogFontFamily());
    StyleConstants.setFontSize(simulationStyle, logOptions.getLogFontSize());
    StyleConstants.setItalic(simulationStyle, true);

    Style headerStyle = textPane.addStyle(HEADER_STYLE, defautStyle);
    StyleConstants.setBold(headerStyle, true);

    Style errorStyle = textPane.addStyle(ERROR_STYLE, defautStyle);
    StyleConstants.setForeground(errorStyle, Color.red);
  }

  public void updateTitle()
  {
    String tmpTitle = thread.getTargetMappings().getName();
    if (mainConsole != null)
      tmpTitle += getConsoleNameSuffix();

    setTitle(tmpTitle);
    consolePane.updateTitle(this);
  }

  private String getConsoleNameSuffix()
  {
    int i = title.lastIndexOf(" (");

    return (i == -1) ? "" : title.substring(i);
  }

  public boolean hasSameTargetMappings(ThreadConsole console)
  {
    return thread.getTargetMappings().equals(console.getThread().getTargetMappings());
  }

  public void breakLogs()
  {
    append("");
  }

  public void clear()
  {
    try
    {
      textPane.getDocument().remove(0, textPane.getDocument().getLength());
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }

    append(LabelsFactory.get(LabelsFactory.MSG_SYMBOLS) + " : "
      + INFO_COPY_NEW + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_NEW)
      + ", "
      + INFO_COPY_REPLACE + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_REPLACE)
      + ", "
      + INFO_COPY_EQUAL + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_EQUAL)
      + ", "
      + INFO_COPY_DELETED + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_DELETED)
      + ", "
      + INFO_COPY_NO_CLASS + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_NO_CLASS)
      + ", "
      + INFO_COPY_EXCLUDED + ":"
      + LabelsFactory.get(LabelsFactory.MSG_SYMBOL_EXCLUDED)
      + "",
      HEADER_STYLE);
    breakLogs();

    if (simulationMode)
      append(" - " + LabelsFactory.get(LabelsFactory.MSG_SIMULATION_ACTIVED)
        + " -\n", DEFAULT_STYLE);

    cleared = true;
  }

  private void append(String s)
  {
    append(s, simulationMode ? SIMULATION_STYLE : DEFAULT_STYLE);
  }

  private void append(final String s, final String styleName)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          Document doc = textPane.getDocument();
          int start = doc.getLength();
          // doc.insertString(start, s + "\n", textPane.getStyle(styleName));
          doc.insertString(start, "*" + s + "\n", textPane.getStyle(styleName));
          doc.remove(start, 1);

          // Add a temporary '*' and then remove it because of a bug of the
          // StyledEditorKit, see the test class
        }
        catch (BadLocationException ignored)
        {
        }
        scrollToEnd();
      }
    });
  }

  private void scrollToEnd()
  {
    textPane.setCaretPosition(textPane.getDocument().getLength());
  }

  public void configChanged(Config config)
  {
    updateFont();
    if (simulationMode != config.getGeneralOptions().isSimulationMode())
    {
      append(" - "
        + LabelsFactory.get((simulationMode)
        ? LabelsFactory.MSG_SIMULATION_DEACTIVED
        : LabelsFactory.MSG_SIMULATION_ACTIVED)
        + " -\n", DEFAULT_STYLE);
    }
    simulationMode = config.getGeneralOptions().isSimulationMode();
  }

  public void threadStarted(SynchronizerThread thread, Runnable runnable, VirtualFile[] files)
  {
    consolePane.updateTitle(this);

    if (config.getLogOptions().isClearBeforeSynchro())
      clear();

    if (config.getLogOptions().isAutoPopup())
      consolePane.doPopup();
  }

  public void threadResumed(SynchronizerThread thread)
  {
    consolePane.updateTitle(this);
  }

  public void threadStopped(SynchronizerThread thread,
    SyncronizingStatsInfo statsInfo)
  {
    consolePane.updateTitle(this);
    append(" - " + LabelsFactory.get(LabelsFactory.MSG_COPY_STOPPED) + " - ");
    breakLogs();
    cleared = false;
  }

  public void threadInterrupted(SynchronizerThread thread)
  {
    consolePane.updateTitle(this);
    append(" - " + LabelsFactory.get(LabelsFactory.MSG_COPY_INTERRUPTED) + " - ");
    cleared = false;
  }

  public void threadFinished(SynchronizerThread copierThread,
    SyncronizingStatsInfo statsInfo)
  {
    int count = statsInfo.getSuccessCount()
      + statsInfo.getIgnoredCount()
      + statsInfo.getFailuresCount();

    if (config.getLogOptions().isLogExludedPaths())
      count += statsInfo.getExcludedCount();

    if (count == 0)
    {
      append(LabelsFactory.get(LabelsFactory.MSG_NO_FILE_COPIED));
    }
    else
    {
      if (statsInfo.getSuccessCount() > 0)
      {
        String s = ((statsInfo.getSuccessCount() == 1)
          ? LabelsFactory.get(LabelsFactory.MSG_NB_FILE_COPIED)
          : LabelsFactory.get(LabelsFactory.MSG_NB_FILES_COPIED));
        append(statsInfo.getSuccessCount() + " " + s);
      }

      if (statsInfo.getDeletedCount() > 0)
      {
        String s = ((statsInfo.getDeletedCount() == 1)
          ? LabelsFactory.get(LabelsFactory.MSG_NB_FILE_DELETED)
          : LabelsFactory.get(LabelsFactory.MSG_NB_FILES_DELETED));
        append(statsInfo.getDeletedCount() + " " + s);
      }

      count = statsInfo.getIgnoredCount();
      if (config.getLogOptions().isLogExludedPaths())
        count += statsInfo.getExcludedCount();

      if (count > 0)
      {
        String s = ((count == 1)
          ? LabelsFactory.get(LabelsFactory.MSG_NB_FILE_IGNORED)
          : LabelsFactory.get(LabelsFactory.MSG_NB_FILES_IGNORED));
        append(count + " " + s);
      }
      if (statsInfo.getFailuresCount() > 0)
      {
        String s = ((statsInfo.getFailuresCount() == 1)
          ? LabelsFactory.get(LabelsFactory.MSG_NB_FAILURE)
          : LabelsFactory.get(LabelsFactory.MSG_NB_FAILURES));
        append(statsInfo.getFailuresCount() + " " + s, ERROR_STYLE);
      }
    }

    consolePane.updateTitle(this);
    breakLogs();
    cleared = false;
  }

  public void fileCopying(SynchronizerThread thread, final String src, final String dest,
    final int copyType)
  {
    final ConfigPathsManager pathsManager = thread.getPlugin().getPathManager();

    String tmpSrc = pathsManager.toPresentablePath(src);
    String tmpDest = dest;

    if (tmpDest != null)
    {
      tmpDest = pathsManager.toPresentablePath(dest);
    }

    String time = TIME_FORMATTER.format(new Date());
    String copyTypeInfo = (copyType == -1) ? "" : COPY_INFOS[copyType];

    if (tmpDest == null)
    {
      if (config.getLogOptions().isLogExludedPaths())
      {
        append(time + " " + copyTypeInfo + " "
          + "(" + LabelsFactory.get(LabelsFactory.MSG_FROM)
          + " " + tmpSrc + ")");
      }
    }
    else if ((copyType != SynchronizerThread.TYPE_COPY_IDENTICAL)
      || (config.getLogOptions().isLogIdenticalPaths()))
    {
      append(time + " " + copyTypeInfo + " " + tmpDest);
      if (config.getLogOptions().isLogSrcPaths())
      {
        append("[ " + LabelsFactory.get(LabelsFactory.MSG_FROM)
          + " " + tmpSrc + " ]");
      }
    }
    cleared = false;
  }

  public void fileDeleting(SynchronizerThread thread, String path)
  {
    String time = TIME_FORMATTER.format(new Date());
    path = thread.getPlugin().getPathManager().toPresentablePath(path);
    append(time + " " + INFO_COPY_DELETED + " " + path);
  }

  public void copyFailed(SynchronizerThread thread, Throwable t)
  {
    String msg = (t.getMessage() == null) ? t.toString() : t.getMessage();
    append(LabelsFactory.get(LabelsFactory.MSG_CANT_COPY_FILE) + " : " + msg,
      ERROR_STYLE);
    cleared = false;
  }

  public void dirDeletionFailed(SynchronizerThread syncronizerThread,
    String path)
  {
    append(LabelsFactory.get(LabelsFactory.MSG_CANT_DELETE_DIR), ERROR_STYLE);
    cleared = false;
  }

  public void fileDeletionFailed(SynchronizerThread syncronizerThread)
  {
    append(LabelsFactory.get(LabelsFactory.MSG_CANT_DELETE_FILE), ERROR_STYLE);
    cleared = false;
  }
}
