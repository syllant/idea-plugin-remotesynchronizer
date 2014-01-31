package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

/**/
public interface PathDialogFactory
{
  public AbstractPathDialog createDialog(ConfigPathsManager pathManager, Object defaultValue);
}
