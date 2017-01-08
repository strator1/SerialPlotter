package de.unituebingen.medizin.eye.serialplotter.configuration;

/**
 * @author strasser
 */
public enum ColumnUsageEnum {
	X_AXIS("X-Axis"),
	Y_AXIS("Y-Axis"),
	STORE("Store"),
	SKIP("Skip");

	private final String _text;

	private ColumnUsageEnum(final String text) {
		_text = text;
	}

	@Override
	public String toString() {
		return _text;
	}
}