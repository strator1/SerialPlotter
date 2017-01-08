package de.unituebingen.medizin.eye.serialplotter.parser;

import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnDatatypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnUsageEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.Configuration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * @author strasser
 */
public final class Parser implements SerialPortEventListener {
	private static final Pattern QUOTE_PATTERN = Pattern.compile("^\"?([^\"]*)\"?$");

	private final SerialPort _serialPort;
	private final Configuration _config;
	private final Pattern _rowPattern;
	private final String _colPattern;
	private final DecimalFormat _decimalFormat;

	private final StringBuilder _buffer = new StringBuilder();

	private final List<ParseEventListener> _listeners = Collections.synchronizedList(new ArrayList<>(1));

	private int _index = 0;

	public Parser(final SerialPort port, final Configuration config) {
		_serialPort = port;
		_config = config;

		_decimalFormat = new DecimalFormat();
		final DecimalFormatSymbols symbol = new DecimalFormatSymbols();
		symbol.setDecimalSeparator(_config.getDecimalSeparator());
		_decimalFormat.setDecimalFormatSymbols(symbol);

		final int maxLineEndingLength = _config.getLineEndings().stream().mapToInt(s -> s.length()).max().getAsInt();
		_rowPattern = Pattern.compile(_config.getLineEndings().stream().collect(Collectors.joining("|", "(.*?)[", String.format("]{1,%d}", maxLineEndingLength))));
		_colPattern = _config.getFieldSeparators().stream().map(c -> c.toString()).collect(Collectors.joining("+|", "(", ")"));
	}

	@Override
	public void serialEvent(final SerialPortEvent event) {
		if (event.isRXCHAR()) {
			try {
				final String s = _serialPort.readString();
				if (s != null) {
					_buffer.append(s);
				}
			} catch (SerialPortException ex) {
				Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
			}

			int end = 0;
			final Matcher matcher = _rowPattern.matcher(_buffer.toString());
			while (matcher.find()) {
				final String row = matcher.group(1);
				if (row != null && row.length() > 0) {
					end = matcher.end();
					processRow(row);
				}
			}
			_buffer.delete(0, end);
		}
	}

	public void addParseEventListener(final ParseEventListener l) {
		_listeners.add(l);
	}

	public void removeParseEventListener(final ParseEventListener l) {
		_listeners.remove(l);
	}

	private void notifyListeners(final ParseEvent event) {
		_listeners.stream().forEach(l -> l.parseEvent(event));
	}

	private void processRow(final String row) {
		final Record record = new Record(_config.getTitles().size(), row, _index++, Instant.now());

		final String[] tokens = row.split(_colPattern);
		if (tokens.length < _config.getTitles().size()) {
			return;
		}
		for (int i = 0; i < _config.getTitles().size(); i++) {
			String field = tokens[i];

			if (field != null && field.length() > 0) {
				final ColumnUsageEnum usage = _config.getUsages().get(i);
				final ColumnDatatypeEnum type = _config.getTypes().get(i);
				if (usage != ColumnUsageEnum.SKIP) {
					if (_config.isQuoteFields()) {
						final Matcher matcher = QUOTE_PATTERN.matcher(field);
						if (matcher.find()) {
							field = matcher.group(1);
						}
					}
					switch (type) {
						case DECIMAL:
							try {
								final Number doubleValue = _decimalFormat.parse(field);
								record.setValue(i, doubleValue);
							} catch (ParseException ex) {
								Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
							}
							break;
						case INTEGER:
							final Number intValue = Integer.parseInt(field);
							record.setValue(i, intValue);
							break;
						case TEXT:
							break;
						case TIMESTAMP:
							final Instant timestamp = Instant.ofEpochMilli(Integer.parseInt(field));
							record.setValue(i, timestamp);
							break;
					}
				}
			}
		}

		notifyListeners(new ParseEvent(this, record));
	}
}