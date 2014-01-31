package org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

/**/
public interface IConfigPane
{
  public String getTitle();
  public boolean isModified(Config config);
  public void reset(Config config);
  public void apply(Config config);
  public void buildUI(ConfigPathsManager pathsManager);
}
