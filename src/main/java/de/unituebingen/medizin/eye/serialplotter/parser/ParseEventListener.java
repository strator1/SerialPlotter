package de.unituebingen.medizin.eye.serialplotter.parser;

import java.util.EventListener;

/**
 * @author strasser
 */
public interface ParseEventListener extends EventListener {
	/**
	 * @param e
	 */
	public void parseEvent(final ParseEvent e);
}