package de.unituebingen.medizin.eye.serialplotter;

import static java.util.ResourceBundle.getBundle;

import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnUsageEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.ColumnDatatypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.XTypeEnum;
import de.unituebingen.medizin.eye.serialplotter.configuration.Configuration;
import de.unituebingen.medizin.eye.serialplotter.swing.table.ColumnConfigTableModel;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * @author strasser
 */
public class DataSetupPanel extends javax.swing.JPanel {
	private static final String PREFERENCES_LAST_DIRECTORY			= "lastDirectory";

	private static final String PROPERTY_TAB_STOP					= "tabStop";
	private static final String PROPERTY_COMMA						= "comma";
	private static final String PROPERTY_SPACE						= "space";
	private static final String PROPERTY_SEMICOLON					= "semicolon";
	private static final String PROPERTY_OTHER						= "other";
	private static final String PROPERTY_OTHER_SEPARATOR_CHAR		= "otherSeparatorChar";
	private static final String PROPERTY_CR_LF						= "crLf";
	private static final String PROPERTY_CR							= "cr";
	private static final String PROPERTY_LF							= "lf";
	private static final String PROPERTY_OTHER_LINE_ENDING			= "otherLineEnding";
	private static final String PROPERTY_OTHER_LINE_ENDING_STRING	= "otherLineEndingString";
	private static final String PROPERTY_USE_QUOTE_CHAR				= "useQuoteChar";
	private static final String PROPERTY_COMMA_DECIMAL_SEPARATOR	= "commaDecimalSeparator";
	private static final String PROPERTY_DOT_DECIMAL_SEPARATOR		= "dotDecimalSeparator";

	private static final char DECIMALSEPARATOR_DOT = '.';
	private static final char DECIMALSEPARATOR_COMMA = ',';
	private static final String LINEENDING_LF = "\n";
	private static final String LINEENDING_CR = "\r";
	private static final String LINEENDING_CRLF = "\r\n";
	private static final char SEPARATOR_SEMICOLON = ';';
	private static final char SEPARATOR_TAB = '\t';
	private static final char SEPARATOR_SPACE = ' ';
	private static final char SEPARATOR_COMMA = ',';

	private boolean _dotDecimalSeparator = true;
	private boolean _crLf = true;
	private boolean _semicolon = true;

	private boolean _tabStop;
	private boolean _comma;
	private boolean _space;
	private boolean _other;
	private Character _otherSeparatorChar;
	private boolean _cr;
	private boolean _lf;
	private boolean _otherLineEnding;
	private String _otherLineEndingString;
	private boolean _useQuoteChar;
	private boolean _commaDecimalSeparator;

	/**
	 * Creates new form DataSetupPanel
	 */
	public DataSetupPanel() {
		initComponents();
	}

	public boolean isTabStop() {
		return _tabStop;
	}

	public void setTabStop(final boolean tabStop) {
		final boolean old = _tabStop;
		_tabStop = tabStop;
		firePropertyChange(PROPERTY_TAB_STOP, old, _tabStop);
	}

	public boolean isComma() {
		return _comma;
	}

	public void setComma(final boolean comma) {
		final boolean old = _comma;
		_comma = comma;
		firePropertyChange(PROPERTY_COMMA, old, _comma);
	}

	public boolean isSpace() {
		return _space;
	}

	public void setSpace(final boolean space) {
		final boolean old = _space;
		_space = space;
		firePropertyChange(PROPERTY_SPACE, old, _space);
	}

	public boolean isSemicolon() {
		return _semicolon;
	}

	public void setSemicolon(final boolean semicolon) {
		final boolean old = _semicolon;
		_semicolon = semicolon;
		firePropertyChange(PROPERTY_SEMICOLON, old, _semicolon);
	}

	public boolean isOther() {
		return _other;
	}

	public void setOther(final boolean other) {
		final boolean old = _other;
		_other = other;
		firePropertyChange(PROPERTY_OTHER, old, _other);
	}

	public Character getOtherSeparatorChar() {
		return _otherSeparatorChar;
	}

	public void setOtherSeparatorChar(final Character otherSeparatorChar) {
		final Character old = _otherSeparatorChar;
		_otherSeparatorChar = otherSeparatorChar;
		firePropertyChange(PROPERTY_OTHER_SEPARATOR_CHAR, old, _otherSeparatorChar);
	}

