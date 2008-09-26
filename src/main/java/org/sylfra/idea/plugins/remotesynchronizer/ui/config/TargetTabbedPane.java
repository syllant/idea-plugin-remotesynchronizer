package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public final class TargetTabbedPane extends JTabbedPane
{
  private ConfigPathsManager pathsManager;
  private NameBox nameBox;

  public TargetTabbedPane(ConfigPathsManager pathsManager)
  {
    this.pathsManager = pathsManager;

    nameBox = new NameBox();
    addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          int index = indexAtLocation(e.getX(), e.getY());
          if (index > -1)
          {
            nameBox.startEdition(index);
          }
        }
      }
    });

  }

  public boolean isModified(Config config)
  {
    // Existings tabs
    for (int i = 0; i < getComponentCount(); i++)
    {
      TargetTab tab = (TargetTab) getComponentAt(i);
      if ((!config.getTargetMappings().contains(tab.getTargetMappings()))
        || (!getTitleAt(i).equals(tab.getTargetMappings().getName()))
        || (i != tab.getInitialPos())
        || (tab.isModified(config)))
      {
        return true;
      }
    }

    // Deleted tabs
    for (TargetMappings targetMappings : config.getTargetMappings())
    {
      if (!containsTab(targetMappings))
      {
        return true;
      }
    }

    return false;
  }

  public void reset(Config config)
  {
    removeAll();

    for (TargetMappings targetMappings : config.getTargetMappings())
    {
      add(targetMappings.getName(),
        new TargetTab(targetMappings, this, pathsManager, getComponentCount()));
    }
  }

  public void apply(Config config)
  {
    config.getTargetMappings().clear();
    for (int i = 0; i < getComponentCount(); i++)
    {
      TargetTab tab = (TargetTab) getComponentAt(i);
      tab.apply();
      tab.setInitialPos(i);
      tab.getTargetMappings().setName(getTitleAt(i));
      config.getTargetMappings().add(tab.getTargetMappings());
    }
  }

  private boolean containsTab(TargetMappings target)
  {
    for (int i = 0; i < getComponentCount(); i++)
    {
      if (((TargetTab) getComponentAt(i)).getTargetMappings().equals(target))
      {
        return true;
      }
    }

    return false;
  }

  private String findNewName()
  {
    boolean found = false;
    String result = LabelsFactory.get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME);
    for (int i = 0; i < getComponentCount(); i++)
    {
      TargetTab tab = (TargetTab) getComponentAt(i);
      if (tab.getTargetMappings().getName().equals(result))
      {
        found = true;
        break;
      }
    }

    if (!found)
    {
      return result;
    }

    int inc = 1;
    result = LabelsFactory.get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME)
      + " (" + inc + ")";
    for (int i = 0; i < getComponentCount(); i++)
    {
      TargetTab tab = (TargetTab) getComponentAt(i);
      if (tab.getTargetMappings().getName().equals(result))
      {
        i = 0;
        result = LabelsFactory.get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME)
          + " (" + ++inc + ")";
      }
    }

    return result;
  }

  public void addTarget()
  {
    TargetMappings target = new TargetMappings();
    target.setName(findNewName());
    TargetTab tab = new TargetTab(target, this, pathsManager,
      getComponentCount());
    add(target.getName(), tab);

    int index = getComponentCount() - 1;

    setSelectedComponent(tab);
    nameBox.startEdition(index);
  }

  public Color getForegroundAt(int index)
  {
    TargetTab tab = (TargetTab) getComponentAt(index);
    return tab.isSetAsActive() ? super.getForeground() : Color.gray;
  }

  public void removeTarget()
  {
    remove(getSelectedComponent());
  }

  public void moveTargetToLeft()
  {
    moveTo(getSelectedIndex() - 1);
  }

  public void moveTargetToRight()
  {
    moveTo(getSelectedIndex() + 1);
  }

  private void moveTo(int dest)
  {
    TargetTab tab = (TargetTab) getSelectedComponent();
    // Title may be different from target's name
    String title = getTitleAt(getSelectedIndex());

    remove(tab);
    insertTab(title, null, tab, null, dest);
    setSelectedIndex(dest);
  }

  // Hum, textField is not editable on a popup menu...
  private class NameBox extends JDialog
    {
    private JTextField tfName;
    private int tabIndex;

    public NameBox()
    {
      setModal(true);
      setOpaque(true);
      setUndecorated(true);
      setResizable(false);

      tfName = new JTextField();
      tfName.setHorizontalAlignment(JTextField.CENTER);
      tfName.setFont(tfName.getFont().deriveFont(Font.BOLD));

      JPanel panel = new JPanel();
      panel.add(tfName);
      setContentPane(panel);

      tfName.addKeyListener(new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
          {
            endEdition(false);
          }
        }
      });
      tfName.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          endEdition(true);
        }
      });
      tfName.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          endEdition(true);
        }
      });
    }

    public Dimension getPreferredSize()
    {
      return new Dimension(getBoundsAt(tabIndex).width + 2,
        getBoundsAt(tabIndex).height + 2);
    }

    public void startEdition(int tabIndex)
    {
      this.tabIndex = tabIndex;
      TargetTabbedPane tabbedPane = TargetTabbedPane.this;

      tfName.setText(tabbedPane.getTitleAt(tabIndex));

      Point onScreen = tabbedPane.getLocationOnScreen().getLocation();
      setLocation((int) onScreen.getX() + tabbedPane.getBoundsAt(tabIndex).x,
        (int) onScreen.getY() + tabbedPane.getBoundsAt(tabIndex).y);
      pack();
      setVisible(true);

      tfName.requestFocus();
      tfName.selectAll();
    }

    private void endEdition(boolean valid)
    {
      if (!isVisible())
      {
        return;
      }

      if (valid)
      {
        setTitleAt(tabIndex, tfName.getText());
      }
      dispose();
    }
  }
}
