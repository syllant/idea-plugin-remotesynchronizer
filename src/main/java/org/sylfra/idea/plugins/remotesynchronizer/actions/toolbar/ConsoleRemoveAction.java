package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Remove the current console
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ConsoleRemoveAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    RemoteSynchronizerPlugin plugin = Utils.getPlugin(e);
    ThreadConsole console = plugin.getConsolePane().removeCurrentConsole();
    plugin.getCopierThreadManager().removeThread(console.getThread());
  }

  public void update(AnActionEvent e)
  {
    RemoteSynchronizerPlugin plugin = Utils.getPlugin(e);
    e.getPresentation().setEnabled((plugin != null)
      && (plugin.getConsolePane().getComponentCount() > 1)
      && (plugin.getConsolePane().getCurrentConsole() != null)
      && (plugin.getConsolePane().getCurrentConsole().getThread().isAvailable())
      && (!plugin.getConsolePane().getCurrentConsole().isMainConsole()));
  }
}