	public boolean isCrLf() {
		return _crLf;
	}

	public void setCrLf(final boolean crLf) {
		final boolean old = _crLf;
		_crLf = crLf;
		firePropertyChange(PROPERTY_CR_LF, old, _crLf);
	}

	public boolean isCr() {
		return _cr;
	}

	public void setCr(final boolean cr) {
		final boolean old = _cr;
		_cr = cr;
		firePropertyChange(PROPERTY_CR, old, _cr);
	}

	public boolean isLf() {
		return _lf;
	}

	public void setLf(final boolean lf) {
		final boolean old = _lf;
		_lf = lf;
		firePropertyChange(PROPERTY_LF, old, _lf);
	}

	public boolean isOtherLineEnding() {
		return _otherLineEnding;
	}

	public void setOtherLineEnding(final boolean otherLineEnding) {
		final boolean old = _otherLineEnding;
		_otherLineEnding = otherLineEnding;
		firePropertyChange(PROPERTY_OTHER_LINE_ENDING, old, _otherLineEnding);
	}

	public String getOtherLineEndingString() {
		return _otherLineEndingString;
	}

	public void setOtherLineEndingString(final String otherLineEndingString) {
		final String old = _otherLineEndingString;
		_otherLineEndingString = otherLineEndingString;
		firePropertyChange(PROPERTY_OTHER_LINE_ENDING_STRING, old, _otherLineEndingString);
	}

	public boolean isUseQuoteChar() {
		return _useQuoteChar;
	}

	public void setUseQuoteChar(final boolean useQuoteChar) {
		final boolean old = _useQuoteChar;
		_useQuoteChar = useQuoteChar;
		firePropertyChange(PROPERTY_USE_QUOTE_CHAR, old, _useQuoteChar);
	}

	public boolean isCommaDecimalSeparator() {
		return _commaDecimalSeparator;
	}

	public void setCommaDecimalSeparator(final boolean commaDecimalSeparator) {
		final boolean old = _commaDecimalSeparator;
		_commaDecimalSeparator = commaDecimalSeparator;
		firePropertyChange(PROPERTY_COMMA_DECIMAL_SEPARATOR, old, _commaDecimalSeparator);
	}

	public boolean isDotDecimalSeparator() {
		return _dotDecimalSeparator;
	}

	public void setDotDecimalSeparator(final boolean dotDecimalSeparator) {
		final boolean old = _dotDecimalSeparator;
		_dotDecimalSeparator = dotDecimalSeparator;
		firePropertyChange(PROPERTY_DOT_DECIMAL_SEPARATOR, old, _dotDecimalSeparator);
	}

	public Configuration getConfig() {
		final Configuration config = new Configuration();

		if(isComma()) {
			config.addFieldSeparator(SEPARATOR_COMMA);
		}
		if (isSpace()) {
			config.addFieldSeparator(SEPARATOR_SPACE);
		}
		if (isTabStop()) {
			config.addFieldSeparator(SEPARATOR_TAB);
		}
		if (isSemicolon()) {
			config.addFieldSeparator(SEPARATOR_SEMICOLON);
		}
		if (isOther()) {
			config.addFieldSeparator(getOtherSeparatorChar());
		}
		if (isCrLf()) {
			config.addLineEnding(LINEENDING_CRLF);
		}
		if (isCr()) {
			config.addLineEnding(LINEENDING_CR);
		}
		if (isLf()) {
			config.addLineEnding(LINEENDING_LF);
		}
		if (isOtherLineEnding()) {
			config.addLineEnding(getOtherLineEndingString());
		}
		if (isCommaDecimalSeparator()) {
			config.setDecimalSeparator(DECIMALSEPARATOR_COMMA);
		} else if (isDotDecimalSeparator()) {
			config.setDecimalSeparator(DECIMALSEPARATOR_DOT);
		}
		if (isUseQuoteChar()) {
			config.setQuoteFields(true);
		}

		setUseQuoteChar(false);

		final DefaultTableModel tm = ((DefaultTableModel) _columnsTable.getModel());
		if (tm.getValueAt(0, 3) == ColumnUsageEnum.X_AXIS) {
			config.setTypeX(XTypeEnum.INDEX);
		} else if (tm.getValueAt(1, 3) == ColumnUsageEnum.X_AXIS) {
			config.setTypeX(XTypeEnum.TIMESTAMP);
		}
		for (int i = 2; i < tm.getRowCount(); i++) {
			final String title = (String) tm.getValueAt(i, 1);
			final ColumnDatatypeEnum type = (ColumnDatatypeEnum) tm.getValueAt(i, 2);
			final ColumnUsageEnum usage = (ColumnUsageEnum) tm.getValueAt(i, 3);
			final Integer plot = (Integer) tm.getValueAt(i, 4);

			if (usage == ColumnUsageEnum.X_AXIS) {
				config.setTypeX(XTypeEnum.COLUMN);
				config.setColumnXIndex(i-2);
			}

			config.addTitle(title);
			config.addType(type);
			config.addPlot(plot);
			config.addUsage(usage);
		}

		return config;
	}

