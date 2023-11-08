package com.krymlov.local.database;

import com.krymlov.local.database.parts.datatypes.*;
import com.krymlov.local.database.IO.DatabaseExporter;
import com.krymlov.local.database.IO.DatabaseImporter;
import com.krymlov.local.database.gui.DBMS;
import com.krymlov.local.database.gui.CustomTable;
import com.krymlov.local.database.gui.CustomTableModel;
import com.krymlov.local.database.parts.Column;
import com.krymlov.local.database.parts.Database;
import com.krymlov.local.database.parts.Row;
import com.krymlov.local.database.parts.Table;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    public static DBMS instanceCSW;

    private DatabaseManager(){
    }

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
            instanceCSW = DBMS.getInstance();
        }
        return instance;
    }

    public static Database database;

    public void openDB(String path){
        DatabaseImporter.importDatabase(path);
    }

    public void renameDB(String name){
        if (name != null && !name.isEmpty()) {
            database.setName(name);
            instanceCSW.databaseLabel.setText(database.name);
        }
    }

    public void saveDB(String path) {
        DatabaseExporter.exportDatabase(database, path);
    }

    public void deleteDB() {
        database = null;
        while (instanceCSW.tabbedPane.getTabCount() > 0) {
            instanceCSW.tabbedPane.removeTabAt(0);
        }
    }

    public void createDB(String name) {
        database = new Database(name);
        instanceCSW.databaseLabel.setText(database.name);
    }

    public boolean existDB(){
        return database != null;
    }

    public void addTable(String name){
        if (name != null && !name.isEmpty()) {
            JPanel tablePanel = instanceCSW.createTablePanel();

            DBMS.getInstance().tabbedPane.addTab(name, tablePanel);
            Table table = new Table(name);
            database.addTable(table);
        }
    }

    public void renameTable(int tableIndex, String name){
        if (name != null && !name.isEmpty() && tableIndex != -1) {
            instanceCSW.tabbedPane.setTitleAt(tableIndex,name);
            database.tables.get(tableIndex).setName(name);
        }
    }

    public void deleteTable(int tableIndex){

        if (tableIndex != -1) {
            instanceCSW.tabbedPane.removeTabAt(tableIndex);

            database.deleteTable(tableIndex);
        }
    }

    public void addColumn(int tableIndex, String columnName, ColumnType columnType) {
        addColumn(tableIndex, columnName, columnType, "", ""); // Default min and max
    }

    public void addColumn(int tableIndex, String columnName, ColumnType columnType, String min, String max) {
        if (columnName != null && !columnName.isEmpty()) {
            if (tableIndex != -1) {
                JPanel tablePanel = (JPanel) instanceCSW.tabbedPane.getComponentAt(tableIndex);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                if(!min.equals("") && !max.equals("")){
                    tableModel.addColumn(columnName + " (" + columnType.name() + ")" + "(" + min + ":" + max + ")");
                } else {
                    tableModel.addColumn(columnName + " (" + columnType.name() + ")");
                }

                switch (columnType) {
                    case INT -> {
                        Column columnInt = new IntegerColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnInt);
                    }
                    case REAL -> {
                        Column columnReal = new RealColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnReal);
                    }
                    case STRING -> {
                        Column columnStr = new StringColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnStr);
                    }
                    case CHAR -> {
                        Column columnChar = new CharColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnChar);
                    }
                    case MONEY -> {
                        Column columnMoney = new MoneyColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnMoney);
                    }
                    case MONEY_INVL -> {
                        Column columnMoneyInvl = new MoneyInvlColumn(columnName, min, max);
                        database.tables.get(tableIndex).addColumn(columnMoneyInvl);
                    }
                }
                for (Row row : database.tables.get(tableIndex).rows) {
                    row.values.add("");
                }
            }
        }
    }


    public void renameColumn(int tableIndex, int columnIndex, String oldColumnName, String newColumnName, JTable table){
        if (newColumnName != null && !newColumnName.isEmpty()) {
            if (tableIndex != -1 && columnIndex != -1) {
                TableColumn column = table.getColumnModel().getColumn(columnIndex);
                column.setHeaderValue(column.getHeaderValue().toString().replace(oldColumnName, newColumnName));
                table.getTableHeader().repaint();

                database.tables.get(tableIndex).columns.get(columnIndex).setName(newColumnName);
            }
        }
    }

    public void changeColumnType(int tableIndex, int columnIndex, ColumnType columnType, JTable table){
        changeColumnType(tableIndex, columnIndex, columnType, table, "", "");
    }

    public void changeColumnType(int tableIndex, int columnIndex, ColumnType columnType, JTable table, String min, String max){
        if (tableIndex != -1 && columnIndex != -1) {

            String name = database.tables.get(tableIndex).columns.get(columnIndex).name;
            TableColumn column1 = table.getColumnModel().getColumn(columnIndex);
            if(!min.equals("") && !max.equals("")){
                column1.setHeaderValue(name + " (" + columnType.name() + ")" + "(" + min + ":" + max + ")");
            } else {
                column1.setHeaderValue(name + " (" + columnType.name() + ")");
            }

            table.getTableHeader().repaint();


            switch (columnType) {
                case INT -> {
                    Column columnInt = new IntegerColumn(database.tables.get(tableIndex).columns.get(columnIndex).name);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnInt);
                }
                case REAL -> {
                    Column columnReal = new RealColumn(database.tables.get(tableIndex).columns.get(columnIndex).name);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnReal);
                }
                case STRING -> {
                    Column columnStr = new StringColumn(database.tables.get(tableIndex).columns.get(columnIndex).name);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnStr);
                }
                case CHAR -> {
                    Column columnChar = new CharColumn(database.tables.get(tableIndex).columns.get(columnIndex).name);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnChar);
                }
                case MONEY -> {
                    Column columnMoney = new MoneyColumn(database.tables.get(tableIndex).columns.get(columnIndex).name);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnMoney);
                }
                case MONEY_INVL -> {
                    Column column = database.tables.get(tableIndex).columns.get(columnIndex);
//                    String name = column.name + "(" + min + ":" + max + ")";
                    Column columnMoneyInvl = new MoneyInvlColumn(column.name, min, max);
                    database.tables.get(tableIndex).columns.set(columnIndex, columnMoneyInvl);
                }
            }
            for (Row row: database.tables.get(tableIndex).rows) {
                row.values.set(columnIndex,"");
            }
            for (int i = 0; i < database.tables.get(tableIndex).rows.size(); i++) {
                table.setValueAt("", i, columnIndex);
            }
        }
    }

    public void deleteColumn(int tableIndex, int columnIndex, CustomTableModel tableModel){

        if (columnIndex != -1) {
            tableModel.removeColumn(columnIndex);
            database.tables.get(tableIndex).deleteColumn(columnIndex);
        }
    }

    public void addRow(int tableIndex, Row row){

        if (tableIndex != -1) {
            JPanel tablePanel = (JPanel) instanceCSW.tabbedPane.getComponentAt(tableIndex);
            JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            CustomTableModel tableModel = (CustomTableModel) table.getModel();
            tableModel.addRow(new Object[tableModel.getColumnCount()]);

            database.tables.get(tableIndex).addRow(row);
            System.out.println(row.values);
        }
    }

    public void deleteRow(int tableIndex, int rowIndex, CustomTableModel tableModel){

        if (rowIndex != -1) {
            tableModel.removeRow(rowIndex);

            database.tables.get(tableIndex).deleteRow(rowIndex);
        }
    }

    public void updateCellValue(String value, int tableIndex, int columnIndex, int rowIndex, CustomTable table){
        if (database.tables.get(tableIndex).columns.get(columnIndex).validate(value)){
            database.tables.get(tableIndex).rows.get(rowIndex).setAt(columnIndex,value.trim());
        }
        else {
            String data = database.tables.get(tableIndex).rows.get(rowIndex).getAt(columnIndex);
            if (data != null){
                table.setValueAt(data, rowIndex, columnIndex);
            }
            else {
                table.setValueAt("", rowIndex, columnIndex);
            }

            JFrame frame = new JFrame("Error!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JOptionPane.showMessageDialog(
                    frame,
                    "Invalid input data",
                    "Error!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private boolean evaluateCondition(String columnValue, String operator, String inputValue, Column column) {
        ColumnType columnType = ColumnType.valueOf(column.getType());

        if(columnValue == null || columnValue.equals("")) return false;

        // Handle different data types
        switch (columnType) {
            case INT: {
                int columnIntValue = Integer.parseInt(columnValue);
                int inputIntValue = Integer.parseInt(inputValue);
                return compareIntegers(columnIntValue, inputIntValue, operator);
            }
            case REAL: {
                double columnDoubleValue = Double.parseDouble(columnValue);
                double inputDoubleValue = Double.parseDouble(inputValue);
                return compareDoubles(columnDoubleValue, inputDoubleValue, operator);
            }
            case STRING:
                return compareStrings(columnValue, inputValue, operator);
            case CHAR:
                return compareChars(columnValue, inputValue, operator);
            case MONEY: {
                double columnMoneyValue = MoneyColumn.toDouble(columnValue);
                double inputMoneyValue = MoneyColumn.toDouble(inputValue);
                return compareDoubles(columnMoneyValue, inputMoneyValue, operator);
            }
            case MONEY_INVL: {
                double columnMoneyValue = MoneyColumn.toDouble(columnValue);
                double inputMoneyValue = MoneyColumn.toDouble(inputValue);
                return compareDoubles(columnMoneyValue, inputMoneyValue, operator);
            }
            default:
                // Handle unknown data type or invalid operator
                return false;
        }
    }

    private boolean compareIntegers(int columnValue, int inputValue, String operator) {
        switch (operator) {
            case ">":
                return columnValue > inputValue;
            case "<":
                return columnValue < inputValue;
            case ">=":
                return columnValue >= inputValue;
            case "<=":
                return columnValue <= inputValue;
            case "==":
                return columnValue == inputValue;
            default:
                return false;
        }
    }

    private boolean compareDoubles(double columnValue, double inputValue, String operator) {
        switch (operator) {
            case ">":
                return columnValue > inputValue;
            case "<":
                return columnValue < inputValue;
            case ">=":
                return columnValue >= inputValue;
            case "<=":
                return columnValue <= inputValue;
            case "==":
                return Double.compare(columnValue, inputValue) == 0;
            default:
                // Handle an invalid operator
                return false;
        }
    }

    private boolean compareStrings(String columnValue, String inputValue, String operator) {
        switch (operator) {
            case "==":
                return columnValue.equals(inputValue);
            case "!=":
                return !columnValue.equals(inputValue);
            case ">":
                return columnValue.compareTo(inputValue) > 0;
            case "<":
                return columnValue.compareTo(inputValue) < 0;
            case ">=":
                return columnValue.compareTo(inputValue) >= 0;
            case "<=":
                return columnValue.compareTo(inputValue) <= 0;
            default:
                // Handle an invalid operator
                return false;
        }
    }
    private boolean compareChars(String columnValue, String inputValue, String operator) {
        if (columnValue.length() != 1 || inputValue.length() != 1) {
            // Handle invalid input (not single characters)
            return false;
        }

        char columnChar = columnValue.charAt(0);
        char inputChar = inputValue.charAt(0);

        switch (operator) {
            case "==":
                return columnChar == inputChar;
            case "!=":
                return columnChar != inputChar;
            case ">":
                return columnChar > inputChar;
            case "<":
                return columnChar < inputChar;
            case ">=":
                return columnChar >= inputChar;
            case "<=":
                return columnChar <= inputChar;
            default:
                // Handle an invalid operator
                return false;
        }
    }

    public List<Row> projection(int selectedTab, CustomTableModel tableModel, Column column, String operator, String inputValue) {
        List<Row> resultRows = new ArrayList<>();

        List<Row> rows = database.tables.get(selectedTab).rows;
        List<Column> columns = database.tables.get(selectedTab).columns;

        addTable(column.getName() + " " + operator + " " + inputValue);
        int filteredTableInd = database.tables.size() - 1;

        for (Column selectedColumn : columns) {
            if (ColumnType.valueOf(selectedColumn.type).equals(ColumnType.MONEY_INVL)) {
                String min = ((MoneyInvlColumn) selectedColumn).getMin();
                String max = ((MoneyInvlColumn) selectedColumn).getMax();

                addColumn(filteredTableInd,selectedColumn.getName(),ColumnType.valueOf(selectedColumn.type), min, max);
            } else{
                addColumn(filteredTableInd,selectedColumn.name, ColumnType.valueOf(selectedColumn.type));
            }
        }

        for (Row row : rows) {
            String columnValue = row.values.get(columns.indexOf(column)).toString();
            boolean meetsCondition = evaluateCondition(columnValue, operator, inputValue, column);

            if (meetsCondition) {
                Row newRow = new Row();
                newRow.values.addAll(row.values);
                addRow(filteredTableInd, row);
                resultRows.add(newRow);
            }

        }

        DBMS.getInstance().renderCells();

        return resultRows;
    }
}
