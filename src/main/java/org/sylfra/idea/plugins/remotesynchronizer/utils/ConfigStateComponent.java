package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.AppTopics;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.util.messages.MessageBusConnection;
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

    addOnSaveListener();
  }

  private void addOnSaveListener()
  {
    MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
    connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC,
      new FileDocumentManagerAdapter()
      {
        @Override
        public void beforeDocumentSaving(Document document)
        {
          if (config.getGeneralOptions().isCopyOnSave())
          {
            ActionManager.getInstance().getAction("RemoteSynchronize.SynchronizeAllAction").actionPerformed(
              new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN,
                new Presentation(), ActionManager.getInstance(), 0));
          }
        }
      });
  }

  /**
   * Provided a settings bean with default values
   *
   * @return a settings bean with default values
   */
  public Config getDefaultSettings()
  {
    return new Config();
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

    // Prevent both options to be true
    if (config.getGeneralOptions().isSaveBeforeCopy())
    {
      config.getGeneralOptions().setCopyOnSave(false);
    }
  }
}
