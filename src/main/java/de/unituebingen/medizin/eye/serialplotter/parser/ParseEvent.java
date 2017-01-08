package de.unituebingen.medizin.eye.serialplotter.parser;

import java.util.EventObject;

/**
 * @author strasser
 */
public final class ParseEvent extends EventObject {
	private final Record _record;

	ParseEvent(final Parser source, final Record record) {
		super(source);
		_record = record;
	}

	public Record getRecord() {
		return _record;
	}
}