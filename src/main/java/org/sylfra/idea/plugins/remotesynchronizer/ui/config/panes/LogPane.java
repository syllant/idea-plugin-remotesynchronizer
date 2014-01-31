package org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import java.awt.*;

/**/
public final class LogPane extends JPanel implements IConfigPane
{
  private final static String[] FONT_SIZES =
    {
      "6", "7", "8", "9", "10", "11", "12", "13", "14",
      "16", "18", "20", "24", "28", "32", "48", "64"
    };
  private final static String[] FONT_NAMES = GraphicsEnvironment
    .getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

  private JCheckBox ckClearBeforeSynchro;
  private JCheckBox ckAutoPopup;
  private JComboBox cbFontFamily;
  private JComboBox cbFontSize;
  private JCheckBox ckLogSrcPaths;
  private JCheckBox ckLogExludedPaths;
  private JCheckBox ckLogIdenticalPaths;

  public LogPane()
  {
}

  public String getTitle()
  {
    return LabelsFactory.get(LabelsFactory.PANEL_LOGS);
}

  public boolean isModified(Config config)
  {
    Config.LogOptions logOptions = config.getLogOptions();

    return !((String.valueOf(logOptions.getLogFontFamily()).equals(cbFontFamily.getSelectedItem()))
      &&
      (String.valueOf(logOptions.getLogFontSize()).equals(cbFontSize.getSelectedItem()))
      && (logOptions.isLogExludedPaths() == ckLogExludedPaths.isSelected())
      && (logOptions.isLogSrcPaths() == ckLogSrcPaths.isSelected())
      && (logOptions.isLogIdenticalPaths() == ckLogIdenticalPaths.isSelected())
      && (logOptions.isClearBeforeSynchro() == ckClearBeforeSynchro.isSelected())
      && (logOptions.isAutoPopup() == ckAutoPopup.isSelected()));
}

  public void reset(Config config)
  {
    Config.LogOptions logOptions = config.getLogOptions();

    cbFontFamily.setSelectedItem(logOptions.getLogFontFamily());
    cbFontSize.setSelectedItem(String.valueOf(logOptions.getLogFontSize()));
    ckLogExludedPaths.setSelected(logOptions.isLogExludedPaths());
    ckLogSrcPaths.setSelected(logOptions.isLogSrcPaths());
    ckLogIdenticalPaths.setSelected(logOptions.isLogIdenticalPaths());
    ckClearBeforeSynchro.setSelected(logOptions.isClearBeforeSynchro());
    ckAutoPopup.setSelected(logOptions.isAutoPopup());
}

  public void apply(Config config)
  {
    Config.LogOptions logOptions = config.getLogOptions();
    logOptions.setLogFontFamily(cbFontFamily.getSelectedItem().toString());
    logOptions.setLogFontSize(Integer.parseInt(cbFontSize.getSelectedItem().toString()));
    logOptions.setLogExludedPaths(ckLogExludedPaths.isSelected());
    logOptions.setLogSrcPaths(ckLogSrcPaths.isSelected());
    logOptions.setLogIdenticalPaths(ckLogIdenticalPaths.isSelected());
    logOptions.setClearBeforeSynchro(ckClearBeforeSynchro.isSelected());
    logOptions.setAutoPopup(ckAutoPopup.isSelected());
}

  public void buildUI(ConfigPathsManager pathsManager)
  {
    ckClearBeforeSynchro =
      new JCheckBox(LabelsFactory.get(LabelsFactory.LB_CLEAR_BEFORE_SYNCHRO));
    ckAutoPopup =
      new JCheckBox(LabelsFactory.get(LabelsFactory.LB_AUTO_POPUP_LOGS));

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    // Clear before synchro
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.NORTHWEST;
    add(ckClearBeforeSynchro, c);

    // Auto popup
    c.gridy++;
    add(ckAutoPopup, c);

    // Font panel
    c.gridy++;
    add(createFontPanel(), c);

    // Show paths panel
    c.gridy++;
    c.weighty = 1.0;
    add(createShowPathsPanel(), c);
}

  private JPanel createFontPanel()
  {
    cbFontFamily = new JComboBox(FONT_NAMES);
    cbFontSize = new JComboBox(FONT_SIZES);

    JPanel result = new JPanel(new GridBagLayout());
    result.setBorder(BorderFactory.createTitledBorder(
      LabelsFactory.get(LabelsFactory.PANEL_FONT)));
    GridBagConstraints c = new GridBagConstraints();

  // Font family
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 10);
    c.anchor = GridBagConstraints.NORTHWEST;
    result.add(new JLabel(LabelsFactory.get(LabelsFactory.LB_FONT_FAMILY)), c);

    c.gridx++;
    c.insets = new Insets(5, 0, 5, 20);
    result.add(cbFontFamily, c);

  // Font size
    c.gridx++;
    c.insets = new Insets(5, 0, 5, 10);
    result.add(new JLabel(LabelsFactory.get(LabelsFactory.LB_FONT_SIZE)), c);

    c.gridx++;
    c.insets = new Insets(5, 0, 5, 5);
    c.weightx = 1.0;
    result.add(cbFontSize, c);

    return result;
}

  private JPanel createShowPathsPanel()
  {
    ckLogSrcPaths =
      new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SHOW_SOURCE_PATHS));
    ckLogExludedPaths =
      new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SHOW_EXCLUDED_PATHS));
    ckLogIdenticalPaths =
      new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SHOW_IDENTICAL_PATHS));

    JPanel result = new JPanel(new GridBagLayout());
    result.setBorder(BorderFactory.createTitledBorder(
      LabelsFactory.get(LabelsFactory.PANEL_SHOW_PATHS)));

    GridBagConstraints c = new GridBagConstraints();

    // Source paths
    c.gridy++;
    c.anchor = GridBagConstraints.NORTHWEST;
    result.add(ckLogSrcPaths, c);

    // Excluded paths
    c.gridy++;
    result.add(ckLogExludedPaths, c);

    // Identical paths
    c.gridy++;
    c.weighty = 1.0;
    result.add(ckLogIdenticalPaths, c);

    return result;
  }
}
