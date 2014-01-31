package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Clear the current console
 */
public class ConsoleClearAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    ThreadConsole currentConsole = Utils.getPlugin(e).getConsolePane()
      .getCurrentConsole();
    if (currentConsole != null)
      currentConsole.clear();
  }

  public void update(AnActionEvent e)
  {
    RemoteSynchronizerPlugin plugin = Utils.getPlugin(e);
    e.getPresentation().setEnabled((plugin != null)
      && (plugin.getConsolePane().getCurrentConsole() != null)
      && (!plugin.getConsolePane().getCurrentConsole().isCleared()));
  }
}
