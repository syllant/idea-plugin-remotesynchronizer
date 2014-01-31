package org.sylfra.idea.plugins.remotesynchronizer.javasupport;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:syllant@gmail.com">Sylvain FRANCOIS</a>
 * @version $Id$
 */
public class DefaultJavaSupport extends AbstractProjectComponent implements IJavaSupport
{
  private final Project project;

  protected DefaultJavaSupport(Project project)
  {
    super(project);
    this.project = project;
  }

  public VirtualFile[] getSelectedFiles(DataContext dataContext)
  {
    VirtualFile[] selectedFiles = DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    if (selectedFiles == null)
    {
      Module module = DataKeys.MODULE.getData(dataContext);
      return (module == null) ? null : ModuleRootManager.getInstance(module).getContentRoots();
    }
    return selectedFiles;
  }

  public boolean insideModule(DataContext dataContext)
  {
    return (DataKeys.MODULE.getData(dataContext) != null);
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

    PsiManager psiManager = PsiManager.getInstance(project);
    PsiJavaFile psiJavaFile = (PsiJavaFile) psiManager.findFile(f);
    assert psiJavaFile != null;
    PsiClass[] classes = psiJavaFile.getClasses();

    // Each class defined in a source file may contain several inner class...
    final List<String> result = new ArrayList<String>();
    for (PsiClass aClass : classes)
    {
      final String path = outputPath + '/' + aClass.getQualifiedName().replace('.', '/') + ".class";

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
  List<String> getInnerClassFilePaths(VirtualFile c)
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

  public String getRelativeSourcePath(VirtualFile f)
  {
    return PathsUtils.getRelativePath(
      ProjectRootManager.getInstance(project).getContentSourceRoots(), f);
  }

  public String getOutputPath(VirtualFile f)
  {
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project)
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
      ProjectRootManager.getInstance(project).getContentRoots(), f);
  }
}
