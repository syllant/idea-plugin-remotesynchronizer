package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.ExcludedCopyPathDialogFactory;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.ExcludedDeletePathDialogFactory;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.PathDialogFactory;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.SynchroMappingDialogFactory;
import org.sylfra.idea.plugins.remotesynchronizer.ui.tables.AbstractPathTable;
import org.sylfra.idea.plugins.remotesynchronizer.ui.tables.ExcludedCopyPathsTable;
import org.sylfra.idea.plugins.remotesynchronizer.ui.tables.ExcludedDeletePathsTable;
import org.sylfra.idea.plugins.remotesynchronizer.ui.tables.SynchroPathsTable;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.ActionsHolder;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.TargetTabbedPane;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TargetTab extends JPanel
{
  private int initialPos;
  private TargetMappings targetMappings;
  private JCheckBox cbActive;
  private SynchroPathsTable synchroTable;
  private ExcludedCopyPathsTable excludedCopyTable;
  private ExcludedDeletePathsTable excludedDeleteTable;

  public TargetTab(TargetMappings targetMappings,
    final TargetTabbedPane tabbedPane, ConfigPathsManager pathsManager,
    int initialPos)
  {
    this.targetMappings = targetMappings;
    this.initialPos = initialPos;

    synchroTable = new SynchroPathsTable(pathsManager);
    excludedCopyTable = new ExcludedCopyPathsTable();
    excludedDeleteTable = new ExcludedDeletePathsTable();
    cbActive = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_ACTIVE),
      targetMappings.isActive());

    cbActive.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        int index = tabbedPane.indexOfComponent(TargetTab.this);
        tabbedPane.setForegroundAt(index, tabbedPane.getForegroundAt(index));
      }
    });
    reset();
    buildUI(pathsManager);
  }

  public TargetMappings getTargetMappings()
  {
    return targetMappings;
  }

  public int getInitialPos()
  {
    return initialPos;
  }

  public void setInitialPos(int initialPos)
  {
    this.initialPos = initialPos;
  }

  public boolean isModified(Config config)
  {
    return (cbActive.isSelected() != targetMappings.isActive())
      || (!Arrays.asList(targetMappings.getSynchroMappings())
      .equals(synchroTable.getData()))
      || (!Arrays.asList(targetMappings.getExcludedCopyPaths())
      .equals(excludedCopyTable.getData()))
      || (!Arrays.asList(targetMappings.getExcludedDeletePaths())
      .equals(excludedDeleteTable.getData()));
  }

  public void reset()
  {
    cbActive.setSelected(targetMappings.isActive());
    synchroTable.setData(cloneList(targetMappings.getSynchroMappings()));
    excludedCopyTable.setData(cloneList(targetMappings.getExcludedCopyPaths()));
    excludedDeleteTable.setData(cloneList(targetMappings.getExcludedDeletePaths()));
  }

  public void apply()
  {
    targetMappings.setActive(cbActive.isSelected());
    targetMappings.setSynchroMappings(synchroTable.getData()
      .toArray(new SynchroMapping[synchroTable.getData().size()]));
    targetMappings.setExcludedCopyPaths(excludedCopyTable.getData()
      .toArray(new String[excludedCopyTable.getData().size()]));
    targetMappings.setExcludedDeletePaths(excludedDeleteTable.getData()
      .toArray(new String[excludedDeleteTable.getData().size()]));
  }

  private void buildUI(ConfigPathsManager pathsManager)
  {
    JPanel pnActive = new JPanel(new BorderLayout());
    pnActive.add(cbActive, BorderLayout.WEST);
    JLabel xInfo = new JLabel("X : "
      + LabelsFactory.get(LabelsFactory.LB_DELETE_OBSOLETE));
    xInfo.setFont(xInfo.getFont().deriveFont(Font.ITALIC));
    pnActive.add(xInfo, BorderLayout.EAST);

    JPanel pnIncluded = createTablePanel(LabelsFactory.get(LabelsFactory.PANEL_INCLUDED_PATHS),
      synchroTable, SynchroMappingDialogFactory.getInstance(), pathsManager);

    JPanel pnExcludedCopy = createTablePanel(LabelsFactory.get(LabelsFactory.PANEL_EXCLUDED_COPY_PATHS),
      excludedCopyTable, ExcludedCopyPathDialogFactory.getInstance(), pathsManager);

    JPanel pnExcludedDelete = createTablePanel(LabelsFactory.get(LabelsFactory.PANEL_EXCLUDED_DELETE_PATHS),
      excludedDeleteTable, ExcludedDeletePathDialogFactory.getInstance(), pathsManager);

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridy = 0;
    c.gridx = 0;
    c.gridwidth = 2;
    c.weighty = 0.0;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.BOTH;
    add(pnActive, c);

    c.gridy++;
    c.weighty = 1.0;
    add(pnIncluded, c);

    c.gridy++;
    c.gridwidth = 1;
    add(pnExcludedCopy, c);

    c.gridx++;
    add(pnExcludedDelete, c);
  }

  private JPanel createTablePanel(String title, AbstractPathTable table,
    PathDialogFactory dialogFactory, ConfigPathsManager pathsManager)
  {
    // Paths table
    JPanel pnTable = new JPanel(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.getViewport().setPreferredSize(
      new Dimension(
        (int) pnTable.getPreferredSize().getHeight(),
        (int) pnTable.getPreferredSize().getWidth()));
    pnTable.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JButton bnAdd = new JButton(new ActionsHolder.AddTableItemAction(table, dialogFactory, pathsManager));
    JButton bnEdit = new JButton(new ActionsHolder.EditTableItemAction(table, dialogFactory, pathsManager));
    JButton bnRemove = new JButton(new ActionsHolder.RemoveTableItemAction(table));

    int height = bnAdd.getPreferredSize().height;
    bnAdd.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
    bnEdit.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
    bnRemove.setMaximumSize(new Dimension(Short.MAX_VALUE, height));

    JPanel pnButtons = new JPanel();
    pnButtons.setLayout(new BoxLayout(pnButtons, BoxLayout.Y_AXIS));
    pnButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pnButtons.add(bnAdd);
    pnButtons.add(bnEdit);
    pnButtons.add(bnRemove);

    JPanel pnMain = new JPanel(new BorderLayout());
    pnMain.setBorder(BorderFactory.createTitledBorder(title));
    pnMain.add(pnTable, BorderLayout.CENTER);
    pnMain.add(pnButtons, BorderLayout.EAST);

    return pnMain;
  }

  public boolean isSetAsActive()
  {
    return cbActive.isSelected();
  }

  public static java.util.List<Object> cloneList(Object[] array)
  {
    if (array == null)
      return new ArrayList<Object>();

    ArrayList<Object> result = new ArrayList<Object>(array.length);
    for (Object anArray : array)
    {
      if (anArray instanceof SynchroMapping)
      {
        result.add(((SynchroMapping) anArray).clone());
      }
      else
      {
        result.add(anArray.toString());
      }
    }

    return result;
  }
}
