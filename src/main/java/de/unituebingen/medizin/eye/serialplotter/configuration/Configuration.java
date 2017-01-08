package de.unituebingen.medizin.eye.serialplotter.configuration;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author strasser
 */
public final class Configuration {
	private final List<Character> _fieldSeparators = new ArrayList<>(1);
	private final List<String> _lineEndings = new ArrayList<>(1);
	private final List<String> _titles = new ArrayList<>();
	private final List<ColumnDatatypeEnum> _types = new ArrayList<>();
	private final List<Integer> _plots = new ArrayList<>(1);
	private final List<ColumnUsageEnum> _usages = new ArrayList<>(3);

	private boolean _quoteFields;
	private char _decimalSeparator;
	private XTypeEnum _typeX;
	private int _columnXIndex;

	public Configuration() {
	}

	public XTypeEnum getTypeX() {
		return _typeX;
	}

	public void setTypeX(final XTypeEnum typeX) {
		_typeX = typeX;
	}

	public int getColumnXIndex() {
		return _columnXIndex;
	}

	public void setColumnXIndex(final int columnXIndex) {
		_columnXIndex = columnXIndex;
	}

	public void addFieldSeparator(final char fieldSeparator) {
		_fieldSeparators.add(fieldSeparator);
	}

	public List<Character> getFieldSeparators() {
		return Collections.unmodifiableList(_fieldSeparators);
	}

	public void addLineEnding(final String lineEnding) {
		_lineEndings.add(lineEnding);
	}

	public List<String> getLineEndings() {
		return Collections.unmodifiableList(_lineEndings);
	}

	public void setQuoteFields(final boolean quoteFields) {
		_quoteFields = quoteFields;
	}

	public boolean isQuoteFields() {
		return _quoteFields;
	}

	public void setDecimalSeparator(final char decimalSeparator) {
		_decimalSeparator = decimalSeparator;
	}

	public char getDecimalSeparator() {
		return _decimalSeparator;
	}

	public void addTitle(final String title) {
		_titles.add(title);
	}

	public List<String> getTitles() {
		return Collections.unmodifiableList(_titles);
	}

	public void addType(final ColumnDatatypeEnum type) {
		_types.add(type);
	}

	public List<ColumnDatatypeEnum> getTypes() {
		return Collections.unmodifiableList(_types);
	}

	public void addPlot(final Integer plot) {
		_plots.add(plot);
	}

	public List<Integer> getPlots() {
		return Collections.unmodifiableList(_plots);
	}

	public void addUsage(final ColumnUsageEnum usage) {
		_usages.add(usage);
	}

	public List<ColumnUsageEnum> getUsages() {
		return Collections.unmodifiableList(_usages);
	}

	public int getColumnCount() {
		return _titles.size();
	}
}