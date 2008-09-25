package org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.ActionsHolder;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.TargetTabbedPane;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import java.awt.*;

public class TargetsPane extends JPanel implements IConfigPane
{
  private TargetTabbedPane tabbedPane;

  public TargetsPane(ConfigPathsManager pathsManager)
  {
    tabbedPane = new TargetTabbedPane(pathsManager);
}

  public String getTitle()
  {
    return LabelsFactory.get(LabelsFactory.PANEL_TARGETS);
}

  public boolean isModified(Config config)
  {
    return tabbedPane.isModified(config);
}

  public void reset(Config config)
  {
    tabbedPane.reset(config);
}

  public void apply(Config config)
  {
    tabbedPane.apply(config);
}

  public void buildUI(ConfigPathsManager pathsManager)
  {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

  // Toolbar
    JToolBar toolBar = new JToolBar();
    toolBar.setBorderPainted(false);
    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    toolBar.add(new ActionsHolder.AddTabAction(tabbedPane));
    toolBar.add(new ActionsHolder.RemoveTabAction(tabbedPane));
    toolBar.add(new JSeparator());
    toolBar.add(new ActionsHolder.MoveTabToLeftAction(tabbedPane));
    toolBar.add(new ActionsHolder.MoveTabToRightAction(tabbedPane));

    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(0, 5, 2, 10);
    c.anchor = GridBagConstraints.NORTHWEST;
    add(toolBar, c);

  // Tabs
    c.gridy++;
    c.insets = new Insets(0, 5, 3, 10);
    c.weighty = 1.0;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.BOTH;
    add(tabbedPane, c);
}
}