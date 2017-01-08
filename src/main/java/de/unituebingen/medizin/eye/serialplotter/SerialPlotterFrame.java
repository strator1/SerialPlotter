package de.unituebingen.medizin.eye.serialplotter;

import static java.util.ResourceBundle.getBundle;

import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnUsageEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnDatatypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.XTypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.Configuration;
import de.unituebingen.medizin.eye.serialplotter.swing.table.MyXYDatasetTableModel;
import de.unituebingen.medizin.eye.serialplotter.parser.Record;
import de.unituebingen.medizin.eye.serialplotter.parser.ParseEvent;
import de.unituebingen.medizin.eye.serialplotter.parser.Parser;
import de.unituebingen.medizin.eye.serialplotter.parser.ParseEventListener;

import au.com.bytecode.opencsv.CSVWriter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author strasser
 */
public class SerialPlotterFrame extends javax.swing.JFrame implements ParseEventListener {
	private static final String[] PARITY_STRING							= getBundle("strings").getString("parity.strings").split(",");

	private static final String PREFERENCE_LAST_DIRECTORY				= "lastDirectory";
	private static final String PROPERTY_PORT							= "port";
	private static final String PROPERTY_BAUDRATE						= "baudrate";
	private static final String PROPERTY_CONFIG							= "config";
	private static final String PROPERTY_DATABITS						= "databits";
	private static final String PROPERTY_STOPBITS						= "stopbits";
	private static final String PROPERTY_PARITY							= "parity";
	private static final String PROPERTY_FLOW_CONTROL					= "flowControl";
	private static final String PROPERTY_SERIAL_PORT					= "serialPort";
	private static final String PROPERTY_RECORDING						= "recording";
	private static final String PROPERTY_SERIAL_PORT_CONNECTION_STRING	= "serialPortConnectionString";
	private static final String PROPERTY_CONNECTED						= "connected";

	private final DefaultTableXYDataset _dataset = new DefaultTableXYDataset(false);

	private String _port;
	private int _baudrate;
	private int _databits;
	private int _stopbits;
	private int _parity;
	private int _flowControl;
	private String _connectionString;

	private SerialPort _serialPort;

	private Configuration _config;

	private boolean _connected = false;
	private boolean _recording = false;

	private Parser _parser;

	/**
	 * Creates new form SerialPlotterFrame
	 */
	public SerialPlotterFrame() {
		initComponents();

		addPropertyChangeListener(PROPERTY_CONFIG, e -> {
			final Configuration config = (Configuration) e.getNewValue();

			clearPlots();

			final DefaultDrawingSupplier supplier = new DefaultDrawingSupplier();
			final Map<Integer, XYPlot> plots = new HashMap<>(config.getColumnCount());
			for (int i = 0; i < config.getColumnCount(); i++) {
				final ColumnUsageEnum usage = config.getUsages().get(i);
				if (usage == ColumnUsageEnum.Y_AXIS) {
					final XYSeries series = new XYSeries(config.getTitles().get(i), true, false);

					_dataset.addSeries(series);

					final XYPlot plot = plots.computeIfAbsent(config.getPlots().get(i), k -> {
						final XYPlot p = new XYPlot();
						p.setBackgroundPaint(null);
						_combinedDomainXYPlot.add(p);
						return p;
					});

					final int datasetIndex = plot.getDatasetCount();
					plot.setDataset(datasetIndex, new XYSeriesCollection(series));
					final int axisIndex = plot.getRangeAxisCount();
					plot.setRangeAxis(axisIndex, new NumberAxis(config.getTitles().get(i)));
					plot.mapDatasetToRangeAxis(datasetIndex, axisIndex);
					final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
					renderer.setBasePaint(supplier.getNextPaint());
					plot.setRenderer(datasetIndex, renderer);
				} else if (usage == ColumnUsageEnum.STORE) {
					_dataset.addSeries(new XYSeries(config.getTitles().get(i), true, false));					
				}
			}

			_xyTableModel.fireTableStructureChanged();
		});
	}