	private void clear() {
		setComma(false);
		setSpace(false);
		setTabStop(false);
		setSemicolon(false);
		setOther(false);
		setOtherSeparatorChar(null);

		setCr(false);
		setLf(false);
		setCrLf(false);
		setOtherLineEnding(false);
		setOtherLineEndingString(null);

		setDotDecimalSeparator(true);
		setCommaDecimalSeparator(false);
	}

	void init(final Configuration config) {
		clear();

		for (final char c : config.getFieldSeparators()) {
			switch (c) {
				case SEPARATOR_COMMA:
					setComma(true);
					break;
				case SEPARATOR_SPACE:
					setSpace(true);
					break;
				case SEPARATOR_TAB:
					setTabStop(true);
					break;
				case SEPARATOR_SEMICOLON:
					setSemicolon(true);
					break;
				default:
					setOther(true);
					setOtherSeparatorChar(c);
					break;
			}
		}

		config.getLineEndings().stream().forEach(s -> {
			switch (s) {
				case LINEENDING_CRLF:
					setCrLf(true);
					break;
				case LINEENDING_CR:
					setCr(true);
					break;
				case LINEENDING_LF:
					setLf(true);				
					break;
				default:
					setOtherLineEnding(true);
					setOtherLineEndingString(s);
					break;
			}
		});

		setUseQuoteChar(config.isQuoteFields());

		switch(config.getDecimalSeparator()) {
			case DECIMALSEPARATOR_DOT:
				setDotDecimalSeparator(true);
				setCommaDecimalSeparator(false);
				break;
			case DECIMALSEPARATOR_COMMA: {
				setDotDecimalSeparator(false);
				setCommaDecimalSeparator(true);
				break;
			}
		}

		for (int i = 0; i < config.getColumnCount(); i++) {
			final String title = config.getTitles().get(i);
			final ColumnDatatypeEnum type = config.getTypes().get(i);
			final ColumnUsageEnum usage = config.getUsages().get(i);
			addColumn(title, type, usage, config.getPlots().get(i));
		}

		((DefaultTableModel) _columnsTable.getModel()).setValueAt(config.getTypeX() == XTypeEnum.INDEX ? ColumnUsageEnum.X_AXIS : ColumnUsageEnum.SKIP, 0, 3);
		((DefaultTableModel) _columnsTable.getModel()).setValueAt(config.getTypeX() == XTypeEnum.TIMESTAMP ? ColumnUsageEnum.X_AXIS : ColumnUsageEnum.SKIP, 1, 3);
	}

	private void addColumn(final ColumnDatatypeEnum format, final Object usage, final Integer plot) {
		final int column = _columnsTable.getModel().getRowCount() - 1;
		addColumn(String.format("Col %d", column), format, usage, plot);
	}

