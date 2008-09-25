package org.sylfra.idea.plugins.remotesynchronizer.ui.tables;

import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

/**
 * Contains one column for excluded copy paths
 *
 * @author <a href="mailto=sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 */
public class ExcludedCopyPathsTable extends AbstractPathTable
{
  // Columns names
  private static String[] COLUMN_NAMES = new String[]
  {
    LabelsFactory.get(LabelsFactory.COL_EXCLUDED_COPY_PATH)
  };

  public ExcludedCopyPathsTable()
  {
    super(new PrivateTableModel());
    getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
  }

  private final static class PrivateTableModel
    extends AbstractPathTable.AbstractPathTableModel
  {
    public PrivateTableModel()
    {
      super(COLUMN_NAMES);
    }

    public Object getValueAt(int row, int col)
    {
      return getValueAt(row);
    }
  }
}
