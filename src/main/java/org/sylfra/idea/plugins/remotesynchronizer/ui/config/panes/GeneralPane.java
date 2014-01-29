package org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes;

import com.intellij.openapi.project.Project;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.model.TargetMappings;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public final class GeneralPane extends JPanel implements IConfigPane
{
  private final Project project;
  private final ConfigPathsManager pathManager;
  private JCheckBox ckStoreRelativePaths;
  private JCheckBox ckSaveBeforeCopy;
  private JCheckBox ckCopyOnSave;
  private JCheckBox ckCreateMissingDirs;
  private JCheckBox ckSimulationMode;
  private JCheckBox ckAllowConcurrentRuns;

  public GeneralPane(Project project, ConfigPathsManager pathManager)
  {
    this.project = project;
    this.pathManager = pathManager;
  }

  public String getTitle()
  {
    return LabelsFactory.get(LabelsFactory.PANEL_GENERAL);
  }

  public boolean isModified(Config config)
  {
    Config.GeneralOptions generalOptions = config.getGeneralOptions();

    return !((generalOptions.isStoreRelativePaths() == ckStoreRelativePaths.isSelected())
      && (generalOptions.isSaveBeforeCopy() == ckSaveBeforeCopy.isSelected())
      && (generalOptions.isCopyOnSave() == ckCopyOnSave.isSelected())
      && (generalOptions.isCreateMissingDirs() == ckCreateMissingDirs.isSelected())
      && (generalOptions.isAllowConcurrentRuns() == ckAllowConcurrentRuns.isSelected())
      && (generalOptions.isSimulationMode() == ckSimulationMode.isSelected()));
  }

  public void reset(Config config)
  {
    // Prevent both options to be true
    if (config.getGeneralOptions().isSaveBeforeCopy())
    {
      config.getGeneralOptions().setCopyOnSave(false);
    }

    Config.GeneralOptions generalOptions = config.getGeneralOptions();

    ckStoreRelativePaths.setSelected(generalOptions.isStoreRelativePaths());
    ckSaveBeforeCopy.setSelected(generalOptions.isSaveBeforeCopy());
    ckCopyOnSave.setSelected(generalOptions.isCopyOnSave());
    ckCreateMissingDirs.setSelected(generalOptions.isCreateMissingDirs());
    ckSimulationMode.setSelected(generalOptions.isSimulationMode());
    ckAllowConcurrentRuns.setSelected(generalOptions.isAllowConcurrentRuns());

    ckCopyOnSave.setEnabled(!ckSaveBeforeCopy.isSelected());
    ckSaveBeforeCopy.setEnabled(!ckCopyOnSave.isSelected());
  }

  public void apply(Config config)
  {
    Config.GeneralOptions generalOptions = config.getGeneralOptions();

    if (generalOptions.isStoreRelativePaths() != ckStoreRelativePaths.isSelected())
    {
      for (TargetMappings targetMappings : config.getTargetMappings())
      {
        for (SynchroMapping mapping : targetMappings.getSynchroMappings())
        {
          String path = ckStoreRelativePaths.isSelected()
            ? PathsUtils.getRelativePath(project, mapping.getSrcPath())
            : pathManager.expandPath(mapping.getSrcPath(), true);
          mapping.setSrcPath(path);

          path = ckStoreRelativePaths.isSelected()
            ? PathsUtils.getRelativePath(project, mapping.getDestPath())
            : pathManager.expandPath(mapping.getDestPath(), true);
          mapping.setDestPath(path);
        }
      }
    }

    generalOptions.setStoreRelativePaths(ckStoreRelativePaths.isSelected());
    generalOptions.setSaveBeforeCopy(ckSaveBeforeCopy.isSelected());
    generalOptions.setCopyOnSave(ckCopyOnSave.isSelected());
    generalOptions.setCreateMissingDirs(ckCreateMissingDirs.isSelected());
    generalOptions.setSimulationMode(ckSimulationMode.isSelected());
    generalOptions.setAllowConcurrentRuns(ckAllowConcurrentRuns.isSelected());
  }

  public void buildUI(ConfigPathsManager pathsManager)
  {
    ckStoreRelativePaths = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_STORE_RELATIVE_PATHS));
    ckSaveBeforeCopy = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SAVE_BEFORE_COPY));
    ckCopyOnSave = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_COPY_ON_SAVE));
    ckCreateMissingDirs = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_CREATE_MISSING_DIRS));
    ckSimulationMode = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SIMULATION_MODE));
    ckAllowConcurrentRuns = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_ALLOW_CONCURRENT_RUNS));

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    // Save before copy
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 1.0;
    add(ckSimulationMode, c);

    // Store relative paths
    c.gridy++;
    add(ckStoreRelativePaths, c);

    // Save before synchronize
    c.gridy++;
    add(ckSaveBeforeCopy, c);

    // Synchronize on save
    c.gridy++;
    add(ckCopyOnSave, c);

    // Create missing dirs
    c.gridy++;
    add(ckCreateMissingDirs, c);

    // Create missing dirs
    c.gridy++;
    c.weighty = 1.0;
    add(ckAllowConcurrentRuns, c);

    handleSaveCopyOptionsConflict();
  }

  private void handleSaveCopyOptionsConflict()
  {
    ckCopyOnSave.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ckSaveBeforeCopy.setEnabled(!ckCopyOnSave.isSelected());
      }
    });

    ckSaveBeforeCopy.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ckCopyOnSave.setEnabled(!ckSaveBeforeCopy.isSelected());
      }
    });
  }
}
