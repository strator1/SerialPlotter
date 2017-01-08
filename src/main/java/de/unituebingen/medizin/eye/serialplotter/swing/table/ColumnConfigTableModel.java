package de.unituebingen.medizin.eye.serialplotter.swing.table;

import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnDatatypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnUsageEnum;
import javax.swing.table.DefaultTableModel;

/**
 * @author strasser
 */
public class ColumnConfigTableModel extends DefaultTableModel {
	private final static Class[] TYPES = new Class [] {
        java.lang.Integer.class,
		java.lang.String.class,
		ColumnDatatypeEnum.class,
		ColumnUsageEnum.class,
		java.lang.Integer.class
    };

	public ColumnConfigTableModel() {
		super(
			new Object [][] {
				{null, "index", ColumnDatatypeEnum.INTEGER, ColumnUsageEnum.X_AXIS, "All"},
				{null, "t", ColumnDatatypeEnum.TIMESTAMP, ColumnUsageEnum.SKIP, "All"}
			},
			new String [] {
				"Col #", "Title", "Type", "Usage", "Plot #"
			}
		);
	}

	@Override
    public Class getColumnClass(final int columnIndex) {
        return TYPES[columnIndex];
    }

	@Override
	public boolean isCellEditable(final int row, final int column) {
		if (row >= 2) {
			return true;
		} else {
			return column == 3;
		}
	}
}