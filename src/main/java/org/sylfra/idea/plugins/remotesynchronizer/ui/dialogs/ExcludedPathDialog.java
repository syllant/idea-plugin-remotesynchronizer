package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import com.intellij.openapi.ui.Messages;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Intended to get an excluded path
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public abstract class ExcludedPathDialog extends AbstractPathDialog
{
  private JTextField tfPath;
  private String label;

  public ExcludedPathDialog(ConfigPathsManager pathManager, Object defaultValue,
    String title, String label)
  {
    super(pathManager, defaultValue);

    setTitle(title);
    this.label = label;
  }

  protected void init()
  {
    tfPath = createTextField();
    super.init();
  }

  public void updateDialogFromValue()
  {
    tfPath.setText((value == null)
      ? ""
      : value.toString());
  }

  protected void updateValueFromDialog()
  {
    value = tfPath.getText().toString();
  }

  protected boolean checkDialogValues()
  {
    if ("".equals(tfPath.getText()))
    {
      Messages.showMessageDialog(LabelsFactory.get(LabelsFactory.MSG_INVALID_PATH),
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
    c.anchor = c.SOUTHWEST;
    c.insets = new Insets(5, 5, 0, 0);
    JLabel lb = new JLabel(label);
    panel.add(lb, c);

    c.gridy++;
    c.weightx = 1.0;
    c.fill = c.HORIZONTAL;
    c.insets = new Insets(0, 5, 0, 5);
    panel.add(tfPath, c);

    c.gridx++;
    c.weightx = 0.0;
    c.fill = c.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    JButton bnBrowse = createBrowseButton(tfPath, true);
    panel.add(bnBrowse, c);

    return panel;
  }

  public JComponent getPreferredFocusedComponent()
  {
    return tfPath;
  }

  public String getPath()
  {
    return tfPath.getText();
  }
}