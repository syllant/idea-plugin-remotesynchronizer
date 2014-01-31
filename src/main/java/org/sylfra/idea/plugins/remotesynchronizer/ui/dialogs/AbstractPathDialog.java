package org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Base dialog used to edit a file path
 */
public abstract class AbstractPathDialog extends DialogWrapper implements Disposable
{
  private static final int TEXT_SIZE = 40;

  protected ConfigPathsManager pathManager;
  protected Object value;
  protected FileChooserDescriptor fcDescriptor;

  public AbstractPathDialog(ConfigPathsManager pathManager, Object defaultValue)
  {
    super(true);
    value = defaultValue;
    this.pathManager = pathManager;

    fcDescriptor = new FileChooserDescriptor(true, true, true, true, false, false);

    init();
    updateDialogFromValue();
  }

  protected JTextField createTextField()
  {
    JTextField result = FileChooserFactory.getInstance().createFileTextField(fcDescriptor,
            false, this).getField();
    result.setColumns(TEXT_SIZE);
    result.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doOKAction();
      }
    });

    return result;
  }

  protected String formatInputPath(String path)
  {
    File f = new File(path);
    if (!f.isAbsolute())
    {
      f = new File(pathManager.getProjectDefaultRoot().getPath(), path);
    }

    String result = PathsUtils.toModelPath(f.getAbsolutePath());

    if (pathManager.getPlugin().getConfig().getGeneralOptions().isStoreRelativePaths())
    {
      result = PathsUtils.getRelativePath(pathManager.getPlugin().getProject(), result);
    }

    char c = path.charAt(path.length() - 1);
    if ((c == '/') || (c == '\\'))
    {
      result += '/';
    }

    return result;
  }

  protected JButton createBrowseButton(final JTextField textField, final boolean useAntPattern)
  {
    JButton button = new JButton("...");
    button.setPreferredSize(new Dimension(20,
            textField.getPreferredSize().height));
    button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        VirtualFile toSelect = null;
        if (textField.getText().length() > 0)
        {
          toSelect = PathsUtils.getVirtualFile(textField.getText().replace('\\', '/'));
        }

        if (toSelect == null)
        {
          toSelect = pathManager.getProjectDefaultRoot();
        }

        VirtualFile[] virtualFiles = FileChooser.chooseFiles(fcDescriptor, (Component) e.getSource(),
          pathManager.getPlugin().getProject(), toSelect);

        if (virtualFiles.length > 0)
        {
          textField.setText(getPathForTextField(virtualFiles[0], useAntPattern));
        }
      }
    });

    return button;
  }

  private String getPathForTextField(VirtualFile virtualFile, boolean useAntPattern)
  {
    String path = virtualFile.getPath();
    if (useAntPattern)
    {
      path = PathsUtils.toModelPath(path);
      if (virtualFile.isDirectory())
      {
        path += "/*";
      }

      return path;
    }

    return pathManager.toPresentablePath(path);
  }

  public Object getValue()
  {
    return value;
  }

  protected void doOKAction()
  {
    if (checkDialogValues())
    {
      updateValueFromDialog();
      super.doOKAction();
    }
  }

  protected abstract void updateDialogFromValue();

  protected abstract void updateValueFromDialog();

  protected abstract boolean checkDialogValues();

  public void dispose()
  {
    super.dispose();
  }
}
