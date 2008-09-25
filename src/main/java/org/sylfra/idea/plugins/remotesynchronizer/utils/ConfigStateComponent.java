package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;

/**
 * @author <a href="mailto:sylvain.francois@kalistick.fr">Sylvain FRANCOIS</a>
 * @version $Id$
 */
@State(
  name = "RemoteSynchronizer",
  storages = {
  @Storage(
    id = "RemoteSynchronizer",
    file = "$WORKSPACE_FILE$"
  )}
)
public class ConfigStateComponent implements PersistentStateComponent<Config>
{
  private Config config;

  public ConfigStateComponent()
  {
    config = getDefaultSettings();
  }

  /**
   * Provided a settings bean with default values
   *
   * @return a settings bean with default values
   */
  public Config getDefaultSettings()
  {
    Config config = new Config();

    return config;
  }

  /**
   * {@inheritDoc}
   */
  public Config getState()
  {
    return config;
  }

  /**
   * {@inheritDoc}
   */
  public void loadState(Config object)
  {
    config = object;
  }
}
