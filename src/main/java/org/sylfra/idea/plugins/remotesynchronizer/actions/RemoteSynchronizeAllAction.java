package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;

/**
 * Synchronize all project files
 */
public class RemoteSynchronizeAllAction extends AbstractRemoteSynchronizeAction
{
  protected VirtualFile[] getFiles(RemoteSynchronizerPlugin plugin,
    DataContext dataContext)
  {
    return ProjectRootManager.getInstance(plugin.getProject()).getContentRoots();
  }
}
