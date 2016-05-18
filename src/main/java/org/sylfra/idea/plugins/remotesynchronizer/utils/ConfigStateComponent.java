package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;

/**
 *
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
  private final Project project;

  public ConfigStateComponent(Project project)
  {
    this.project = project;
    config = getDefaultSettings();

    addOnSaveListener();
  }

  private void addOnSaveListener()
  {
    // Applies to all opened projects, which means that the same file could be synchronized multiple times across
    // different projects, which is fine
    MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
    connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC,
      new FileDocumentManagerAdapter()
      {
        @Override
        public void beforeDocumentSaving(@NotNull Document document)
        {
          // Intercepting beforeDocumentSaving event is not accurate, should rely on an "after" event to be sure the
          // file is saved before the sync. This should work most of times since the sync is launched as a separate
          // thread, but should be improved
          if (config.getGeneralOptions().isCopyOnSave())
          {
            VirtualFile vFile = FileDocumentManager.getInstance().getFile(document);
            if ((vFile != null) && ProjectFileIndex.SERVICE.getInstance(project).isInContent(vFile))
            {
              RemoteSynchronizerPlugin.getInstance(project).launchSyncIfAllowed(new VirtualFile[]{vFile});
            }
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
