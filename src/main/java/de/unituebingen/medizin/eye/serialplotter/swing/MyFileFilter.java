package de.unituebingen.medizin.eye.serialplotter.swing;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileFilter;

/**
 * @author strasser
 */
public final class MyFileFilter extends FileFilter {
	private final String _title;
	private final String[] _extensions;

	public MyFileFilter(final String title, final String ... extensions) {
		_title = title;
		_extensions = extensions;
	}

	@Override
	public boolean accept(final File f) {
		return f.isDirectory() || Arrays
									.stream(_extensions)
									.map(e -> String.format(".%s", e))
									.anyMatch(e -> f.getName().endsWith(e));
	}

	@Override
	public String getDescription() {
		return Arrays
			.stream(_extensions)
			.map(e -> String.format("*.%s", e))
			.collect(Collectors.joining(", ", String.format("%s (", _title), ")"));
	}
}