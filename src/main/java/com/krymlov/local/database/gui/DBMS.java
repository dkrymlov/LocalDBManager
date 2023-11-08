package com.krymlov.local.database.gui;

import com.krymlov.local.database.DatabaseManager;
import com.krymlov.local.database.parts.Column;
import com.krymlov.local.database.parts.Row;
import com.krymlov.local.database.parts.datatypes.ColumnType;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DBMS {

    private static DBMS instance;
    private static String databaseName = "Open or create database";
    JFrame frame;
    public JTabbedPane tabbedPane;
    public JMenuBar menuBar;

    public JMenuItem renameDatabaseMenuItem;
    public JMenuItem deleteTableMenuItem;
    public JMenuItem addRowMenuItem;
    public JMenuItem addColumnMenuItem;
    public JMenuItem deleteRowMenuItem;
    public JMenuItem deleteColumnMenuItem;
    public JMenuItem createTableMenuItem;
    public JMenuItem renameTableMenuItem;
    public JMenuItem renameColumnMenuItem;
    public JMenuItem changeColumnTypeMenuItem;
    public JMenuItem loadDB;
    public JMenuItem projection;
    public JMenuItem createDB;
    public JMenuItem deleteDB;
    public JMenuItem saveDB;

    public JMenu tableMenu = new JMenu("Table");
    public JMenu columnMenu = new JMenu("Column");
    public JMenu rowMenu = new JMenu("Row");

    public JLabel databaseLabel;

    public static DBMS getInstance(){
        if (instance == null){
            instance = new DBMS();

            instance.frame = new JFrame("Local Database Manager");
            instance.menuBar = new JMenuBar();

            instance.tabbedPane = new JTabbedPane();

            instance.renameDatabaseMenuItem = new JMenuItem("Rename");
            instance.deleteTableMenuItem = new JMenuItem("Delete");
            instance.addRowMenuItem = new JMenuItem("Add");
            instance.addColumnMenuItem = new JMenuItem("Add");
            instance.deleteRowMenuItem = new JMenuItem("Delete");
            instance.deleteColumnMenuItem = new JMenuItem("Delete");
            instance.createTableMenuItem = new JMenuItem("Create");
            instance.renameTableMenuItem = new JMenuItem("Rename");
            instance.renameColumnMenuItem = new JMenuItem("Rename");
            instance.changeColumnTypeMenuItem = new JMenuItem("Change column type");
            instance.loadDB = new JMenuItem("Open");
            instance.createDB = new JMenuItem("Create");
            instance.deleteDB = new JMenuItem("Delete");
            instance.projection = new JMenuItem("Projection");
            instance.saveDB = new JMenuItem("Save");
        }
        return instance;
    }

    public static void main(String[] args) {
        DBMS instance = DBMS.getInstance();
        instance.frame.setSize(800, 600);
        instance.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        instance.setMenusVisibility(false);

        JMenu dbMenu = new JMenu("Database");
        dbMenu.add(instance.createDB);
        dbMenu.add(instance.deleteDB);
        dbMenu.add(instance.saveDB);
        dbMenu.add(instance.loadDB);

        instance.tableMenu.add(instance.createTableMenuItem);
        instance.tableMenu.add(instance.deleteTableMenuItem);
        instance.tableMenu.add(instance.projection);

        instance.columnMenu.add(instance.addColumnMenuItem);
        instance.columnMenu.add(instance.deleteColumnMenuItem);

        instance.rowMenu.add(instance.addRowMenuItem);
        instance.rowMenu.add(instance.deleteRowMenuItem);

        instance.menuBar.add(dbMenu);
        instance.menuBar.add(instance.tableMenu);
        instance.menuBar.add(instance.columnMenu);
        instance.menuBar.add(instance.rowMenu);

        DBMS.instance.frame.setJMenuBar(DBMS.instance.menuBar);

        instance.frame.getContentPane().add(DBMS.getInstance().tabbedPane, BorderLayout.CENTER);

        instance.databaseLabel = new JLabel(databaseName, SwingConstants.CENTER);
        instance.frame.getContentPane().add(instance.databaseLabel, BorderLayout.NORTH);

        instance.frame.setLocationRelativeTo(null);

        instance.frame.setVisible(true);

        instance.loadDB.addActionListener(e -> {
            String strPath = JOptionPane.showInputDialog(instance.frame, "Enter path:");
            if (strPath != null) {
                Path path = Paths.get(strPath);
                if (Files.exists(path)){
                    DatabaseManager.getInstance().openDB(strPath);
                    instance.setMenusVisibility(true);
                } else {
                    JOptionPane.showMessageDialog(DBMS.instance.frame, "Path not exist");
                }
            }
        });

        instance.renameDatabaseMenuItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(instance.frame, "Enter new database name:");
            DatabaseManager.getInstance().renameDB(newName);
        });

        instance.createTableMenuItem.addActionListener(e -> {
            String tableName = JOptionPane.showInputDialog(instance.frame, "Enter new table name::");
            DatabaseManager.getInstance().addTable(tableName);
        });

        instance.deleteTableMenuItem.addActionListener(e -> {
            int selectedIndex = instance.tabbedPane.getSelectedIndex();
            DatabaseManager.getInstance().deleteTable(selectedIndex);
        });

        // Update the ActionListener code
        instance.addColumnMenuItem.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                String newColumnName = JOptionPane.showInputDialog(instance.frame, "Enter new column name:");

                if (newColumnName != null && !newColumnName.isEmpty()) {
                    ColumnType selectedDataType = (ColumnType) JOptionPane.showInputDialog(
                            instance.frame,
                            "Pick a new col type:",
                            "Add Column",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            ColumnType.values(),
                            ColumnType.INT
                    );

                    if (selectedDataType != null) {
                        if (selectedDataType == ColumnType.MONEY_INVL) {
                            DoubleInput dialog = new DoubleInput();
                            DoubleInput.InputResult result = dialog.showInputDialog();
                            if (result != null) {
                                String min = result.getMin();
                                String max = result.getMax();
                                DatabaseManager.getInstance().addColumn(selectedTab, newColumnName, selectedDataType, min, max);
                            }
                        } else {
                            DatabaseManager.getInstance().addColumn(selectedTab, newColumnName, selectedDataType);
                        }
                    }
                }
            }
        });



        instance.deleteColumnMenuItem.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                int selectedColumn = table.getSelectedColumn();
                DatabaseManager.getInstance().deleteColumn(selectedTab, selectedColumn, tableModel);
            }
        });

        instance.addRowMenuItem.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            DatabaseManager.getInstance().addRow(selectedTab, new Row());
        });

        instance.deleteRowMenuItem.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                int selectedRow = table.getSelectedRow();
                DatabaseManager.getInstance().deleteRow(selectedTab, selectedRow, tableModel);
            }
        });

        instance.renameTableMenuItem.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                String newName = JOptionPane.showInputDialog(instance.frame, "Enter new table name:");
                DatabaseManager.getInstance().renameTable(selectedTab, newName);
            }
        });

        instance.renameColumnMenuItem.addActionListener(e -> {

            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                String newColumnName = JOptionPane.showInputDialog(instance.frame, "Enter new column name:");

                JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();

                int selectedColumn = table.getSelectedColumn();
                String oldName = DatabaseManager.database.tables.get(selectedTab).columns.get(selectedColumn).name;
                DatabaseManager.getInstance().renameColumn(selectedTab, selectedColumn, oldName, newColumnName, table);
            }

        });

        instance.changeColumnTypeMenuItem.addActionListener(e -> {

            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                ColumnType selectedDataType = (ColumnType) JOptionPane.showInputDialog(
                        instance.frame,
                        "Enter new col type:",
                        "Edit column",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ColumnType.values(),
                        ColumnType.INT
                );

                if (selectedDataType != null) {
                    JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                    JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                    JTable table = (JTable) scrollPane.getViewport().getView();
                    int selectedColumn = table.getSelectedColumn();

                    if (selectedDataType == ColumnType.MONEY_INVL) {
                        DoubleInput dialog = new DoubleInput();
                        DoubleInput.InputResult result = dialog.showInputDialog();
                        if (result != null) {
                            String min = result.getMin();
                            String max = result.getMax();
                            DatabaseManager.getInstance().changeColumnType(selectedTab, selectedColumn, selectedDataType, table, min, max);
                        }
                    } else {
                        DatabaseManager.getInstance().changeColumnType(selectedTab, selectedColumn, selectedDataType, table);
                    }
                }
            }
        });

        instance.createDB.addActionListener(e -> {
            if (!DatabaseManager.getInstance().existDB()) {
                String name = JOptionPane.showInputDialog(instance.frame, "Enter new database name:");
                if (name != null && !name.isEmpty()) {
                    DatabaseManager.getInstance().createDB(name);
                    instance.setMenusVisibility(true);
                }
            } else {
                int result = JOptionPane.showConfirmDialog(instance.frame, "Database exists. Want to create new?", "Accept database creation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    String name = JOptionPane.showInputDialog(instance.frame, "Enter new database name:");
                    if (name != null && !name.isEmpty()) {
                        DatabaseManager.getInstance().deleteDB();
                        DatabaseManager.getInstance().createDB(name);
                    }
                }
            }
        });
        instance.deleteDB.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(instance.frame, "Want to delete?", "Accept database delete", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                DatabaseManager.getInstance().deleteDB();
                instance.databaseLabel.setText("Open or create Database");
                instance.setMenusVisibility(false);

            }
        });

        instance.saveDB.addActionListener(e -> {
            String path = JOptionPane.showInputDialog(instance.frame, "Enter Path:");
            System.out.println(path);
            DatabaseManager.getInstance().saveDB(path);
        });

        instance.projection.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) instance.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                List<Column> columns = DatabaseManager.database.tables.get(selectedTab).columns;
                CustomInputDialog dialog = new CustomInputDialog(columns);
                CustomInputDialog.InputResult result = dialog.showInputDialog();
                if (result != null) {
                    Column column = result.getColumn();
                    String operator = result.getOption2();
                    String inputValue = result.getInputValue();

                    System.out.println("Option 1: " + column);
                    System.out.println("Option 2: " + operator);
                    System.out.println("Input Value: " + inputValue);

                    DatabaseManager.getInstance().projection(selectedTab, tableModel, column, operator, inputValue);
                    JOptionPane.showMessageDialog(DBMS.instance.frame, "Projection Ready!");
                }

            }
        });
    }

    public JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();

        CustomTable table = new CustomTable(model);

        DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());

        cellEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();
                Object updatedValue = table.getValueAt(selectedRow, selectedColumn);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });

        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellEditor(cellEditor);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        CustomTableModel tableModel = new CustomTableModel ();

        table.setModel(tableModel);

        return panel;
    }
    private void setMenusVisibility(boolean visible) {
        renameDatabaseMenuItem.setVisible(visible);
        instance.tableMenu.setVisible(visible);
        instance.columnMenu.setVisible(visible);
        instance.rowMenu.setVisible(visible);
        instance.deleteDB.setVisible(visible);
        instance.saveDB.setVisible(visible);
    }

    public void renderCells(){
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JPanel tablePanel = (JPanel) tabbedPane.getComponentAt(i);
            JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
            CustomTable table = (CustomTable) scrollPane.getViewport().getView();
            for (int j = 0; j < DatabaseManager.database.tables.get(i).rows.size(); j++) {
                for (int k = 0; k < DatabaseManager.database.tables.get(i).columns.size(); k++) {
                    String data = DatabaseManager.database.tables.get(i).rows.get(j).getAt(k);
                    table.setValueAt(data, j, k);
                }
            }
        }
    }
}