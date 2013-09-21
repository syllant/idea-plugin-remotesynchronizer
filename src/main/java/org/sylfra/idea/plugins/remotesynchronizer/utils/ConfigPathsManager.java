package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.ConfigListener;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides support for paths management, using current project and plugin
 * settings
 * <p/>
 * NB : paths are handled with '/'
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ConfigPathsManager
{
  private static final int PATTERN_PROJECT_DIR_LENGTH = PathsUtils.PATTERN_PROJECT_DIR.length();

  private RemoteSynchronizerPlugin plugin;
  private PathsCacheManager pathsCacheManager;

  public ConfigPathsManager(RemoteSynchronizerPlugin plugin)
  {
    this.plugin = plugin;
    pathsCacheManager = new PathsCacheManager(plugin.getConfig());
    plugin.getConfig().addConfigListener(pathsCacheManager);
  }

  public RemoteSynchronizerPlugin getPlugin()
  {
    return plugin;
  }

  public boolean isRelativePath(String path)
  {
    return isRelativePath(
      ProjectRootManager.getInstance(plugin.getProject()).getContentRoots(), path);
  }

  public boolean isRelativePath(VirtualFile[] roots, String path)
  {
    for (VirtualFile root : roots)
    {
      if (isRelativePath(root.getPath(), path))
      {
        return true;
      }
    }

    return false;
  }

  public boolean isRelativePath(String root, String path)
  {
    path = expandPath(path, true);
    return ((path.toLowerCase().indexOf(root.toLowerCase()) == 0)
      && ((path.length() == root.length()
      || (path.charAt(root.length()) == '/'))));
  }

  public boolean isOutputPath(String path)
  {
    Module[] modules = ModuleManager.getInstance(plugin.getProject()).getModules();
    for (Module module : modules)
    {
      CompilerModuleExtension cme = CompilerModuleExtension.getInstance(module);
      if (cme == null)
      {
        return false;
      }
      
      // Use getCompilerOutputXXXPointer since getCompilerOutputXXXPath return NULL when directory does not exist
      if (((cme.getCompilerOutputPointer() != null)
        && isRelativePath(PathsUtils.toModelPath(cme.getCompilerOutputPointer().getPresentableUrl()), path))
        || ((cme.getCompilerOutputForTestsPointer() != null)
        && isRelativePath(PathsUtils.toModelPath(cme.getCompilerOutputForTestsPointer().getPresentableUrl()), path)))
      {
        return true;
      }
    }

    return false;
  }

  public boolean isJavaSource(VirtualFile f)
  {
    if (!f.getName().endsWith(".java"))
      return false;

    VirtualFile[] vf = ProjectRootManager.getInstance(plugin.getProject()).getContentSourceRoots();

    for (VirtualFile aVf : vf)
    {
      if (PathsUtils.isAncestor(aVf, f))
      {
        return true;
      }
    }

    return false;
  }

  public VirtualFile getProjectDefaultRoot()
  {
    VirtualFile[] vf = ProjectRootManager.getInstance(plugin.getProject())
      .getContentRoots();

    return (vf.length == 0) ? plugin.getProject().getBaseDir() : vf[0];
  }

  public String getRelativePath(String path)
  {
    VirtualFile f = PathsUtils.getVirtualFile(path);
    if (f == null)
      return path;

    return getRelativePath(f);
  }

  public String getRelativeOutputPath(String path)
  {
    VirtualFile f = PathsUtils.getVirtualFile(path);
    if (f == null)
      return path;

    return getRelativePath(f);
  }

  public String getRelativePath(VirtualFile f)
  {
    return PathsUtils.getRelativePath(
      ProjectRootManager.getInstance(plugin.getProject()).getContentRoots(), f);
  }

  public String getRelativeSourcePath(VirtualFile f)
  {
    return PathsUtils.getRelativePath(
      ProjectRootManager.getInstance(plugin.getProject()).getContentSourceRoots(), f);
  }

  public String getOutputPath(VirtualFile f)
  {
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(plugin.getProject())
      .getFileIndex();
    Module module = fileIndex.getModuleForFile(f);

    VirtualFile vFile;
    if (fileIndex.isInTestSourceContent(f))
    {
      vFile = CompilerModuleExtension.getInstance(module).getCompilerOutputPathForTests();
    }
    else if (fileIndex.isInSourceContent(f))
    {
      vFile = CompilerModuleExtension.getInstance(module).getCompilerOutputPath();
    }
    else
    {
      vFile = null;
    }

    if (vFile == null)
    {
      return null;
    }

    return PathsUtils.toModelPath(vFile.getPresentableUrl());
  }

  /**
   * Returns classes files path for java source file.
   * Several classes files may correspond to one java file due to inner classes
   *
   * @param f java source file
   * @return if main class file does not exist, a list containing its path.
   *         Otherwise a list containing paths of main class and its existing inner
   *         classes
   */
  public List<String> getClassFilePaths(VirtualFile f)
  {
    String outputPath = getOutputPath(f);
    if (outputPath == null)
    {
      return null;
    }

    PsiManager psiManager = PsiManager.getInstance(plugin.getProject());
    PsiJavaFile psiJavaFile = (PsiJavaFile) psiManager.findFile(f);
    assert psiJavaFile != null;
    PsiClass[] classes = psiJavaFile.getClasses();

    // Each class defined in a source file may contain several inner class...
    final List<String> result = new ArrayList<String>();
    for (PsiClass aClass : classes)
    {
      final String path = outputPath + '/'
        + aClass.getQualifiedName().replace('.', '/') + ".class";

      VirtualFile c = LocalFileSystem.getInstance().findFileByPath(path);
      result.addAll(getInnerClassFilePaths(c));
      result.add(path);
    }

    return result;
  }

  /**
   * Return a list with all inner classes paths (anonymous or not) for the
   * specified class file
   */
  private List<String> getInnerClassFilePaths(VirtualFile c)
  {
    List<String> result = new ArrayList<String>();

    if (c != null)
    {
      String baseName = c.getNameWithoutExtension() + "$";
      VirtualFile parent = c.getParent();
      VirtualFile[] children = parent.getChildren();

      for (VirtualFile child : children)
      {
        if (child.getNameWithoutExtension().indexOf(baseName) == 0)
        {
          result.add(child.getPath());
        }
      }
    }

    return result;
  }

  public boolean isExcludedFromCopy(TargetMappings target, String path)
  {
    return isExcluded(target.getExcludedCopyPaths(), path);
  }

  public boolean isExcludedFromDeletion(TargetMappings target, String path)
  {
    return isExcluded(target.getExcludedDeletePaths(), path);
  }

  private boolean isExcluded(String[] paths, String path)
  {
    for (String itPath : paths)
    {
      File f = new File(path);
      if (f.isDirectory())
      {
        path += "/";
      }

      if (SelectorUtils.match(itPath, path))
      {
        return true;
      }
    }

    return false;
  }

  public String getRemotePath(TargetMappings target, String path)
  {
    String result = pathsCacheManager.getRemotePath(target, path);
    if (result != null)
      return (PathsCacheManager.NULL_PATH.equals(result)) ? null : result;

    if (isExcludedFromCopy(target, path))
    {
      pathsCacheManager.storeRemotePath(target, path,
        PathsCacheManager.NULL_PATH);
      return null;
    }

    result = findRemotePath(target, path);

    pathsCacheManager.storeRemotePath(target, path,
      (result == null) ? PathsCacheManager.NULL_PATH : result);

    return result;
  }

  private String findRemotePath(TargetMappings target,
    String path)
  {
    String bestPath = null;
    String destPath = null;

    // Find best included path
    for (int i = 0; i < target.getSynchroMappings().length; i++)
    {
      SynchroMapping pathMapping = target.getSynchroMappings()[i];
      if (matchesBetter(path, pathMapping.getSrcPath(), bestPath))
      {
        bestPath = expandPath(pathMapping.getSrcPath(), true);
        destPath = expandPath(pathMapping.getDestPath(), true);
      }
    }

    return buildPathFromBestPath(bestPath, path, destPath);
  }

  public String getSrcPath(TargetMappings target, String path)
  {
    String result = pathsCacheManager.getSrcPath(target, path);
    if (result != null)
      return (PathsCacheManager.NULL_PATH.equals(result)) ? null : result;

    if (isExcludedFromDeletion(target, path))
    {
      pathsCacheManager.storeSrcPath(target, path,
        PathsCacheManager.NULL_PATH);
      return null;
    }

    String bestPath = null;

    for (int i = 0; i < target.getSynchroMappings().length; i++)
    {
      SynchroMapping pathMapping = target.getSynchroMappings()[i];
      String destPath = expandPath(pathMapping.getDestPath(), true);

      if (path.indexOf(destPath) == 0)
      {
        String srcPath = expandPath(pathMapping.getSrcPath(), true);
        String tmp = buildPathFromBestPath(destPath, path, srcPath);

        // check is this path is not precisely linked
        if ((path.equals(findRemotePath(target, tmp)))
          && (matchesBetter(path, destPath, bestPath)))
        {
          result = tmp;
          bestPath = destPath;
        }
      }
    }

    pathsCacheManager.storeSrcPath(target, path,
      (result == null) ? PathsCacheManager.NULL_PATH : result);

    return result;
  }

  private String buildPathFromBestPath(String bestPath, String paramPath,
    String foundPath)
  {
    if (bestPath == null)
      return null;

    // Build absolute src path
    if (paramPath.toLowerCase().indexOf(bestPath.toLowerCase()) != 0)
      return null;

    if (foundPath.charAt(foundPath.length() - 1) == '/')
    {
      int i = paramPath.lastIndexOf('/');
      if ((i == -1) && (paramPath.length() > 1))
        return null;
      return foundPath + paramPath.substring(i + 1);
    }

    paramPath = paramPath.substring(bestPath.length());

    if ((!"".equals(paramPath))
      && (paramPath.charAt(0) != '/')
      && (foundPath.charAt(foundPath.length() - 1) != '/'))
    {
      foundPath += '/';
    }

    return foundPath + paramPath;
  }

  public String toPresentablePath(String path)
  {
    path = path.replace('/', File.separatorChar);

    return path;
  }

  public String expandPath(String path, boolean modelPath)
  {
    if (path.startsWith(PathsUtils.PATTERN_PROJECT_DIR))
    {
      path = plugin.getProject().getBaseDir().getPresentableUrl()
        + path.substring(PATTERN_PROJECT_DIR_LENGTH);
      try
      {
        path = new File(path).getCanonicalPath();
      }
      catch (IOException e)
      {
        // ignored, can't do more and logging is not relevant
      }
    }

    if (modelPath)
      path = PathsUtils.toModelPath(path);

    return path;
  }

  public boolean matchesBetter(String path, String testPath, String bestPath)
  {
    testPath = expandPath(testPath, true);
    if ((testPath == null) || (path.toLowerCase().indexOf(testPath.toLowerCase()) != 0))
    {
      return false;
    }

    if (testPath.length() < path.length())
    {
      char c = path.charAt(testPath.length());
      if ((testPath.charAt(testPath.length() - 1) != '/') && (c != '/'))
      {
        return false;
      }
    }

    return ((bestPath == null) || (testPath.length() > bestPath.length()));
  }

  private final static class PathsCacheManager
    implements ConfigListener
  {
    private final static String NULL_PATH = "<null>";

    private Map<TargetMappings, Map<String, String>[]> caches;

    public PathsCacheManager(Config config)
    {
      caches = new HashMap<TargetMappings, Map<String, String>[]>();
      configChanged(config);
    }

    public void configChanged(Config config)
    {
      caches.clear();
      for (TargetMappings targetMappings : config.getTargetMappings())
      {
        //noinspection unchecked
        caches.put(targetMappings, new Map[]{
          new HashMap<String, String>(),
          new HashMap<String, String>()});
      }
    }

    public synchronized String getRemotePath(TargetMappings target,
      String path)
    {
      return (String) ((Map[]) caches.get(target))[0].get(path);
    }

    public synchronized String getSrcPath(TargetMappings target, String path)
    {
      return (String) ((Map[]) caches.get(target))[1].get(path);
    }

    public synchronized void storeRemotePath(TargetMappings target, String path,
      String remotePath)
    {
      caches.get(target)[0].put(path, remotePath);
    }

    public synchronized void storeSrcPath(TargetMappings target, String path,
      String srcPath)
    {
      caches.get(target)[1].put(path, srcPath);
    }
  }
}
