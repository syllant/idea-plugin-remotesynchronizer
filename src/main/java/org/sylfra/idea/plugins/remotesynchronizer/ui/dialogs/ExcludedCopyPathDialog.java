package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

/**
 * Intended to get an excluded copy path
 */
public class ExcludedCopyPathDialog extends ExcludedPathDialog
{
  public ExcludedCopyPathDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    super(pathManager,
      defaultValue,
      LabelsFactory.get(LabelsFactory.FRAME_EXCLUDED_COPY_PATH),
      LabelsFactory.get(LabelsFactory.LB_EXCLUDED_COPY_PATH));
  }
}