package de.unituebingen.medizin.eye.serialplotter.swing.table;

import de.unituebingen.medizin.eye.serialplotter.configuration.Configuration;
import de.unituebingen.medizin.eye.serialplotter.configuration.XTypeEnum;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDatasetTableModel;

/**
 * @author strasser
 */
public final class MyXYDatasetTableModel extends XYDatasetTableModel {
	private final Configuration _config;

	public MyXYDatasetTableModel(final DefaultTableXYDataset dataset, final Configuration config) {
		super(dataset);
		_config = config;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			if (_config == null) {
				return "x";
			}
			if (_config.getTypeX() == XTypeEnum.INDEX) {
				return "index";
			} else if (_config.getTypeX() == XTypeEnum.TIMESTAMP) {
				return "t";
			} else if (_config.getTypeX() == XTypeEnum.COLUMN) {
				return _config.getTitles().get(_config.getColumnXIndex());
			} else {
				return "";
			}
		} else {
			return super.getColumnName(column);
		}
	}
}