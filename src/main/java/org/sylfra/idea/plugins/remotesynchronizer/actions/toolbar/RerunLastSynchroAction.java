package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Remove the current console
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class RerunLastSynchroAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);

    if (t.isAvailable())
      t.rerun();
  }

  public void update(AnActionEvent e)
  {
    SynchronizerThread t = Utils.getCurrentCopierThread(e);
    boolean enabled = (t != null) && (t.isAvailable()) && (t.getSelectedFiles() != null);
    e.getPresentation().setEnabled(enabled);
  }
}