	private void addColumn(final String title, final ColumnDatatypeEnum format, final Object usage, final Integer plot) {
		final int column = _columnsTable.getModel().getRowCount() - 1;
		((DefaultTableModel) _columnsTable.getModel()).addRow(new Object[] {
			column,
			title,
			format,
			usage,
			plot
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        final javax.swing.ButtonGroup decimalSeparatorButtonGroup = new javax.swing.ButtonGroup();
        final javax.swing.JComboBox dataTypeComboBox = new javax.swing.JComboBox();
        final javax.swing.JComboBox usageComboBox = new javax.swing.JComboBox();
        final de.unituebingen.medizin.eye.serialplotter.swing.table.ColumnConfigTableModel columnConfigTableModel = new de.unituebingen.medizin.eye.serialplotter.swing.table.ColumnConfigTableModel();
        final de.unituebingen.medizin.eye.serialplotter.swing.table.SpinnerNumberEditor plotNrSpinner = new de.unituebingen.medizin.eye.serialplotter.swing.table.SpinnerNumberEditor();
        final javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
        final javax.swing.JPanel importPanel = new javax.swing.JPanel();
        final javax.swing.JPanel columnSeparatorPanel = new javax.swing.JPanel();
        final javax.swing.JCheckBox tabStopCharCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox spacheCharCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox commaCharCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox otherCharCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JTextField otherCharTextField = new javax.swing.JTextField();
        final javax.swing.JCheckBox semicolonCharCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JPanel lineEndingPanel = new javax.swing.JPanel();
        final javax.swing.JCheckBox crLfLineEndingCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox crLineEndingCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox lfLineEndingCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JCheckBox otherLineEndingCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JTextField otherLineEndingTextField = new javax.swing.JTextField();
        final javax.swing.JPanel decimalSeparatorPanel = new javax.swing.JPanel();
        final javax.swing.JRadioButton commaRadioButton = new javax.swing.JRadioButton();
        final javax.swing.JRadioButton dotRadioButton = new javax.swing.JRadioButton();
        final javax.swing.JPanel fieldQuotesPanel = new javax.swing.JPanel();
        final javax.swing.JCheckBox fieldQuotesCheckBox = new javax.swing.JCheckBox();
        final javax.swing.JPanel columnsTabPane = new javax.swing.JPanel();
        final javax.swing.JScrollPane columnsTableScrollPane = new javax.swing.JScrollPane();
        final javax.swing.JPanel columnButtonPanel = new javax.swing.JPanel();
        final javax.swing.JButton addColumnButton = new javax.swing.JButton();
        final javax.swing.JButton deleteColumnButton = new javax.swing.JButton();
        final javax.swing.JPanel filterTabPanel = new javax.swing.JPanel();
        final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        final javax.swing.JButton saveButton = new javax.swing.JButton();
        final javax.swing.JButton loadButton = new javax.swing.JButton();

        dataTypeComboBox.setModel(new DefaultComboBoxModel(ColumnDatatypeEnum.values()));

        _fc.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("strings"); // NOI18N
        _fc.setDialogTitle(bundle.getString("dialog.datasetup.savedialog.title")); // NOI18N
        _fc.setFileFilter(new de.unituebingen.medizin.eye.serialplotter.swing.MyFileFilter("Config file", "xml"));

        usageComboBox.setModel(new DefaultComboBoxModel(ColumnUsageEnum.values()));

        columnConfigTableModel.addTableModelListener(new javax.swing.event.TableModelListener() {
            public void tableChanged(javax.swing.event.TableModelEvent evt) {
                columnConfigTableModelTableChanged(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        columnSeparatorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("dialog.datasetup.columnseparator.title"))); // NOI18N

        tabStopCharCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.tabstop.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${tabStop}"), tabStopCharCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "tabStopSeparatorBinding");
        bindingGroup.addBinding(binding);

        spacheCharCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.space.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${space}"), spacheCharCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "spaceSeparatorBinding");
        bindingGroup.addBinding(binding);

        commaCharCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.comma.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${comma}"), commaCharCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "commaSeparatorBinding");
        bindingGroup.addBinding(binding);

        otherCharCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.other.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${other}"), otherCharCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "otherSeparatorBinding");
        bindingGroup.addBinding(binding);

        otherCharTextField.setPreferredSize(new java.awt.Dimension(30, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${otherSeparatorChar}"), otherCharTextField, org.jdesktop.beansbinding.BeanProperty.create("text"), "otherSeparatorCharBinding");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, otherCharCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), otherCharTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "otherCharTextFieldEnabledBinding");
        bindingGroup.addBinding(binding);

        semicolonCharCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.semicolon.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${semicolon}"), semicolonCharCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "semicolorSeparatorBinding");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout columnSeparatorPanelLayout = new javax.swing.GroupLayout(columnSeparatorPanel);
        columnSeparatorPanel.setLayout(columnSeparatorPanelLayout);
        columnSeparatorPanelLayout.setHorizontalGroup(
            columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnSeparatorPanelLayout.createSequentialGroup()
                .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabStopCharCheckBox)
                    .addComponent(spacheCharCheckBox)
                    .addComponent(otherCharCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(columnSeparatorPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(otherCharTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(columnSeparatorPanelLayout.createSequentialGroup()
                        .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(commaCharCheckBox)
                            .addComponent(semicolonCharCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        columnSeparatorPanelLayout.setVerticalGroup(
            columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnSeparatorPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabStopCharCheckBox)
                    .addComponent(commaCharCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spacheCharCheckBox)
                    .addComponent(semicolonCharCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(columnSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(otherCharTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(otherCharCheckBox))
                .addGap(8, 8, 8))
        );

        lineEndingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("dialog.datasetup.lineending.title"))); // NOI18N

        crLfLineEndingCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.crlf.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${crLf}"), crLfLineEndingCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "crLfLineEndingBinding");
        bindingGroup.addBinding(binding);

        crLineEndingCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.cr.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cr}"), crLineEndingCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "crLineEndingBinding");
        bindingGroup.addBinding(binding);

        lfLineEndingCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.lf.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${lf}"), lfLineEndingCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "lfLineEndingBinding");
        bindingGroup.addBinding(binding);

        otherLineEndingCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.otherlineending.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${otherLineEnding}"), otherLineEndingCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "otherLineEndingBinding");
        bindingGroup.addBinding(binding);

        otherLineEndingTextField.setPreferredSize(new java.awt.Dimension(30, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${otherLineEndingString}"), otherLineEndingTextField, org.jdesktop.beansbinding.BeanProperty.create("text"), "otherLineEndingCharBinding");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, otherLineEndingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), otherLineEndingTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "otherLineEndingTextFieldEnabledBinding");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout lineEndingPanelLayout = new javax.swing.GroupLayout(lineEndingPanel);
        lineEndingPanel.setLayout(lineEndingPanelLayout);
        lineEndingPanelLayout.setHorizontalGroup(
            lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lineEndingPanelLayout.createSequentialGroup()
                .addGroup(lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lfLineEndingCheckBox)
                    .addGroup(lineEndingPanelLayout.createSequentialGroup()
                        .addGroup(lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(crLfLineEndingCheckBox)
                            .addComponent(crLineEndingCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(otherLineEndingCheckBox)
                            .addGroup(lineEndingPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(otherLineEndingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(21, 21, 21))
        );
        lineEndingPanelLayout.setVerticalGroup(
            lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lineEndingPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crLfLineEndingCheckBox)
                    .addComponent(otherLineEndingCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lineEndingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crLineEndingCheckBox)
                    .addComponent(otherLineEndingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, Short.MAX_VALUE)
                .addComponent(lfLineEndingCheckBox)
                .addGap(8, 8, 8))
        );

        decimalSeparatorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("dialog.datasetup.decimalseparator.title"))); // NOI18N

        decimalSeparatorButtonGroup.add(commaRadioButton);
        commaRadioButton.setText(bundle.getString("dialog.datasetup.radio.comma.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${commaDecimalSeparator}"), commaRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"), "commaDecimalSeparatorBinding");
        bindingGroup.addBinding(binding);

        decimalSeparatorButtonGroup.add(dotRadioButton);
        dotRadioButton.setText(bundle.getString("dialog.datasetup.radio.dot.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dotDecimalSeparator}"), dotRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"), "dotDecimalSeparatorBinding");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout decimalSeparatorPanelLayout = new javax.swing.GroupLayout(decimalSeparatorPanel);
        decimalSeparatorPanel.setLayout(decimalSeparatorPanelLayout);
        decimalSeparatorPanelLayout.setHorizontalGroup(
            decimalSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decimalSeparatorPanelLayout.createSequentialGroup()
                .addGroup(decimalSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commaRadioButton)
                    .addComponent(dotRadioButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        decimalSeparatorPanelLayout.setVerticalGroup(
            decimalSeparatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decimalSeparatorPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(commaRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotRadioButton))
        );

        fieldQuotesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("dialog.datasetup.quotefield.title"))); // NOI18N

        fieldQuotesCheckBox.setText(bundle.getString("dialog.datasetup.checkbox.quotefields.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${useQuoteChar}"), fieldQuotesCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"), "useQuoteCharBinding");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout fieldQuotesPanelLayout = new javax.swing.GroupLayout(fieldQuotesPanel);
        fieldQuotesPanel.setLayout(fieldQuotesPanelLayout);
        fieldQuotesPanelLayout.setHorizontalGroup(
            fieldQuotesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldQuotesPanelLayout.createSequentialGroup()
                .addComponent(fieldQuotesCheckBox)
                .addGap(0, 0, 0))
        );
        fieldQuotesPanelLayout.setVerticalGroup(
            fieldQuotesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldQuotesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldQuotesCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout importPanelLayout = new javax.swing.GroupLayout(importPanel);
        importPanel.setLayout(importPanelLayout);
        importPanelLayout.setHorizontalGroup(
            importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelLayout.createSequentialGroup()
                .addGroup(importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(decimalSeparatorPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(columnSeparatorPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lineEndingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldQuotesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        importPanelLayout.setVerticalGroup(
            importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelLayout.createSequentialGroup()
                .addGroup(importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lineEndingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(columnSeparatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(decimalSeparatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldQuotesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        tabbedPane.addTab(bundle.getString("dialog.datasetup.tabs.importconfig.title"), importPanel); // NOI18N

        columnsTableScrollPane.setBorder(null);

        _columnsTable.setModel(columnConfigTableModel);
        _columnsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        final TableColumn typeColumn = _columnsTable.getColumnModel().getColumn(2);
        typeColumn.setCellEditor(new DefaultCellEditor(dataTypeComboBox));
        final TableColumn typeColumn2 = _columnsTable.getColumnModel().getColumn(3);
        typeColumn2.setCellEditor(new DefaultCellEditor(usageComboBox));
        final TableColumn typeColumn3 = _columnsTable.getColumnModel().getColumn(4);
        typeColumn3.setCellEditor(plotNrSpinner);
        columnsTableScrollPane.setViewportView(_columnsTable);

        addColumnButton.setText(bundle.getString("dialog.datasetup.buttons.addcolumn.text")); // NOI18N
        addColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addColumnButtonActionPerformed(evt);
            }
        });

        deleteColumnButton.setText(bundle.getString("dialog.datasetup.buttons.deletecolumn.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, _columnsTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), deleteColumnButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "deleteColumnButtonEnabledBinding");
        bindingGroup.addBinding(binding);

        deleteColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteColumnButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout columnButtonPanelLayout = new javax.swing.GroupLayout(columnButtonPanel);
        columnButtonPanel.setLayout(columnButtonPanelLayout);
        columnButtonPanelLayout.setHorizontalGroup(
            columnButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnButtonPanelLayout.createSequentialGroup()
                .addGroup(columnButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(deleteColumnButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addColumnButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        columnButtonPanelLayout.setVerticalGroup(
            columnButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnButtonPanelLayout.createSequentialGroup()
                .addComponent(addColumnButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteColumnButton)
                .addContainerGap(141, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout columnsTabPaneLayout = new javax.swing.GroupLayout(columnsTabPane);
        columnsTabPane.setLayout(columnsTabPaneLayout);
        columnsTabPaneLayout.setHorizontalGroup(
            columnsTabPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnsTabPaneLayout.createSequentialGroup()
                .addComponent(columnsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        columnsTabPaneLayout.setVerticalGroup(
            columnsTabPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(columnsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(columnButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPane.addTab(bundle.getString("dialog.datasetup.tabs.columns.title"), columnsTabPane); // NOI18N

        javax.swing.GroupLayout filterTabPanelLayout = new javax.swing.GroupLayout(filterTabPanel);
        filterTabPanel.setLayout(filterTabPanelLayout);
        filterTabPanelLayout.setHorizontalGroup(
            filterTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 337, Short.MAX_VALUE)
        );
        filterTabPanelLayout.setVerticalGroup(
            filterTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 193, Short.MAX_VALUE)
        );

        tabbedPane.addTab(bundle.getString("dialog.datasetup.tabs.filter.title"), filterTabPanel); // NOI18N

        add(tabbedPane, java.awt.BorderLayout.CENTER);

        saveButton.setText(bundle.getString("dialog.datasetup.buttons.save.text")); // NOI18N
        saveButton.setPreferredSize(new java.awt.Dimension(60, 23));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setText(bundle.getString("dialog.datasetup.buttons.load.text")); // NOI18N
        loadButton.setPreferredSize(new java.awt.Dimension(60, 23));
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(200, 200, 200))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void addColumnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addColumnButtonActionPerformed
		addColumn(ColumnDatatypeEnum.DECIMAL, ColumnUsageEnum.Y_AXIS, 1);
    }//GEN-LAST:event_addColumnButtonActionPerformed

    private void deleteColumnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteColumnButtonActionPerformed
        final int rowIndex = _columnsTable.getSelectedRow();
		if (rowIndex > 1) {
			((DefaultTableModel) _columnsTable.getModel()).removeRow(rowIndex);
		}
    }//GEN-LAST:event_deleteColumnButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
		_fc.setDialogTitle(getBundle("strings").getString("dialog.datasetup.savedialog.title"));
		_fc.setDialogType(JFileChooser.SAVE_DIALOG);
		if (JFileChooser.APPROVE_OPTION == _fc.showSaveDialog(this)) {
			try (final FileOutputStream fos = new FileOutputStream(_fc.getSelectedFile())) {
				new XStream().toXML(getConfig(), fos);
				Preferences.userNodeForPackage(getClass()).put(PREFERENCES_LAST_DIRECTORY, _fc.getCurrentDirectory().getAbsolutePath());
			} catch (IOException ex) {
				//TODO: error dialog
				Logger.getLogger(DataSetupPanel.class.getName()).log(Level.WARNING, null, ex);
			}
		}
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
		_fc.setDialogTitle(getBundle("strings").getString("dialog.datasetup.loaddialog.title"));
		_fc.setDialogType(JFileChooser.OPEN_DIALOG);
		_fc.setCurrentDirectory(new File(Preferences.userNodeForPackage(getClass()).get(PREFERENCES_LAST_DIRECTORY, ".")));
		if (JFileChooser.APPROVE_OPTION == _fc.showOpenDialog(this)) {
			try (final FileInputStream fis = new FileInputStream(_fc.getSelectedFile())) {
				final Configuration config = (Configuration) new XStream().fromXML(fis);
				init(config);
				Preferences.userNodeForPackage(getClass()).put(PREFERENCES_LAST_DIRECTORY, _fc.getCurrentDirectory().getAbsolutePath());
			} catch (IOException ex) {
				//TODO: error dialog
				Logger.getLogger(DataSetupPanel.class.getName()).log(Level.WARNING, null, ex);
			}
		}
    }//GEN-LAST:event_loadButtonActionPerformed

    private void columnConfigTableModelTableChanged(javax.swing.event.TableModelEvent evt) {//GEN-FIRST:event_columnConfigTableModelTableChanged
        if (evt.getColumn() == 3 && evt.getType() == TableModelEvent.UPDATE) {
			final ColumnConfigTableModel tm = (ColumnConfigTableModel) evt.getSource();
			if (tm.getValueAt(evt.getFirstRow(), evt.getColumn()) == ColumnUsageEnum.X_AXIS) {
				for (int i = 0; i < tm.getRowCount(); i++) {
					if (i != evt.getFirstRow()) {
						if (tm.getValueAt(i, 3) == ColumnUsageEnum.X_AXIS) {
							tm.setValueAt(ColumnUsageEnum.SKIP, i, 3);
						}
					}
				}
			}
		}
    }//GEN-LAST:event_columnConfigTableModelTableChanged
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JTable _columnsTable = new javax.swing.JTable();
    private final javax.swing.JFileChooser _fc = new javax.swing.JFileChooser();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}