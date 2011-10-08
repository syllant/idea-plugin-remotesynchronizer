package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import com.intellij.openapi.util.InvalidDataException;
import org.jdom.Element;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;

import javax.swing.*;

public class ConfigPanelTest
{
  public static void main(String[] args)
  {
    JFrame f = new JFrame("ConfigPanelTest");
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

    f.setSize(600, 400);
    f.getContentPane().add(new ConfigPanel(plugin));
    f.setVisible(true);
  }
}

