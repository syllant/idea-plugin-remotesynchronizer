/**
 * 
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

public class SynchroMappingDialogFactory implements PathDialogFactory
{
  private static SynchroMappingDialogFactory instance;

  public synchronized static SynchroMappingDialogFactory getInstance()
  {
    if (instance == null)
    {
      instance = new SynchroMappingDialogFactory();
    }
    return instance;
  }

  private SynchroMappingDialogFactory()
  {
  }

  public AbstractPathDialog createDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    return new SynchroMappingDialog(pathManager, defaultValue);
  }
}

