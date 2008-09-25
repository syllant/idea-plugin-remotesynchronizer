package org.sylfra.idea.plugins.remotesynchronizer.model;

import java.util.ArrayList;
import java.util.Arrays;


public class ConfigSample
{
  public static final String PROJECT_ROOT = "c:/Project1";
  public static final String DEST_ROOT = "r:/remote";

  private Config config;
  private TargetMappings target1;

  public ConfigSample()
  {
    config = new Config();
    target1 = new TargetMappings();
    target1.setName("Target1");

    config.setTargetMappings(new ArrayList<TargetMappings>(Arrays.asList(target1)));

    // IncludedPathMappings
    target1.setSynchroMappings(new SynchroMapping[]
    {
      new SynchroMapping(PROJECT_ROOT + "/classes", DEST_ROOT + "/classes",
        true),
      new SynchroMapping(PROJECT_ROOT + "/dir1", DEST_ROOT + "/dir1-new", true),
      new SynchroMapping(PROJECT_ROOT + "/dir2/dir2-1",
        DEST_ROOT + "/dir2-1-new", true),
      new SynchroMapping(PROJECT_ROOT + "/file1.txt",
        DEST_ROOT + "/files/file1.txt", true),
      new SynchroMapping(PROJECT_ROOT + "/file2.txt", DEST_ROOT + "/files/",
        true),
      new SynchroMapping(PROJECT_ROOT + "/file3.txt",
        DEST_ROOT + "/files/file3-new.txt", true),
      new SynchroMapping(PROJECT_ROOT, DEST_ROOT + "/remain-dir", true)
    });

    // ExcludedPaths
    target1.setExcludedCopyPaths(new String[]
    {
      PROJECT_ROOT + "/classes/test"
    });
  }

  public Config getConfig()
  {
    return config;
  }

  public TargetMappings getTarget1()
  {
    return target1;
  }
}
