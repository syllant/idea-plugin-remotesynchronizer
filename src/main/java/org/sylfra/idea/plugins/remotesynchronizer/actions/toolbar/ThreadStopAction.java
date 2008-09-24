package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Stop current synchronization
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ThreadStopAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    Utils.getPlugin(e).getConsolePane().getCurrentConsole().getThread().stop();
  }

  public void update(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);

    boolean running = (t != null) && (!t.isAvailable());
    e.getPresentation().setEnabled(running);
  }
}
