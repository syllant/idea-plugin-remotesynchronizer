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

/**
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public final class GeneralPane extends JPanel implements IConfigPane
{
  private final Project project;
  private final ConfigPathsManager pathManager;
  private JCheckBox ckStoreRelativePaths;
  private JCheckBox ckSaveBeforeCopy;
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
      && (generalOptions.isCreateMissingDirs() == ckCreateMissingDirs.isSelected())
      && (generalOptions.isAllowConcurrentRuns() == ckAllowConcurrentRuns.isSelected())
      && (generalOptions.isSimulationMode() == ckSimulationMode.isSelected()));
  }

  public void reset(Config config)
  {
    Config.GeneralOptions generalOptions = config.getGeneralOptions();

    ckStoreRelativePaths.setSelected(generalOptions.isStoreRelativePaths());
    ckSaveBeforeCopy.setSelected(generalOptions.isSaveBeforeCopy());
    ckCreateMissingDirs.setSelected(generalOptions.isCreateMissingDirs());
    ckSimulationMode.setSelected(generalOptions.isSimulationMode());
    ckAllowConcurrentRuns.setSelected(generalOptions.isAllowConcurrentRuns());
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
    generalOptions.setCreateMissingDirs(ckCreateMissingDirs.isSelected());
    generalOptions.setSimulationMode(ckSimulationMode.isSelected());
    generalOptions.setAllowConcurrentRuns(ckAllowConcurrentRuns.isSelected());
  }

  public void buildUI(ConfigPathsManager pathsManager)
  {
    ckStoreRelativePaths = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_STORE_RELATIVE_PATHS));
    ckSaveBeforeCopy = new JCheckBox(LabelsFactory.get(LabelsFactory.LB_SAVE_BEFORE_COPY));
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

    // Create missing dirs
    c.gridy++;
    add(ckSaveBeforeCopy, c);

    // Create missing dirs
    c.gridy++;
    add(ckCreateMissingDirs, c);

    // Create missing dirs
    c.gridy++;
    c.weighty = 1.0;
    add(ckAllowConcurrentRuns, c);
  }
}
