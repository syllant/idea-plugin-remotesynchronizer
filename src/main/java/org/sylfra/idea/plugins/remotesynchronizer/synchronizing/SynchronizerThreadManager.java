package org.sylfra.idea.plugins.remotesynchronizer.synchronizing;

import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.ConfigListener;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsolePane;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains a {@link SynchronizerThread} objects list.
 * <p/>
 * When a new synchronizing action is invoked, an available thread is required.
 * If no thread is available (ie. all threads are running), a new thread is
 * created
 */
public class SynchronizerThreadManager
  implements ConfigListener
{
  private RemoteSynchronizerPlugin plugin;
  private List<SynchronizerThread> threads;

  public SynchronizerThreadManager(RemoteSynchronizerPlugin plugin)
  {
    this.plugin = plugin;
    plugin.getConfig().addConfigListener(this);
    threads = new ArrayList<SynchronizerThread>();

    updateThreads();
  }

  /**
   * Invoked from an synchronization acton
   */
  public void launchSynchronization(VirtualFile[] files)
  {
    for (TargetMappings targetMappings : plugin.getConfig().getTargetMappings())
    {
      if (targetMappings.isActive())
      {
        SynchronizerThread thread = getNotNullAvailableThread(targetMappings);
        thread.start(files);
      }
    }
  }

  /**
   * If no thread is available (ie. all threads are running), a new thread
   * is created
   */
  private SynchronizerThread getNotNullAvailableThread(TargetMappings target)
  {
    SynchronizerThread result = getAvailableThread(target);
    if (result != null)
      return result;

    result = new SynchronizerThread(plugin, target);
    plugin.getConsolePane().createConsole(plugin, result, true);
    threads.add(result);

    return result;
  }

  public SynchronizerThread getAvailableThread(TargetMappings target)
  {
    SynchronizerThread result;
    for (SynchronizerThread thread : threads)
    {
      result = thread;
      if (result.getTargetMappings().equals(target) && (result.isAvailable()))
      {
        return result;
      }
    }

    return null;
  }

  public boolean hasRunningSynchro()
  {
    SynchronizerThread result;
    for (SynchronizerThread thread : threads)
    {
      result = thread;
      if (!result.isAvailable())
      {
        return true;
      }
    }

    return false;
  }

  public void removeThread(SynchronizerThread thread)
  {
    threads.remove(thread);
  }

  /**
   * Update thread list after configuration has changed
   */
  private void updateThreads()
  {
    List<SynchronizerThread> newThreads = new ArrayList<SynchronizerThread>();
    ThreadConsolePane consolePane = plugin.getConsolePane();
    Component selectedTab = consolePane.getSelectedComponent();
    consolePane.removeAll();

    // First add threads from config mappings, trying to retrieve existing
    // threads
    for (TargetMappings targetMappings : plugin.getConfig().getTargetMappings())
    {
      if (!targetMappings.isActive())
      {
        continue;
      }

      // Try to retrieve existing threads (and their console)
      SynchronizerThread thread = null;
      boolean found = false;
      Iterator<SynchronizerThread> itThreads = threads.iterator();
      while (itThreads.hasNext())
      {
        thread = itThreads.next();
        if (thread.getTargetMappings().equals(targetMappings))
        {
          newThreads.add(thread);
          itThreads.remove();
          found = true;
          break;
        }
      }

      if (found)
      {
        consolePane.addConsole(thread.getConsole(), false);
      }
      else
      {
        thread = new SynchronizerThread(plugin, targetMappings);
        consolePane.createConsole(plugin, thread, false);
        newThreads.add(thread);
      }
    }

    // Second, trying to retreive "secondary" threads, ie those which were
    // created because their main thread was busy
    for (SynchronizerThread thread : threads)
    {
      if ((thread.getTargetMappings().isActive())
        && (plugin.getConfig().getTargetMappings()
        .contains(thread.getTargetMappings())))
      {
        newThreads.add(thread);
        consolePane.addConsole(thread.getConsole(), true);
      }
    }

    int i = consolePane.indexOfComponent(selectedTab);
    consolePane.setSelectedIndex((i > -1)
      ? i
      : (consolePane.getComponentCount() == 0) ? -1 : 0);

    threads = newThreads;
  }

  public void configChanged(Config config)
  {
    updateThreads();
  }
}
