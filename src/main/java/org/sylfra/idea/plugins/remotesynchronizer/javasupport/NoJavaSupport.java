package org.sylfra.idea.plugins.remotesynchronizer.javasupport;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:syllant@gmail.com">Sylvain FRANCOIS</a>
 * @version $Id$
 */
public class NoJavaSupport implements IJavaSupport
{
  public VirtualFile[] getSelectedFiles(DataContext dataContext)
  {
    return CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
  }

  public boolean insideModule(DataContext dataContext)
  {
    return false;
  }

  public List<String> getClassFilePaths(VirtualFile f)
  {
    return Collections.emptyList();
  }
}
