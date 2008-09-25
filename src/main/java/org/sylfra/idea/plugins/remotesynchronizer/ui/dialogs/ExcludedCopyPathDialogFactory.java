package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

/**
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ExcludedCopyPathDialogFactory implements PathDialogFactory
{
  private static ExcludedCopyPathDialogFactory instance;

  public synchronized static ExcludedCopyPathDialogFactory getInstance()
  {
    if (instance == null)
    {
      instance = new ExcludedCopyPathDialogFactory();
    }
    return instance;
  }

  private ExcludedCopyPathDialogFactory()
  {
  }

  public AbstractPathDialog createDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    return new ExcludedCopyPathDialog(pathManager, defaultValue);
  }
}
