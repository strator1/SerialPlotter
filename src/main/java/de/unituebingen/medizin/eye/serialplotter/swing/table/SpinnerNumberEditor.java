package de.unituebingen.medizin.eye.serialplotter.swing.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

/**
 * @author strasser
 */
public class SpinnerNumberEditor extends AbstractCellEditor implements TableCellEditor {
	private final JSpinner _spinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

	@Override
	public Object getCellEditorValue() {
		return _spinner.getValue();
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		_spinner.setValue(value);
		return _spinner;
	}

	@Override
	public boolean isCellEditable(final EventObject e) {
		return (e instanceof MouseEvent) ? ((MouseEvent) e).getClickCount() >= 2 : true;
	}
}
