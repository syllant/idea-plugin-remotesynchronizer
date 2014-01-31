package org.sylfra.idea.plugins.remotesynchronizer.ui.tables;

import org.sylfra.idea.plugins.remotesynchronizer.model.SynchroMapping;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.LabelsFactory;

/**
 * Contains 2 columns for included paths : source path and destination path
 */
public class SynchroPathsTable extends AbstractPathTable
{
  // Columns indexes
  public final static int NUM_COL_SRC_PATH = 0;
  public final static int NUM_COL_DEST_PATH = 1;
  public final static int NUM_COL_DELETE_OBSOLETE = 2;

  // Columns names
  private static String[] COLUMN_NAMES = new String[]
  {
    LabelsFactory.get(LabelsFactory.COL_SRC_PATH),
    LabelsFactory.get(LabelsFactory.COL_DEST_PATH),
    "X"
  };

  public SynchroPathsTable(ConfigPathsManager pathManager)
  {
    super(new PrivatePathTableModel());
    getColumnModel().getColumn(NUM_COL_SRC_PATH).setCellRenderer(new RelativePathCellRenderer(pathManager));
    getColumnModel().getColumn(NUM_COL_DEST_PATH).setCellRenderer(new PathCellRenderer(pathManager));
    getColumnModel().getColumn(NUM_COL_DELETE_OBSOLETE).setMaxWidth(20);
  }

  private final static class PrivatePathTableModel
    extends AbstractPathTable.AbstractPathTableModel
  {
    public PrivatePathTableModel()
    {
      super(COLUMN_NAMES);
    }

    public Class getColumnClass(int columnIndex)
    {
      return (columnIndex == NUM_COL_DELETE_OBSOLETE)
        ? Boolean.class
        : String.class;
    }

    public Object getValueAt(int row, int col)
    {
      SynchroMapping p = (SynchroMapping) getValueAt(row);

      if (p == null)
        return null;

      switch (col)
      {
        case NUM_COL_SRC_PATH:
          return p.getSrcPath();
        case NUM_COL_DEST_PATH:
          return p.getDestPath();
        case NUM_COL_DELETE_OBSOLETE:
          return p.isDeleteObsoleteFiles();
      }

      return null;
    }
  }
}
