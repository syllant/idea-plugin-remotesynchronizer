package org.sylfra.idea.plugins.remotesynchronizer.model;


/**
 * Mapping between a source path and a destination path used to specify
 * included paths
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class SynchroMapping implements Cloneable
{
  private String srcPath;
  private String destPath;
  private boolean deleteObsoleteFiles;

  public SynchroMapping()
  {
  }

  public SynchroMapping(String destPath,
    String srcPath, boolean deleteObsoleteFiles)
  {
    this.destPath = destPath;
    this.srcPath = srcPath;
    this.deleteObsoleteFiles = deleteObsoleteFiles;
  }

  public String getSrcPath()
  {
    return srcPath;
  }

  public void setSrcPath(String srcPath)
  {
    this.srcPath = srcPath;
  }

  public String getDestPath()
  {
    return destPath;
  }

  public void setDestPath(String destPath)
  {
    this.destPath = destPath;
  }

  public boolean isDeleteObsoleteFiles()
  {
    return deleteObsoleteFiles;
  }

  public void setDeleteObsoleteFiles(boolean deleteObsoleteFiles)
  {
    this.deleteObsoleteFiles = deleteObsoleteFiles;
  }

  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof SynchroMapping)) return false;

    final SynchroMapping synchroMapping = (SynchroMapping) o;

    if (deleteObsoleteFiles != synchroMapping.deleteObsoleteFiles) return false;
    if (destPath != null
      ? !destPath.equals(synchroMapping.destPath)
      : synchroMapping.destPath != null)
      return false;
    if (srcPath != null
      ? !srcPath.equals(synchroMapping.srcPath)
      : synchroMapping.srcPath != null)
      return false;

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (srcPath != null ? srcPath.hashCode() : 0);
    result = 29 * result + (destPath != null ? destPath.hashCode() : 0);
    result = 29 * result + (deleteObsoleteFiles ? 1 : 0);
    return result;
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      return null;
    }
  }
}
