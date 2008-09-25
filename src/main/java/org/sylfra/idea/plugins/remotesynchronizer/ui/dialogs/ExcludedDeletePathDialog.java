package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

/**
 * Intended to get an excluded delete path
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ExcludedDeletePathDialog extends ExcludedPathDialog
{
  public ExcludedDeletePathDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    super(pathManager,
      defaultValue,
      LabelsFactory.get(LabelsFactory.FRAME_EXCLUDED_DELETE_PATH),
      LabelsFactory.get(LabelsFactory.LB_EXCLUDED_DELETE_PATH));
  }
}