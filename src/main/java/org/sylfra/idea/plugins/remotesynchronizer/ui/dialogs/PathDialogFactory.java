package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

/**
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public interface PathDialogFactory
{
  public AbstractPathDialog createDialog(ConfigPathsManager pathManager, Object defaultValue);
}
