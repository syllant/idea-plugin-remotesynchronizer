package org.sylfra.idea.plugins.remotesynchronizer.model;

import java.io.Serializable;

public class TargetMappings implements Serializable
{
  public static final String[] DEFAULT_EXCLUDES_COPY = new String[]
  {
    "**/.dependency-info/*",
    "**/.svn/*",
    "**/CVS/*",
    "**/*.java"
  };

  private String name;
  private boolean active;
  private SynchroMapping[] synchroMappings;
  private String[] excludedCopyPaths;
  private String[] excludedDeletePaths;

  public TargetMappings()
  {
    this.active = true;

    synchroMappings = new SynchroMapping[0];
    excludedCopyPaths = DEFAULT_EXCLUDES_COPY;
    excludedDeletePaths = new String[0];
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  public SynchroMapping[] getSynchroMappings()
  {
    return synchroMappings;
  }

  public void setSynchroMappings(SynchroMapping[] synchroMappings)
  {
    this.synchroMappings = synchroMappings;
  }

  public String[] getExcludedCopyPaths()
  {
    return excludedCopyPaths;
  }

  public void setExcludedCopyPaths(String[] excludedCopyPaths)
  {
    this.excludedCopyPaths = excludedCopyPaths;
  }

  public String[] getExcludedDeletePaths()
  {
    return excludedDeletePaths;
  }

  public void setExcludedDeletePaths(String[] excludedDeletePaths)
  {
    this.excludedDeletePaths = excludedDeletePaths;
  }
}
