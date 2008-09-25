package org.sylfra.idea.plugins.remotesynchronizer.utils;

import junit.framework.TestCase;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.ConfigSample;

public class ConfigPathsManagerTest extends TestCase
{
  private ConfigPathsManager pathsManager;

  public ConfigPathsManagerTest(String name)
  {
    super(name);
    Config config = new ConfigSample().getConfig();
    pathsManager = new ConfigPathsManager(null)
    {
      public String getRelativePath(String path)
      {
        int i = path.indexOf(ConfigSample.PROJECT_ROOT);
        return (i == -1)
          ? path
          : path.substring(ConfigSample.PROJECT_ROOT.length() + 1);
      }
    };
  }

  public void testGetRemotePath()
  {
    ConfigSample config = new ConfigSample();

    // Test srcPath == "."
    String s = pathsManager.getRemotePath(config.getTarget1(),
      ConfigSample.PROJECT_ROOT + "/dir99/test99.txt");
    assertEquals(ConfigSample.DEST_ROOT + "/remain-dir/dir99/test99.txt", s);

    // Test include file to file
    s = pathsManager.getRemotePath(config.getTarget1(),
      ConfigSample.PROJECT_ROOT + "/dir1/test1.txt");
    assertEquals(ConfigSample.DEST_ROOT + "/dir1-new/test1.txt", s);

    // Test include file to other file
    s = pathsManager.getRemotePath(config.getTarget1(),
      ConfigSample.PROJECT_ROOT + "/file3.txt");
    assertEquals(ConfigSample.DEST_ROOT + "/files/file3-new.txt", s);

    // Test include file to dir
    s = pathsManager.getRemotePath(config.getTarget1(),
      ConfigSample.PROJECT_ROOT + "/file2.txt");
    assertEquals(ConfigSample.DEST_ROOT + "/files/file2.txt", s);

    // Test include dir to other dir
    s = pathsManager.getRemotePath(config.getTarget1(),
      ConfigSample.PROJECT_ROOT + "/dir2/dir2-1/file88.txt");
    assertEquals(ConfigSample.DEST_ROOT + "/dir2-1-new/file88.txt", s);
  }
}
