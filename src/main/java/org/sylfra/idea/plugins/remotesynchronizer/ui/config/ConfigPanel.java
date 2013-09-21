package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.GeneralPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.IConfigPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.LogPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.TargetsPane;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

import javax.swing.*;
import java.awt.*;

/**
 * Configuration panel
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ConfigPanel extends JPanel
{
  private Project currentProject;
  private IConfigPane[] configPanes;

  public ConfigPanel(RemoteSynchronizerPlugin plugin)
  {
    configPanes = new IConfigPane[]{
      new TargetsPane(plugin.getPathManager()),
      new GeneralPane(plugin.getProject(), plugin.getPathManager()), new LogPane()
    };

    buildUI(plugin.getPathManager());
    currentProject = plugin.getProject();
  }

  public Project getCurrentProject()
  {
    return currentProject;
  }

  public boolean isModified(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      if (configPane.isModified(config))
      {
        return true;
      }
    }

    return false;
  }

  public void reset(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      configPane.reset(config);
    }
  }

  public void apply(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      configPane.apply(config);
    }
  }

  private void buildUI(ConfigPathsManager pathsManager)
  {
    JPanel pnHeader = createHeaderPanel();

    JTabbedPane mainPane = new JTabbedPane();

    for (IConfigPane configPane : configPanes)
    {
      configPane.buildUI(pathsManager);
      mainPane.addTab(configPane.getTitle(), (Component) configPane);
    }

    setLayout(new BorderLayout());
    add(pnHeader, BorderLayout.NORTH);
    add(mainPane, BorderLayout.CENTER);
  }

  private JPanel createHeaderPanel()
  {
    JPanel pnImportExport = new JPanel();

    pnImportExport.add(new JButton(new ActionsHolder.ImportAction(this)));
    pnImportExport.add(new JButton(new ActionsHolder.ExportAction(this)));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(pnImportExport, BorderLayout.CENTER);
    panel.add(new JLabel("version " +
      PluginManager.getPlugin(PluginId.getId(RemoteSynchronizerPlugin.PLUGIN_NAME)).getVersion()),
      BorderLayout.EAST);
    return panel;
  }
}