	@Override
	public void parseEvent(final ParseEvent e) {
		final Record record = e.getRecord();

		final Number x;
		if (_config.getTypeX() == XTypeEnum.INDEX) {
			x = record.getIndex();
		} else if (_config.getTypeX() == XTypeEnum.TIMESTAMP) {
			x = record.getTimestamp().toEpochMilli();
		} else {
			final int xIndex = _config.getColumnXIndex();
			final ColumnDatatypeEnum type = _config.getTypes().get(xIndex);
			if (type == ColumnDatatypeEnum.DECIMAL || type == ColumnDatatypeEnum.INTEGER) {
				x = record.getNumber(xIndex);
			} else if (type == ColumnDatatypeEnum.TIMESTAMP) {
				x = record.getTimestamp(xIndex).toEpochMilli();
			} else {
				return;
			}
		}

		for (int i = 0; i < _config.getColumnCount(); i++) {
			final String title = _config.getTitles().get(i);
			final ColumnDatatypeEnum type = _config.getTypes().get(i);
			final int index = i;

			IntStream
				.range(0, _dataset.getSeriesCount())
				.mapToObj(j -> _dataset.getSeries(j))
				.filter(series -> title.equals(series.getKey()))
				.findFirst()
				.ifPresent(series -> {
					if (type == ColumnDatatypeEnum.DECIMAL || type == ColumnDatatypeEnum.INTEGER) {
						series.addOrUpdate(x, record.getNumber(index));
					} else if (type == ColumnDatatypeEnum.TIMESTAMP) {
						series.addOrUpdate(x, record.getTimestamp(index).toEpochMilli());
					}
				});
		}
	}

	public void setPort(final String port) {
		final String old = _port;
		_port = port;
		firePropertyChange(PROPERTY_PORT, old, _port);
	}

	public String getPort() {
		return _port;
	}

	public void setBaudrate(final int baudrate) {
		final int old = _baudrate;
		_baudrate = baudrate;
		firePropertyChange(PROPERTY_BAUDRATE, old, _baudrate);
	}

	public int getBaudrate() {
		return _baudrate;
	}

	public void setDatabits(final int databits) {
		final int old = _databits;
		_databits = databits;
		firePropertyChange(PROPERTY_DATABITS, old, _databits);
	}

	public int getDatabits() {
		return _databits;
	}

	public void setStopbits(final int stopbits) {
		final int old = _stopbits;
		_stopbits = stopbits;
		firePropertyChange(PROPERTY_STOPBITS, old, _stopbits);
	}

	public int getStopbits() {
		return _stopbits;
	}

	public void setParity(final int parity) {
		final int old = _parity;
		_parity = parity;
		firePropertyChange(PROPERTY_PARITY, old, _parity);
	}

	public int getParity() {
		return _parity;
	}

	public void setFlowControl(final int flowControl) {
		final int old = _flowControl;
		_flowControl = flowControl;
		firePropertyChange(PROPERTY_FLOW_CONTROL, old, _flowControl);
	}

	public int getFlowControl() {
		return _flowControl;
	}

	public void setConnected(final boolean connected) {
		final boolean old = _connected;
		_connected = connected;
		firePropertyChange(PROPERTY_CONNECTED, old, _connected);
	}

	public boolean isConnected() {
		return _connected;
	}

	public void setSerialPort(final SerialPort serialPort) {
		final SerialPort old = _serialPort;
		_serialPort = serialPort;
		firePropertyChange(PROPERTY_SERIAL_PORT, old, _serialPort);
	}

	public void setRecording(final boolean recording) {
		final boolean old = _recording;
		_recording = recording;
		firePropertyChange(PROPERTY_RECORDING, old, _recording);
	}

	public boolean isRecording() {
		return _recording;
	}

	public SerialPort getSerialPort() {
		return _serialPort;
	}

	public Configuration getConfig() {
		return _config;
	}

	public void setConfig(final Configuration config) {
		final Configuration old = _config;
		_config = config;
		firePropertyChange(PROPERTY_CONFIG, old, _config);
	}

	public void setSerialPortConnectionString(final String connectionString) {
		final String old = _connectionString;
		_connectionString = connectionString;
		firePropertyChange(PROPERTY_SERIAL_PORT_CONNECTION_STRING, old, _connectionString);
	}

	public String getSerialPortConnectionString() {
		return _connectionString;
	}

