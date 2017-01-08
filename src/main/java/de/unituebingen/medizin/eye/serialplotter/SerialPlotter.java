package de.unituebingen.medizin.eye.serialplotter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author strasser
 */
public class SerialPlotter {
	/**
	 * @param args the command line arguments
	 */
	public static void main(final String args[]) {
		try {
			javax.swing.UIManager.setLookAndFeel(new org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel());
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(SerialPlotter.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(() -> new SerialPlotterFrame().setVisible(true));
	}
}