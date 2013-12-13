package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.RemoteSynchronizerPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.AbstractPathDialog;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.PathDialogFactory;
import org.sylfra.idea.plugins.remotesynchronizer.ui.dialogs.SynchroMappingDialog;
import org.sylfra.idea.plugins.remotesynchronizer.ui.tables.AbstractPathTable;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ActionsHolder
{
  public final static class EditTableItemAction extends AbstractAction
    implements ListSelectionListener
  {
    private AbstractPathTable table;
    private PathDialogFactory dialogFactory;
    private ConfigPathsManager pathManager;

    public EditTableItemAction(AbstractPathTable table,
      PathDialogFactory dialogFactory,
      ConfigPathsManager pathManager)
    {
      super(LabelsFactory.get(LabelsFactory.BN_EDIT_PATH));
      this.table = table;
      this.dialogFactory = dialogFactory;
      this.pathManager = pathManager;

      table.getSelectionModel().addListSelectionListener(this);
      table.addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          if (e.getClickCount() == 2)
          {
            actionPerformed(null);
          }
        }
      });
      setEnabled(table.getSelectedRow() > -1);
    }

    public void actionPerformed(ActionEvent e)
    {
      AbstractPathDialog dialog = dialogFactory.createDialog(pathManager, table.getCurrent());
      dialog.show();
      if (dialog.getExitCode() == SynchroMappingDialog.OK_EXIT_CODE)
      {
        table.updateCurrent(dialog.getValue());
      }
    }

    public void valueChanged(ListSelectionEvent e)
    {
      setEnabled((table.getSelectedRow() > -1)
        && (table.getRowCount() > 0));
    }
  }

  /**
   * Add Button (add an included/excluded path)
   */
  public final static class AddTableItemAction extends AbstractAction
  {
    private AbstractPathTable table;
    private PathDialogFactory dialogFactory;
    private ConfigPathsManager pathManager;

    public AddTableItemAction(AbstractPathTable table,
      PathDialogFactory dialogFactory,
      ConfigPathsManager pathManager)
    {
      super(LabelsFactory.get(LabelsFactory.BN_ADD_PATH));
      this.table = table;
      this.dialogFactory = dialogFactory;
      this.pathManager = pathManager;
    }

    public void actionPerformed(ActionEvent e)
    {
      AbstractPathDialog dialog = dialogFactory.createDialog(pathManager, null);
      dialog.show();
      if (dialog.getExitCode() == SynchroMappingDialog.OK_EXIT_CODE)
      {
        table.add(dialog.getValue());
        table.getSelectionModel()
          .setSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
      }
    }
  }

  /**
   * Remove Button (remove an included/excluded path)
   */
  public final static class RemoveTableItemAction extends AbstractAction
    implements ListSelectionListener
  {
    private AbstractPathTable table;

    public RemoveTableItemAction(AbstractPathTable table)
    {
      super(LabelsFactory.get(LabelsFactory.BN_REMOVE_PATH));
      this.table = table;

      table.getSelectionModel().addListSelectionListener(this);
      setEnabled(table.getSelectedRow() > -1);
    }

    public void actionPerformed(ActionEvent e)
    {
      table.removeCurrent();
      if (table.getRowCount() > 0)
      {
        table.getSelectionModel()
          .setSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
      }
    }

    public void valueChanged(ListSelectionEvent e)
    {
      setEnabled((table.getSelectedRow() > -1)
        && (table.getRowCount() > 0));
    }
  }

  protected abstract static class TargetTabbedPaneAction extends AbstractAction
    implements ChangeListener, ContainerListener
  {
    protected TargetTabbedPane targetTabs;

    public TargetTabbedPaneAction(String name, Icon icon,
      TargetTabbedPane targetTabs)
    {
      super(name, icon);
      super.putValue(Action.LONG_DESCRIPTION, name);
      this.targetTabs = targetTabs;
      targetTabs.getModel().addChangeListener(this);
      targetTabs.addContainerListener(this);
    }

    public void componentAdded(ContainerEvent e)
    {
      checkEnabled();
    }

    public void componentRemoved(ContainerEvent e)
    {
      checkEnabled();
    }

    public void stateChanged(ChangeEvent e)
    {
      checkEnabled();
    }

    protected abstract void checkEnabled();
  }

  public static class AddTabAction extends TargetTabbedPaneAction
  {
    public AddTabAction(TargetTabbedPane targetTabs)
    {
      super(LabelsFactory.get(LabelsFactory.BN_ADD_TARGET),
        new ImageIcon(RemoteSynchronizerPlugin.class.getClassLoader()
          .getResource("general/add.png")),
        targetTabs);
    }

    public void actionPerformed(ActionEvent e)
    {
      targetTabs.addTarget();
    }

    protected void checkEnabled()
    {
    }
  }

  public final static class RemoveTabAction extends TargetTabbedPaneAction
  {
    public RemoveTabAction(TargetTabbedPane targetTabs)
    {
      super(LabelsFactory.get(LabelsFactory.BN_REMOVE_TARGET),
        new ImageIcon(RemoteSynchronizerPlugin.class.getClassLoader()
          .getResource("general/remove.png")),
        targetTabs);
    }

    public void actionPerformed(ActionEvent e)
    {
      targetTabs.removeTarget();
    }

    protected void checkEnabled()
    {
      setEnabled(targetTabs.getComponentCount() > 1);
    }
  }

  public final static class MoveTabToLeftAction extends TargetTabbedPaneAction
  {
    public MoveTabToLeftAction(TargetTabbedPane targetTabs)
    {
      super(LabelsFactory.get(LabelsFactory.BN_MOVE_TARGET_TO_LEFT),
        new ImageIcon(RemoteSynchronizerPlugin.getResource("left.png")),
        targetTabs);
    }

    public void actionPerformed(ActionEvent e)
    {
      targetTabs.moveTargetToLeft();
    }

    protected void checkEnabled()
    {
      setEnabled(targetTabs.getSelectedIndex() > 0);
    }
  }

  public final static class MoveTabToRightAction extends TargetTabbedPaneAction
  {
    public MoveTabToRightAction(TargetTabbedPane targetTabs)
    {
      super(LabelsFactory.get(LabelsFactory.BN_MOVE_TARGET_TO_RIGHT),
        new ImageIcon(RemoteSynchronizerPlugin.getResource("right.png")),
        targetTabs);
    }

    public void actionPerformed(ActionEvent e)
    {
      targetTabs.moveTargetToRight();
    }

    protected void checkEnabled()
    {
      setEnabled(targetTabs.getSelectedIndex()
        < targetTabs.getComponentCount() - 1);
    }
  }

  public final static class ExportAction extends AbstractAction
  {
    private ConfigPanel configPanel;
    private JFileChooser fileChooser;

    public ExportAction(ConfigPanel configPanel)
    {
      super(LabelsFactory.get(LabelsFactory.BN_EXPORT));
      this.configPanel = configPanel;
    }

    public void actionPerformed(ActionEvent e)
    {
      Project project = configPanel.getCurrentProject();
      if (fileChooser == null)
      {
        fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(project.getBaseDir().getPath(),
          project.getName() + "-RemoteSynchronizer.xml"));
        fileChooser.setFileFilter(new FileFilter()
        {
          public boolean accept(File file)
          {
            return (file.isDirectory()) || (file.getName().endsWith(".xml"));
          }

          public String getDescription()
          {
            return "XML files";
          }
        });
      }

      if (fileChooser.showSaveDialog((Component) e.getSource()) != JFileChooser.APPROVE_OPTION)
      {
        return;
      }

      try
      {
        File destFile = fileChooser.getSelectedFile();

        RemoteSynchronizerPlugin.getInstance(project).getConfigExternalizer().write(destFile);
      }
      catch (Exception ex)
      {
        Messages.showErrorDialog(project,
          LabelsFactory.get(LabelsFactory.MSG_EXPORT_FAILED,
            ex.toString()), RemoteSynchronizerPlugin.PLUGIN_NAME);
        Logger.getInstance("RemoteSynchronizer").info("Error while exporting settings", ex);
      }
    }
  }

  public final static class ImportAction extends AbstractAction
  {
    private ConfigPanel configPanel;
    private FileChooserDescriptor fcDescriptor;
    private VirtualFile selectedFile;

    public ImportAction(ConfigPanel configPanel)
    {
      super(LabelsFactory.get(LabelsFactory.BN_IMPORT));
      this.configPanel = configPanel;
      fcDescriptor = new FileChooserDescriptor(true, false, false, false, false, false)
      {
        public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles)
        {
          return (file.isDirectory()) || ("xml".equals(file.getExtension()));
        }
      };
      fcDescriptor.setTitle(LabelsFactory.get(LabelsFactory.LB_TITLE_IMPORT_SELECT));
      fcDescriptor.setDescription(LabelsFactory.get(LabelsFactory.LB_DESC_IMPORT_SELECT));
    }

    public void actionPerformed(ActionEvent e)
    {
      Project project = configPanel.getCurrentProject();
      if (selectedFile == null)
      {
        selectedFile = project.getBaseDir();
      }

      VirtualFile[] virtualFiles =
        FileChooser.chooseFiles(fcDescriptor, project, selectedFile);
      if (virtualFiles.length == 0)
      {
        return;
      }

      selectedFile = virtualFiles[0];

      RemoteSynchronizerPlugin plugin = RemoteSynchronizerPlugin.getInstance(project);

      try
      {
        plugin.getConfigExternalizer().read(
          new File(selectedFile.getPath()));
      }
      catch (Exception ex)
      {
        Messages.showErrorDialog(project,
          LabelsFactory.get(LabelsFactory.MSG_IMPORT_FAILED,
            ex.toString()), RemoteSynchronizerPlugin.PLUGIN_NAME);
        Logger.getInstance("RemoteSynchronizer").info("Error while importing settings", ex);
        return;
      }

      configPanel.reset(plugin.getStateComponent().getState());
    }
  }
}