package org.sylfra.idea.plugins.remotesynchronizer.ui.tables;

import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;
import org.sylfra.idea.plugins.remotesynchronizer.utils.PathsUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Base table used to view paths
 */
public class AbstractPathTable extends JTable
{
  public AbstractPathTable(AbstractPathTableModel model)
  {
    super(model);

    getTableHeader().setReorderingAllowed(false);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  public List<Object> getData()
  {
    return getPathModel().getData();
  }

  public void setData(List<Object> data)
  {
    getPathModel().setData(data);
  }

  public Object getCurrent()
  {
    return getPathModel().getValueAt(getSelectedRow());
  }

  public void add(Object o)
  {
    getPathModel().add(o);
  }

  public void updateCurrent(Object o)
  {
    getPathModel().update(getSelectedRow(), o);
  }

  public void removeCurrent()
  {
    int row = getSelectedRow();
    getPathModel().remove(row);
    getSelectionModel().setSelectionInterval(row, row);
  }

  private AbstractPathTableModel getPathModel()
  {
    return (AbstractPathTableModel) getModel();
  }

  /**
   * Implementation based on a List
   */
  protected abstract static class AbstractPathTableModel extends AbstractTableModel
  {
    protected String[] colNames;
    protected List<Object> data;

    protected AbstractPathTableModel(String[] colNames)
    {
      this.colNames = colNames;
    }

    protected List<Object> getData()
    {
      return data;
    }

    public void setData(List<Object> data)
    {
      this.data = data;
      fireTableDataChanged();
    }

    public int getRowCount()
    {
      return (data == null) ? 0 : data.size();
    }

    public int getColumnCount()
    {
      return colNames.length;
    }

    public String getColumnName(int i)
    {
      return colNames[i];
    }

    public Object getValueAt(int row)
    {
      return ((row > -1) && (row < data.size())) ? data.get(row) : null;
    }

    public void remove(int row)
    {
      if ((row > -1) && (row < data.size()))
      {
        data.remove(row);
        fireTableRowsDeleted(row, row);
      }
    }

    public void add(Object o)
    {
      data.add(o);
      fireTableRowsInserted(data.size(), data.size());
    }

    public void update(int row, Object o)
    {
      if ((row > -1) && (row < data.size()))
      {
        data.set(row, o);
        fireTableRowsUpdated(row, row);
      }
    }
  }

  /**
   * Add a tooltip and set a presentable path
   */
  protected static class ToolTipCellRenderer
    extends DefaultTableCellRenderer
  {
    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
    {
      DefaultTableCellRenderer result = (DefaultTableCellRenderer)
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      setToolTipText(result.getText());

      return result;
    }
  }

  /**
   * Add a tooltip and set a presentable path
   */
  protected static class PathCellRenderer extends DefaultTableCellRenderer
  {
    private ConfigPathsManager pathManager;

    public PathCellRenderer(ConfigPathsManager pathManager)
    {
      this.pathManager = pathManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
    {
      DefaultTableCellRenderer result = (DefaultTableCellRenderer)
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      String path = PathsUtils.toModelPath(result.getText());
      if (pathManager.getPlugin().getConfig().getGeneralOptions().isStoreRelativePaths())
      {
        path = PathsUtils.getRelativePath(pathManager.getPlugin().getProject(), path);
      }
      else
      {
        path = pathManager.expandPath(path, false);
      }
      path = pathManager.toPresentablePath(path);

      result.setText(path);
      setToolTipText(pathManager.expandPath(result.getText(), false));

      return result;
    }
  }

  /**
   * Add a tooltip and handle non-relative paths
   */
  protected final static class RelativePathCellRenderer
    extends DefaultTableCellRenderer
  {
    private ConfigPathsManager pathManager;

    public RelativePathCellRenderer(ConfigPathsManager pathManager)
    {
      this.pathManager = pathManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
    {
      DefaultTableCellRenderer result = (DefaultTableCellRenderer)
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      String path = result.getText();
      boolean inProject = (pathManager.isRelativePath(path)) || (pathManager.isOutputPath(path));

      String presentablePath;
      if (pathManager.getPlugin().getConfig().getGeneralOptions().isStoreRelativePaths())
      {
        presentablePath = PathsUtils.getRelativePath(pathManager.getPlugin().getProject(), path);
      }
      else
      {
        presentablePath = pathManager.expandPath(path, false);
      }
      presentablePath = pathManager.toPresentablePath(presentablePath);

      result.setText(presentablePath);

      if (inProject)
      {
        result.setForeground(Color.black);
      }
      else
      {
        result.setForeground(Color.red);
        result.setText(result.getText()
          + " (" + LabelsFactory.get(LabelsFactory.MSG_PATH_NOT_IN_PROJECT)
          + ")");
      }

      setToolTipText(pathManager.expandPath(presentablePath, false));

      return result;
    }
  }
}
