package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;

/**
 * Synchronize opened files
 */
public class RemoteSynchronizeOpenedAction extends AbstractRemoteSynchronizeAction
{
  protected VirtualFile[] getFiles(RemoteSynchronizerPlugin plugin,
    DataContext dataContext)
  {
    return FileEditorManager.getInstance(plugin.getProject()).getOpenFiles();
  }
}
