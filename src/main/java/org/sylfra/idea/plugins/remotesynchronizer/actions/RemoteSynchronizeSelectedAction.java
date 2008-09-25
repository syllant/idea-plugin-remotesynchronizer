package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;

/**
 * Synchronize selected files
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class RemoteSynchronizeSelectedAction extends AbstractRemoteSynchronizeAction
{
  protected VirtualFile[] getFiles(RemoteSynchronizerPlugin plugin,
    DataContext dataContext)
  {
    VirtualFile[] selectedFiles = DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    if (selectedFiles == null)
    {
      Module module = DataKeys.MODULE.getData(dataContext);
      return (module == null) ? null : ModuleRootManager.getInstance(module).getContentRoots();
    }

    return selectedFiles;
  }

  public boolean isEnabled(AnActionEvent e)
  {
    // Some files must be selected
    return super.isEnabled(e) 
      && ((DataKeys.MODULE.getData(e.getDataContext()) != null)
      || (DataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext()) != null));
  }
}
