package org.sylfra.idea.plugins.remotesynchronizer.javasupport;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 */
public interface IJavaSupport
{
  VirtualFile[] getSelectedFiles(DataContext dataContext);

  boolean insideModule(DataContext dataContext);

  List<String> getClassFilePaths(VirtualFile f);
}
