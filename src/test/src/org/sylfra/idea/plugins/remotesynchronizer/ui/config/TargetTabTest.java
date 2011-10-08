package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import com.intellij.openapi.util.InvalidDataException;
import org.jdom.Element;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;

import javax.swing.*;

public class TargetTabTest
{
  public static void main(String[] args)
  {
    JFrame f = new JFrame("TargetTabTest");
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    RemoteSynchronizerPlugin plugin = new RemoteSynchronizerPlugin(null);
    try
    {
      plugin.readExternal(new Element("Test"));
    }
    catch (InvalidDataException e)
    {
      e.printStackTrace();
    }

    ConfigPanel configPanel = new ConfigPanel(plugin);
    configPanel.reset(plugin.getConfig());

    f.setSize(600, 400);
    f.getContentPane().add(configPanel);
    f.setVisible(true);
  }
}