	private void connect() {
		try {
			final SerialPort serialPort = new SerialPort(getPort());
			if (serialPort.openPort()) {
				serialPort.setParams(getBaudrate(), getDatabits(), getStopbits(), getParity());
				serialPort.setFlowControlMode(getFlowControl());
				setSerialPort(serialPort);
				serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
				setConnected(true);
			} else {
				setSerialPort(null);
				setConnected(false);
				//TODO: dialog!!
				Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.SEVERE, getBundle("strings").getString("error.serial.open.text"));
			}
		} catch (SerialPortException ex) {
			//TODO: dialog!!
			setSerialPort(null);
			setConnected(false);
			Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.SEVERE, getBundle("strings").getString("error.serial.open.text"), ex);
		}
	}

	private void disconnect() {
		try {
			if (isRecording()) {
				stopRecording();
			}

			if (getSerialPort().closePort()) {
				setConnected(false);
				setSerialPort(null);
			} else {
				//TODO: what now??
				//TODO: dialog!!
			}
		} catch (SerialPortException ex) {
			//TODO: dialog!!
			setSerialPort(null);
			setConnected(false);
			Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void startRecording() {
		try {
			_parser = new Parser(_serialPort, _config);
			_parser.addParseEventListener(this);
			getSerialPort().addEventListener(_parser);
			setRecording(true);
		} catch (SerialPortException ex) {
			Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void stopRecording() {
		try {
			_parser.removeParseEventListener(this);
			if (_serialPort != null) {
				_serialPort.removeEventListener();
			}
			setRecording(false);
		} catch (SerialPortException ex) {
			Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.WARNING, null, ex);
		}
	}

	private void exit() {
		if (isRecording()) {
			stopRecording();
		}
		if (isConnected()) {
			disconnect();
		}

		dispose();
	}

	private void clearPlots() {
		while (_combinedDomainXYPlot.getSubplots().size() > 0) {
			_combinedDomainXYPlot.remove((XYPlot) _combinedDomainXYPlot.getSubplots().get(0));
		}
		_dataset.removeAllSeries();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        final javax.swing.JToolBar toolBar = new javax.swing.JToolBar();
        final javax.swing.JToggleButton connectToggleButton = new javax.swing.JToggleButton();
        final javax.swing.JButton startButton = new javax.swing.JButton();
        final javax.swing.JButton stopButton = new javax.swing.JButton();
        final javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        final javax.swing.JScrollPane tableScrollpane = new javax.swing.JScrollPane();
        final javax.swing.JTable dataTable = new javax.swing.JTable();
        final org.jdesktop.swingx.JXStatusBar statusBar = new org.jdesktop.swingx.JXStatusBar();
        final javax.swing.JLabel statusLabel = new javax.swing.JLabel();
        final javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        final javax.swing.JMenu sessionMenu = new javax.swing.JMenu();
        final javax.swing.JCheckBoxMenuItem connectCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        final javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        final javax.swing.JMenuItem startMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JMenuItem stopMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JPopupMenu.Separator jSeparator4 = new javax.swing.JPopupMenu.Separator();
        final javax.swing.JMenuItem clearMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        final javax.swing.JMenuItem saveMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JPopupMenu.Separator jSeparator3 = new javax.swing.JPopupMenu.Separator();
        final javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JMenu editMenu = new javax.swing.JMenu();
        final javax.swing.JMenu settingsMenu = new javax.swing.JMenu();
        final javax.swing.JMenuItem serialPortMenuItem = new javax.swing.JMenuItem();
        final javax.swing.JMenuItem parserMenuItem = new javax.swing.JMenuItem();

        _fc.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("strings"); // NOI18N
        _fc.setDialogTitle(bundle.getString("dialog.save.title")); // NOI18N
        _fc.setFileFilter(new de.unituebingen.medizin.eye.serialplotter.swing.MyFileFilter(bundle.getString("dialog.save.fileTypes"), bundle.getString("dialog.save.fileExtensions").split(",")));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("frame.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        connectToggleButton.setFocusable(false);
        connectToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connectToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected}"), connectToggleButton, org.jdesktop.beansbinding.BeanProperty.create("selected"), "connectToggleButtonSelectedBinding");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${port != null}"), connectToggleButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "connectToolbarButtonEnabledBinding");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected ? 'Disconnect' : 'Connect'}"), connectToggleButton, org.jdesktop.beansbinding.BeanProperty.create("text"), "connectButtonTextBinding");
        bindingGroup.addBinding(binding);

        connectToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });
        toolBar.add(connectToggleButton);

        startButton.setText(bundle.getString("menu.session.startRecording.text")); // NOI18N
        startButton.setFocusable(false);
        startButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected and config != null and !recording}"), startButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "startRecordingButtonEnabledBinding");
        bindingGroup.addBinding(binding);

        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });
        toolBar.add(startButton);

        stopButton.setText(bundle.getString("menu.session.stopRecording.text")); // NOI18N
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected and config != null and recording}"), stopButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "stopRecordingButtonEnabledBinding");
        bindingGroup.addBinding(binding);

        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        toolBar.add(stopButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout(4, 4));

        tableScrollpane.setPreferredSize(new java.awt.Dimension(200, 2));

        dataTable.setModel(_xyTableModel);
        tableScrollpane.setViewportView(dataTable);

        mainPanel.add(tableScrollpane, java.awt.BorderLayout.EAST);

        final JFreeChart chart = new JFreeChart(_combinedDomainXYPlot);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        statusBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        statusLabel.setMinimumSize(new java.awt.Dimension(200, 14));
        statusLabel.setPreferredSize(new java.awt.Dimension(200, 14));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${serialPortConnectionString}"), statusLabel, org.jdesktop.beansbinding.BeanProperty.create("text"), "serialPortConnectionStringBinding");
        bindingGroup.addBinding(binding);

        statusBar.add(statusLabel);

        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        sessionMenu.setText(bundle.getString("menu.session.text")); // NOI18N

        connectCheckBoxMenuItem.setText(bundle.getString("menu.session.connect.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected}"), connectCheckBoxMenuItem, org.jdesktop.beansbinding.BeanProperty.create("selected"), "connectMenuItemSelectedBinding");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${port != null}"), connectCheckBoxMenuItem, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "connectMenuItemEnabledBinding");
        bindingGroup.addBinding(binding);

        connectCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });
        sessionMenu.add(connectCheckBoxMenuItem);
        sessionMenu.add(jSeparator2);

        startMenuItem.setText(bundle.getString("menu.session.startRecording.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected and config != null and !recording}"), startMenuItem, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "startRecordingMenuItemEnabledBinding");
        bindingGroup.addBinding(binding);

        startMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });
        sessionMenu.add(startMenuItem);

        stopMenuItem.setText(bundle.getString("menu.session.stopRecording.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${connected and config != null and recording}"), stopMenuItem, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "stopRecordingMenuItemEnabledBinding");
        bindingGroup.addBinding(binding);

        stopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        sessionMenu.add(stopMenuItem);
        sessionMenu.add(jSeparator4);

        clearMenuItem.setText(bundle.getString("menu.session.clear.text")); // NOI18N
        clearMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuItemActionPerformed(evt);
            }
        });
        sessionMenu.add(clearMenuItem);
        sessionMenu.add(jSeparator1);

        saveMenuItem.setText(bundle.getString("menu.session.save.text")); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        sessionMenu.add(saveMenuItem);
        sessionMenu.add(jSeparator3);

        exitMenuItem.setText(bundle.getString("menu.session.exit.text")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        sessionMenu.add(exitMenuItem);

        menuBar.add(sessionMenu);

        editMenu.setText(bundle.getString("menu.edit.text")); // NOI18N
        menuBar.add(editMenu);

        settingsMenu.setText(bundle.getString("menu.settings.text")); // NOI18N

        serialPortMenuItem.setText(bundle.getString("menu.settings.serialPort.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${!connected}"), serialPortMenuItem, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "serialPortMenuItemEnabledBinding");
        bindingGroup.addBinding(binding);

        serialPortMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serialPortMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(serialPortMenuItem);

        parserMenuItem.setText(bundle.getString("menu.settings.parser.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${!recording}"), parserMenuItem, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "dataSetupMenuItemEnabledBinding");
        bindingGroup.addBinding(binding);

        parserMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parserMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(parserMenuItem);

        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serialPortMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serialPortMenuItemActionPerformed
		final SerialPortConfigurationPanel panel = new SerialPortConfigurationPanel();
		final JButton ok = new JButton(getBundle("strings").getString("button.ok.text"));
		ok.addActionListener(e ->{
			((JOptionPane) ((Component) e.getSource()).getParent().getParent()).setValue(ok);
			final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
			if (w != null) {
				w.setVisible(false);
			}
		});
		org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, panel, org.jdesktop.beansbinding.ELProperty.create("${port != null}"), ok, org.jdesktop.beansbinding.BeanProperty.create("enabled")).bind();
		
		final JButton cancel = new JButton(getBundle("strings").getString("button.cancel.text"));
		cancel.addActionListener(e -> {
			((JOptionPane) ((Component) e.getSource()).getParent().getParent()).setValue(cancel);
			final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
			if (w != null) {
				w.setVisible(false);
			}
		});

		if (0 == JOptionPane.showOptionDialog(this, panel, getBundle("strings").getString("dialog.serialport.title"), JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new JButton[] {ok, cancel}, ok)) {
			setPort(panel.getPort());
			setBaudrate(panel.getBaudrate());
			setDatabits(panel.getDatabits());
			setStopbits(panel.getStopbits());
			setParity(panel.getParity());
			setFlowControl(panel.getFlowControl());

			setSerialPortConnectionString(String.format(getBundle("strings").getString("frame.statuslabel.text.format"), getPort(), getBaudrate(), getDatabits(), PARITY_STRING[getParity()], (getStopbits() == 3 ? "1.5" : getStopbits())));
		} else {
			setPort(null);
			setSerialPortConnectionString("");
		}
    }//GEN-LAST:event_serialPortMenuItemActionPerformed

    private void connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectActionPerformed
		if (isConnected()) {
			disconnect();
		} else {
			connect();
		}
    }//GEN-LAST:event_connectActionPerformed

    private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
        startRecording();
    }//GEN-LAST:event_startActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        stopRecording();
    }//GEN-LAST:event_stopActionPerformed

    private void parserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parserMenuItemActionPerformed
		final DataSetupPanel panel = new DataSetupPanel();
		if (getConfig() != null) {
			panel.init(getConfig());
		}
		final JButton ok = new JButton(getBundle("strings").getString("button.ok.text"));
		ok.addActionListener(e ->{
			((JOptionPane) ((Component) e.getSource()).getParent().getParent()).setValue(ok);
			final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
			if (w != null) {
				w.setVisible(false);
			}
		});
		org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, panel, org.jdesktop.beansbinding.ELProperty.create("${port != null}"), ok, org.jdesktop.beansbinding.BeanProperty.create("enabled")).bind();

		final JButton cancel = new JButton(getBundle("strings").getString("button.cancel.text"));
		cancel.addActionListener(e -> {
			((JOptionPane) ((Component) e.getSource()).getParent().getParent()).setValue(cancel);
			final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
			if (w != null) {
				w.setVisible(false);
			}
		});

		if (0 == JOptionPane.showOptionDialog(this, panel, getBundle("strings").getString("dialog.parserconfig.title"), JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new JButton[] {ok, cancel}, ok)) {
			final Configuration config = panel.getConfig();
			setConfig(config);
		}
    }//GEN-LAST:event_parserMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        exit();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        exit();
    }//GEN-LAST:event_formWindowClosed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
		_fc.setCurrentDirectory(new File(Preferences.userNodeForPackage(getClass()).get(PREFERENCE_LAST_DIRECTORY, ".")));
		if (JFileChooser.APPROVE_OPTION == _fc.showSaveDialog(this)) {
			try (final CSVWriter writer = new CSVWriter(new FileWriter(_fc.getSelectedFile()), '\t')) {
				writer.writeAll(
					IntStream
						.range(-1, _dataset.getItemCount())
						.mapToObj(j ->
							IntStream
								.range(0, _dataset.getSeriesCount())
								.mapToObj(i -> _dataset.getSeries(i))
								.map(series -> j < 0 ? series.getKey() : series.getY(j).toString())
								.toArray(size -> new String[size])
						)
						.collect(Collectors.toList())
				);
				Preferences.userNodeForPackage(getClass()).put(PREFERENCE_LAST_DIRECTORY, _fc.getCurrentDirectory().getAbsolutePath());
			} catch (IOException ex) {
				//TODO: dialog!
				Logger.getLogger(SerialPlotterFrame.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed
        for (int i = 0; i < _dataset.getSeriesCount(); i++) {
			_dataset.getSeries(i).clear();
		}
    }//GEN-LAST:event_clearMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final org.jfree.chart.plot.CombinedDomainXYPlot _combinedDomainXYPlot = new org.jfree.chart.plot.CombinedDomainXYPlot();
    private final javax.swing.JFileChooser _fc = new javax.swing.JFileChooser();
    private final org.jfree.data.xy.XYDatasetTableModel _xyTableModel = new MyXYDatasetTableModel(_dataset, _config)
    ;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}