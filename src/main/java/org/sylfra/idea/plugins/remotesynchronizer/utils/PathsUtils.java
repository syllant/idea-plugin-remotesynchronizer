package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * Utility methods for paths management
 */
public class PathsUtils
{
  public static final String PATTERN_PROJECT_DIR = "$PROJECT_DIR$";

  public static String toModelPath(String path)
  {
    return path.replace(File.separatorChar, '/');
  }

  public static String toModelPath(File f)
  {
    return toModelPath(f.getAbsolutePath());
  }

  public static VirtualFile getVirtualFile(String path)
  {
    return LocalFileSystem.getInstance().findFileByPath(path);
  }

  public static boolean isAncestor(String childPath, String parentPath)
  {
    File childFile = new File(childPath);
    File parentFile = new File(parentPath);

    while (childFile != null)
    {
      if (childFile.equals(parentFile))
      {
        return true;
      }
      childFile = childFile.getParentFile();
    }

    return false;
  }

  public static boolean isAncestor(VirtualFile parent, VirtualFile f)
  {
    VirtualFile tmp = f;
    while (tmp != null)
    {
      if (tmp.equals(parent))
      {
        return true;
      }

      tmp = tmp.getParent();
    }

    return false;
  }

  public static String getRelativePath(VirtualFile[] roots, VirtualFile f)
  {
    for (VirtualFile root : roots)
    {
      String s = VfsUtil.getRelativePath(f, root, '/');
      if (s != null)
      {
        return s;
      }
    }

    return f.getPath();
  }

  public static String getRelativePath(Project project, String path)
  {
    VirtualFile projectDir = project.getBaseDir();

    StringBuilder buffer = new StringBuilder();
    String relPath = null;
    while ((projectDir != null)
      && ((relPath = getRelativePath(path, projectDir.getPath())) == null))
    {
      buffer.append("../");
      projectDir = projectDir.getParent();
    }

    // Unexpected
    if (projectDir == null)
    {
      return path;
    }

    return PATTERN_PROJECT_DIR + "/" + buffer + relPath;
  }

  private static String getRelativePath(String path, String basePath)
  {
    path = toModelPath(path);
    basePath = toModelPath(basePath);
    if (!basePath.endsWith("/"))
      basePath += "/";

    int pos = path.indexOf(basePath);
    return (pos == -1) ? null : path.substring(basePath.length());
  }

  public static String replaceJavaExtensionByClass(String path)
  {
    return path.substring(0, path.length() - 4) + "class";
  }

}
