package org.sylfra.idea.plugins.remotesynchronizer.ui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.ConfigListener;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ToolPanel extends JPanel
  implements ConfigListener
{
  private ThreadConsolePane consolePane;
  private JLabel emptyLabel;

  public ToolPanel(ThreadConsolePane consolePane, Config config)
  {
    super(new BorderLayout());

    this.consolePane = consolePane;
    config.addConfigListener(this);

    emptyLabel = new JLabel(LabelsFactory.get(LabelsFactory.LB_NO_ACTIVE_TARGET));
    emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

    setBorder(new EmptyBorder(2, 2, 2, 2));

    ActionGroup actionGroup = (ActionGroup) ActionManager.getInstance()
      .getAction(RemoteSynchronizerPlugin.WINDOW_ACTIONS_NAME);
    ActionToolbar toolbar = ActionManager.getInstance()
      .createActionToolbar(RemoteSynchronizerPlugin.PLUGIN_NAME, actionGroup,
        false);

    add(toolbar.getComponent(), BorderLayout.WEST);
    add(emptyLabel, BorderLayout.CENTER);

    configChanged(config);
  }

  public void configChanged(Config config)
  {
    if (config.hasActiveTarget())
    {
      if (emptyLabel.equals(getComponent(1)))
      {
        remove(1);
        add(consolePane, BorderLayout.CENTER);
        if (consolePane.getComponentCount() > 0)
          consolePane.setSelectedIndex(0);
      }
    }
    else
    {
      if (consolePane.equals(getComponent(1)))
      {
        remove(1);
        add(emptyLabel, BorderLayout.CENTER);
      }
    }

    repaint();
  }
}
