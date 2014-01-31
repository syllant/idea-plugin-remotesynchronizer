package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Resume current synchronization
 */
public class ThreadResumeAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);

    if (t.isInterrupted())
      t.resume();
  }

  public void update(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);
    e.getPresentation().setVisible(((t != null) && (t.isInterrupted())));
  }
}
