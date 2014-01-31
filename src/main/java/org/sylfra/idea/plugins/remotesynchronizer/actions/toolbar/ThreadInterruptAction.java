package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Interrupt current synchronization
 */
public class ThreadInterruptAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);

    if (!t.isInterrupted())
      t.interrupt();
  }

  public void update(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);

    boolean show = ((t == null) || (!t.isInterrupted()));
    boolean enabled = ((t != null) && (t.isActive()));

    e.getPresentation().setVisible(show);
    e.getPresentation().setEnabled(enabled);
  }
}
