package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

/**/
public class ExcludedDeletePathDialogFactory implements PathDialogFactory
{
  private static ExcludedDeletePathDialogFactory instance;

  public synchronized static ExcludedDeletePathDialogFactory getInstance()
  {
    if (instance == null)
    {
      instance = new ExcludedDeletePathDialogFactory();
    }
    return instance;
  }

  private ExcludedDeletePathDialogFactory()
  {
  }

  public AbstractPathDialog createDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    return new ExcludedDeletePathDialog(pathManager, defaultValue);
  }
}
