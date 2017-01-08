package de.unituebingen.medizin.eye.serialplotter.parser;

import java.time.Instant;

/**
 * @author strasser
 */
public final class Record {
	private final Object[] _columns;
	private final String _raw;
	private final int _index;
	private final Instant _timestamp;
	
	Record(final int colCount, final String raw, final int index, final Instant timestamp) {
		_columns = new Object[colCount];
		_raw = raw;
		_index = index;
		_timestamp = timestamp;
	}

	public String getRaw() {
		return _raw;
	}

	public void setValue(final int column, final Object value) {
		_columns[column] = value;
	}

	public Object getValue(final int column) {
		return _columns[column];
	}

	public Number getNumber(final int column) {
		return (Number) getValue(column);
	}

	public Instant getTimestamp(final int column) {
		return (Instant) getValue(column);
	}

	public Instant getTimestamp() {
		return _timestamp;
	}

	public int getIndex() {
		return _index;
	}
}