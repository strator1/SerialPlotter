package de.unituebingen.medizin.eye.serialplotter.configuration;

import java.time.Instant;

/**
 * @author strasser
 */
public enum ColumnDatatypeEnum {
	INTEGER(java.lang.Integer.class),
	DECIMAL(java.lang.Double.class),
	TEXT(java.lang.String.class),
	TIMESTAMP(java.time.Instant.class);

	private final Class<?> _clazz;

	private ColumnDatatypeEnum(final Class<?> clazz) {
		_clazz = clazz;
	}

	@Override
	public String toString() {
		if (Integer.class.equals(_clazz)) {
			return "Integer";
		} else if (Double.class.equals(_clazz)) {
			return "Decimal";
		} else if (String.class.equals(_clazz)) {
			return "Text";
		} else if (Instant.class.equals(_clazz)) {
			return "Timestamp";
		} else {
			throw new IllegalArgumentException("Unknown type");
		}
	}
}