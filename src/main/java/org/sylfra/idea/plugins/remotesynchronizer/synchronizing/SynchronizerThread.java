package org.sylfra.idea.plugins.remotesynchronizer.synchronizing;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.model.SyncronizingStatsInfo;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * Manage a synchronizing in background.
 * <p/>
 * Method {@link #start(com.intellij.openapi.vfs.VirtualFile[])}
 * must be invoked to start synchronizing of given files/dirs.
 * <p/>
 * Virtual files are filtered to drop doublons and converted to absolute paths.
 * Then the remote dirs are inspected in order to delete obsolete files.
 * Finally, added and modified filkes are copied.
 * <p/>
 * This thread may be interrupted, resumed or stopped
 */
public class SynchronizerThread
{
  private final static int STATE_ACTIVE = 0;
  private final static int STATE_INTERRUPTED = 1;
  private final static int STATE_STOPPED = 2;

  public static final int TYPE_COPY_NEW = 0;
  public static final int TYPE_COPY_REPLACE = 1;
  public static final int TYPE_COPY_IDENTICAL = 2;
  public static final int TYPE_COPY_NOCLASS = 3;
  public static final int TYPE_COPY_EXCLUDED = 4;
  public static final int TYPE_COPY_DELETED = 5;

  private RemoteSynchronizerPlugin plugin;
  private TargetMappings targetMappings;
  private ThreadConsole console;
  private VirtualFile[] selectedFiles;
  private Stack<String> filesToCopy;
  private Stack<File> filesToDelete;
  private int state;
  private SyncronizingStatsInfo statsInfo;
  private SynchronizerThreadListener listener;

  public SynchronizerThread(RemoteSynchronizerPlugin plugin,
    TargetMappings targetMappings)
  {
    this.plugin = plugin;
    this.targetMappings = targetMappings;
    filesToCopy = new Stack<String>();
    filesToDelete = new Stack<File>();
    state = STATE_STOPPED;
  }

  public ThreadConsole getConsole()
  {
    return console;
  }

  public RemoteSynchronizerPlugin getPlugin()
  {
    return plugin;
  }

  public void setConsole(ThreadConsole console)
  {
    this.console = console;
  }

  public TargetMappings getTargetMappings()
  {
    return targetMappings;
  }

  public void setListener(SynchronizerThreadListener listener)
  {
    this.listener = listener;
  }

  public void start(VirtualFile[] files)
  {
    selectedFiles = files;

    state = STATE_ACTIVE;
    statsInfo = new SyncronizingStatsInfo();

    Thread t = runThread();
    listener.threadStarted(this, t, files);
  }

  public void rerun()
  {
    start(selectedFiles);
  }

  /**
   * Stop current synchronizing and clear non copied files
   */
  public void stop()
  {
    state = STATE_STOPPED;
    filesToCopy.clear();
    listener.threadStopped(this, statsInfo);
  }

  /**
   * Interrupt synchronizing
   */
  public void interrupt()
  {
    state = STATE_INTERRUPTED;
    listener.threadInterrupted(this);
  }

  /**
   * Resume synchronizing
   */
  public void resume()
  {
    state = STATE_ACTIVE;
    listener.threadResumed(this);
    runThread();
  }

  /**
   * Is the thread stopped ?
   */
  public boolean isAvailable()
  {
    return (state == STATE_STOPPED);
  }

  /**
   * Is the thread running ?
   */
  public boolean isActive()
  {
    return (state == STATE_ACTIVE);
  }

  /**
   * Is the thread interrupted ?
   */
  public boolean isInterrupted()
  {
    return (state == STATE_INTERRUPTED);
  }

  /**
   * Returns files last synchronized
   */
  public VirtualFile[] getSelectedFiles()
  {
    return selectedFiles;
  }

  /**
   * Starts thread
   */
  private Thread runThread()
  {
    Thread t = new Thread("RemoteSynchronizer")
    {
      public void run()
      {
        ApplicationManager.getApplication().runReadAction(new Runnable()
        {
          public void run()
          {
            synchronize();
          }
        });
      }
    };

    t.start();

    return t;
  }

  /**
   * Delete obsolete files/dir and then starts copies
   */
  private void synchronize()
  {
    filterFilesToCopy();
    filterFilesToDelete();

    while ((state == STATE_ACTIVE) && (!filesToDelete.empty()))
      deleteFile(filesToDelete.pop(), statsInfo);

    while ((state == STATE_ACTIVE) && (!filesToCopy.empty()))
      copyFile(filesToCopy.pop().toString(), statsInfo);

    if (state == STATE_ACTIVE)
      finished();
  }

  /**
   * Build stack of files to copy
   */
  private void filterFilesToCopy()
  {
    filesToCopy.clear();
    addPathsToCopy(selectedFiles);
  }

  /**
   * Selected files are filtered to drop doublons and converted to absolute
   * paths. Add files recursively.
   */
  private void addPathsToCopy(VirtualFile[] files)
  {
    if (files == null)
      return;

    ConfigPathsManager pathManager = plugin.getPathManager();
    for (VirtualFile f : files)
    {
      if (f.isDirectory())
      {
        addPathsToCopy(f.getChildren());
      }
      else
      {
        if (!filesToCopy.contains(f.getPath()))
        {
          filesToCopy.push(f.getPath());
        }

        if (pathManager.isJavaSource(f))
        {
          List<String> classFilePaths = plugin.getJavaSupport().getClassFilePaths(f);
          if (classFilePaths != null)
          {
            for (String path : classFilePaths)
            {
              if (!filesToCopy.contains(path))
              {
                filesToCopy.push(path);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Build stack of files to delete
   * Inspects all files under destinations within included paths. Keep only
   * files which are contained in files selection
   */
  private void filterFilesToDelete()
  {
    filesToDelete.clear();
    if (selectedFiles == null)
      return;

    // If there is no directory selected, no need to look for files to delete
    boolean dirSelected = false;
    for (VirtualFile selectedFile : selectedFiles)
    {
      if (selectedFile.isDirectory())
      {
        dirSelected = true;
        break;
      }
    }
    if (!dirSelected)
      return;

    SynchroMapping[] includedMappings = targetMappings.getSynchroMappings();
    for (SynchroMapping p : includedMappings)
    {
      if (p.isDeleteObsoleteFiles())
      {
        String destPath = plugin.getPathManager().expandPath(p.getDestPath(), false);
        pushFilesToDelete(new File(destPath));
      }
    }
  }

  /**
   * Add recursively files to delete
   */
  private void pushFilesToDelete(File destFile)
  {
    if (!destFile.exists())
      return;

    String srcPath = plugin.getPathManager()
      .getSrcPath(targetMappings, PathsUtils.toModelPath(destFile));

    if (srcPath != null)
    {
      File srcFile = new File(srcPath);

      if (((!srcFile.exists())
        || (plugin.getPathManager().isExcludedFromCopy(targetMappings, srcPath)))
        && (isContainedInSelection(srcPath))
        && (!filesToDelete.contains(destFile)))
      {
        filesToDelete.push(destFile);
      }
    }

    if (destFile.isDirectory())
    {
      File[] children = destFile.listFiles();
      if (children != null)
        for (File aChildren : children)
        {
          pushFilesToDelete(aChildren);
        }
    }
  }

  /**
   * Does this path belong to file selection ?
   */
  private boolean isContainedInSelection(String path)
  {
    for (VirtualFile file : selectedFiles)
    {
      if (plugin.getPathManager().isRelativePath(file.getPath(), path))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Delete a file and update stats
   */
  private void deleteFile(File f, SyncronizingStatsInfo statsInfo)
  {
    boolean isFile = f.isFile();
    if (!isFile)
    {
      File[] children = f.listFiles();
      if ((children == null) || (children.length > 0))
        return;
    }

    listener.fileDeleting(this, f.getAbsolutePath());

    boolean deleted = (plugin.getConfig().getGeneralOptions().isSimulationMode()) || f.delete();
    if (deleted)
    {
      if (isFile)
        statsInfo.addDeleted();
    }
    else
    {
      statsInfo.addFailure();
      if (isFile)
        listener.fileDeletionFailed(this);
      else
        listener.dirDeletionFailed(this, f.getAbsolutePath());
    }
  }

  /**
   * Try to copy specified file and update stats info
   */
  private void copyFile(String srcPath, SyncronizingStatsInfo statsInfo)
  {
    ConfigPathsManager pathsManager = plugin.getPathManager();
    String destPath = pathsManager.getRemotePath(targetMappings, srcPath);

    // Destination path not found
    if (destPath == null)
    {
      statsInfo.addExcluded();
      listener.fileCopying(this, srcPath, null, TYPE_COPY_EXCLUDED);
    }
    else
    {
      File srcFile = new File(srcPath);
      File destFile = new File(destPath);

      int copyType;
      if (!srcFile.exists())
        copyType = TYPE_COPY_NOCLASS;
      else if (!destFile.exists())
        copyType = TYPE_COPY_NEW;
      else if (srcFile.lastModified() == destFile.lastModified())
        copyType = TYPE_COPY_IDENTICAL;
      else if (destFile.isFile())
        copyType = TYPE_COPY_REPLACE;
      else
        copyType = -1;

      // File ignored
      if ((copyType == TYPE_COPY_IDENTICAL)
        || (copyType == TYPE_COPY_NOCLASS))
      {
        statsInfo.addIgnored();
        listener.fileCopying(this, srcPath, destPath, copyType);
      }
      else
      {
        listener.fileCopying(this, srcPath, destPath, copyType);

        if (!checkParentFile(destFile))
        {
          listener.copyFailed(this,
            new Throwable(LabelsFactory.get(LabelsFactory.MSG_CANT_MAKE_DIRS)));
          statsInfo.addFailure();
          return;
        }
        else
        {
          // Copy files
          try
          {
            if (!plugin.getConfig().getGeneralOptions().isSimulationMode())
              Utils.copyFile(srcFile, destFile);
            statsInfo.addSuccess();
          }
          catch (IOException ex)
          {
            listener.copyFailed(this, ex);
            statsInfo.addFailure();
          }
        }
      }
    }
  }

  // Create directory if needed and allowed
  private boolean checkParentFile(File destFile)
  {
    File parentFile = destFile.getParentFile();
    boolean parentIsDir;

    if (parentFile.isFile())
    {
      parentIsDir = false;
    }
    else
    {
      if ((!plugin.getConfig().getGeneralOptions().isSimulationMode())
        && (!parentFile.exists())
        && (plugin.getConfig().getGeneralOptions().isCreateMissingDirs()))
      {
        parentIsDir = parentFile.mkdirs();
      }
      else
        parentIsDir = true;
    }
    return parentIsDir;
  }

  /**
   * Called after the synchronizing has ended
   */
  protected void finished()
  {
    state = STATE_STOPPED;
    listener.threadFinished(this, statsInfo);
    if (statsInfo.hasFailures())
    {
      final ToolWindowManager twManager = ToolWindowManager
        .getInstance(plugin.getProject());
      twManager.invokeLater(new Runnable()
      {
        public void run()
        {
          twManager.getToolWindow(RemoteSynchronizerPlugin.PLUGIN_NAME).show(null);
        }
      });
    }
  }
}
