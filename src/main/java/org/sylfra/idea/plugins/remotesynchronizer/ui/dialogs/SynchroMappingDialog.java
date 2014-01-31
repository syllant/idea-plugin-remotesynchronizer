package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import com.intellij.openapi.ui.Messages;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Intended to get an included path
 */
public class SynchroMappingDialog extends AbstractPathDialog
{
  private JTextField tfSrcPath;
  private JTextField tfDestPath;
  private JCheckBox ckDeleteObsolete;

  public SynchroMappingDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    super(pathManager, defaultValue);
  }

  protected void init()
  {
    setTitle(LabelsFactory.get(LabelsFactory.FRAME_INCLUDED_PATH));
    tfSrcPath = createTextField();
    tfDestPath = createTextField();
    ckDeleteObsolete = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_DELETE_OBSOLETE));

    super.init();
  }

  protected void updateDialogFromValue()
  {
    if (value == null)
    {
      tfSrcPath.setText("");
      tfDestPath.setText("");
      ckDeleteObsolete.setSelected(true);
    }
    else
    {
      SynchroMapping p = (SynchroMapping) value;

      tfSrcPath.setText(pathManager.expandPath(pathManager.toPresentablePath(p.getSrcPath()), false));
      tfDestPath.setText(pathManager.expandPath(pathManager.toPresentablePath(p.getDestPath()), false));
      ckDeleteObsolete.setSelected(p.isDeleteObsoleteFiles());
    }
  }

  protected void updateValueFromDialog()
  {
    if (value == null)
      value = new SynchroMapping();

    SynchroMapping p = (SynchroMapping) value;

    p.setSrcPath(formatInputPath((tfSrcPath.getText())));
    p.setDestPath(formatInputPath(tfDestPath.getText()));
    p.setDeleteObsoleteFiles(ckDeleteObsolete.isSelected());
  }

  protected boolean checkDialogValues()
  {
    if ("".equals(tfSrcPath.getText()))
    {
      Messages.showMessageDialog(LabelsFactory.get(LabelsFactory.MSG_INVALID_SRC_PATH),
        RemoteSynchronizerPlugin.PLUGIN_NAME, Messages.getErrorIcon());

      return false;
    }

    if ("".equals(tfDestPath.getText()))
    {
      Messages.showMessageDialog(LabelsFactory.get(LabelsFactory.MSG_INVALID_DEST_PATH),
        RemoteSynchronizerPlugin.PLUGIN_NAME, Messages.getErrorIcon());

      return false;
    }

    if (PathsUtils.isAncestor(tfDestPath.getText(), tfSrcPath.getText()))
    {
      Messages.showMessageDialog(LabelsFactory.get(LabelsFactory.MSG_SRC_IN_DEST_PATH),
        RemoteSynchronizerPlugin.PLUGIN_NAME, Messages.getErrorIcon());

      return false;
    }

    return true;
  }

  protected JComponent createCenterPanel()
  {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.SOUTHWEST;
    c.insets = new Insets(5, 5, 0, 0);
    JLabel label = new JLabel(LabelsFactory.get(LabelsFactory.LB_SRC_PATH));
    panel.add(label, c);

    c.gridy++;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 5, 0, 5);
    panel.add(tfSrcPath, c);

    c.gridx++;
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    JButton bnBrowse = createBrowseButton(tfSrcPath, false);
    panel.add(bnBrowse, c);

    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(8, 5, 0, 0);
    label = new JLabel(LabelsFactory.get(LabelsFactory.LB_DEST_PATH));
    panel.add(label, c);

    c.gridy++;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 5, 0, 5);
    panel.add(tfDestPath, c);

    c.gridx++;
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    bnBrowse = createBrowseButton(tfDestPath, false);
    panel.add(bnBrowse, c);

    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(8, 5, 0, 0);
    panel.add(ckDeleteObsolete, c);

    return panel;
  }

  /**
   * @return component which should be focused when the the dialog appears on the screen.
   */
  public JComponent getPreferredFocusedComponent()
  {
    return tfSrcPath;
  }

  public boolean isOK()
  {
    return tfSrcPath.getText().length() > 2;
  }

  public String getSrcPath()
  {
    return tfSrcPath.getText();
  }

  public String getDestPath()
  {
    return tfDestPath.getText();
  }
}