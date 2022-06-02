package org.isf.opdchronic.gui;

import org.isf.opdchronic.manager.OpdChronicManager;
import org.isf.opdchronic.model.OpdChronicHistoryRow;
import org.joda.time.DateTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by nicosalvato on 2016-10-07.
 * Contact: nicosalvato@gmail.com
 */
public class OpdChronicHistoryPanel extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private OpdChronicManager opdChronicManager = OpdChronicManager.getInstance();
    int patientCode;
    Date date;
    private DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
    private JTable table;
    private int[] columnWidthPercentage = { 10, 15, 15, 40, 20};

    public OpdChronicHistoryPanel(JDialog owner, Integer patientCode, Date date) {
        super(owner, true);
        this.patientCode = patientCode;
        this.date = date;
        initialize();
        resizeColumns();
    }

    private void initialize() {
        this.setContentPane(getMainPanel());
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setLocationRelativeTo(null);
        this.setTitle("Chronic Opd History");
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private JPanel getMainPanel() {
        JPanel panel = new JPanel();
        panel.add(getHistoryTablePanel());
        return panel;
    }

    public JScrollPane getHistoryTablePanel() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(1000, 625));  // Rapporto aureo: non si scherza un cazzo
        scrollPane.setViewportView(getHistoryTable());
        return scrollPane;
    }

    private JTable getHistoryTable() {
        table = new JTable();
        HistoryTableModel tableModel = new HistoryTableModel();
        table.setModel(tableModel);
        table.setRowHeight(tableModel.getOverallRowHeight());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MultilineCellRenderer multilineCellRenderer = new MultilineCellRenderer();
        table.setDefaultRenderer(String.class, multilineCellRenderer);
        return table;
    }

    private void resizeColumns() {
        int tableWidth = table.getWidth();
        TableColumn column;
        TableColumnModel jTableColumnModel = table.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = jTableColumnModel.getColumn(i);
            int pWidth = columnWidthPercentage[i] * tableWidth;
            column.setPreferredWidth(pWidth);
        }
    }

    class HistoryTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;
        private final Class<?>[] COL_CLASSES = { String.class, String.class, String.class, String.class, String.class };
        private final String[] COL_NAMES = { "Date", "Diseases", "Visit Params", "Therapy", "Notes" };
        private List<OpdChronicHistoryRow> opdChronicHistoryRows;
        private static final int SINGLE_ROW_HEIGHT = 25;
        private int overallRowHeight = 25;

        HistoryTableModel() {
            super();
            opdChronicHistoryRows = opdChronicManager.getOpdHistoryByPatientAndDate(patientCode, date);
            getHeight();
        }

        public Object getValueAt(int r, int c) {
            OpdChronicHistoryRow opdChronicHistoryRow = opdChronicHistoryRows.get(r);
            switch(c) {
                case 0:
                    return currentDateFormat.format(opdChronicHistoryRow.getVisitDate()) +
                            (opdChronicHistoryRow.getScheduledVisitDate() != null ?
                                    ("\n(" + opdChronicManager.onSchedule(
                                            new DateTime(opdChronicHistoryRow.getScheduledVisitDate()),
                                            new DateTime(opdChronicHistoryRow.getVisitDate())) + ")") : "");
                case 1:
                    return opdChronicHistoryRow.getDiseases();
                case 2:
                    return opdChronicHistoryRow.getVisitParams();
                case 3:
                    return opdChronicHistoryRow.getTherapy();
                case 4:
                    return opdChronicHistoryRow.getNotes();
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return COL_CLASSES[columnIndex];
        }

        public int getColumnCount() {
            return COL_CLASSES.length;
        }

        public int getRowCount() {
            return opdChronicHistoryRows != null ? opdChronicHistoryRows.size() : 0;
        }

        public String getColumnName(int columnIndex) {
            return COL_NAMES[columnIndex];
        }

        public void getHeight() {
            List<Integer> rowHeights = new ArrayList<Integer>(opdChronicHistoryRows.size());
            for (OpdChronicHistoryRow opdChronicHistoryRow : opdChronicHistoryRows) {
                // Compute the number of lines needed for a row based on the cell with the longer content
                int rowHeightCandidate = getNumberOfLineInCell(opdChronicHistoryRow.getDiseases());
                if (getNumberOfLineInCell(opdChronicHistoryRow.getVisitParams()) > rowHeightCandidate)
                    rowHeightCandidate = getNumberOfLineInCell(opdChronicHistoryRow.getVisitParams());
                if (getNumberOfLineInCell(opdChronicHistoryRow.getTherapy()) > rowHeightCandidate)
                    rowHeightCandidate = getNumberOfLineInCell(opdChronicHistoryRow.getTherapy());
                if (getNumberOfLineInCell(opdChronicHistoryRow.getNotes()) > rowHeightCandidate)
                    rowHeightCandidate = getNumberOfLineInCell(opdChronicHistoryRow.getNotes());

                rowHeights.add(rowHeightCandidate);
            }
            // Choose the height of all the rows based on the row that requires more space.
            // All rows will have the same height.
            overallRowHeight = Collections.max(rowHeights) * SINGLE_ROW_HEIGHT;
        }

        private int getNumberOfLineInCell(String content) {
            // count new line occurrences in a string to determine the number of lines in every cell
            if (content == null || content.isEmpty())
                return 0;
            return content.length() - content.replace("\n", "").length() + 1;
        }

        public int getOverallRowHeight() {
            return overallRowHeight;
        }
    }

    class MultilineCellRenderer extends JTextArea implements TableCellRenderer {

        private static final long serialVersionUID = 7568290417176011495L;

        public MultilineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setFont(table.getFont());
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column)) {
                    setForeground(UIManager.getColor("Table.focusCellForeground"));
                    setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else {
                setBorder(new EmptyBorder(1, 2, 1, 2));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
