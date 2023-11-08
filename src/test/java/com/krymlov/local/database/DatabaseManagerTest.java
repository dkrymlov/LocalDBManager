package com.krymlov.local.database;

import static org.junit.jupiter.api.Assertions.*;

import com.krymlov.local.database.parts.Row;
import com.krymlov.local.database.parts.datatypes.ColumnType;
import com.krymlov.local.database.gui.CustomTable;
import com.krymlov.local.database.gui.CustomTableModel;
import com.krymlov.local.database.gui.DBMS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class DatabaseManagerTest {
    private DatabaseManager dbManager;

    @BeforeEach
    public void setUp() {
        dbManager = DatabaseManager.getInstance();
        DBMS.getInstance();
        DBMS.getInstance().databaseLabel = new JLabel("databaseName", SwingConstants.CENTER);
        // Initialize your database or perform other setup here if needed
    }

    @Test
    public void testCreateDB() {
        dbManager.createDB("TestDB");
        assertTrue(dbManager.existDB());
        Assertions.assertEquals("TestDB", dbManager.database.name);
    }

    @Test
    public void testRenameDB() {
        dbManager.createDB("TestDB");
        dbManager.renameDB("NewName");
        Assertions.assertEquals("NewName", dbManager.database.name);
    }

    @Test
    public void testDeleteDB() {
        dbManager.createDB("TestDB");
        dbManager.deleteDB();
        assertNull(dbManager.database);
        assertEquals(0, dbManager.instanceCSW.tabbedPane.getTabCount());
    }

    @Test
    public void testAddTable() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        Assertions.assertEquals(1, dbManager.database.tables.size());
        assertEquals("Table1", dbManager.database.tables.get(0).name);
    }

    @Test
    public void testRenameTable() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.renameTable(0, "NewTable1");
        assertEquals("NewTable1", dbManager.database.tables.get(0).name);
    }

    @Test
    public void testDeleteTable() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addTable("Table2");
        dbManager.deleteTable(0);
        Assertions.assertEquals(1, dbManager.database.tables.size());
        assertEquals("Table2", dbManager.database.tables.get(0).name);
    }

    @Test
    public void testAddColumn() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addColumn(0, "Column1", ColumnType.INT);
        assertEquals(1, dbManager.database.tables.get(0).columns.size());
        assertEquals("Column1", dbManager.database.tables.get(0).columns.get(0).name);
    }

    @Test
    public void testChangeColumnType() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addColumn(0, "Column1", ColumnType.INT);

        JPanel tablePanel = (JPanel) DBMS.getInstance().tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
        JTable table = (JTable) scrollPane.getViewport().getView();
        dbManager.changeColumnType(0, 0, ColumnType.REAL, table); // Pass null for JTable in this test
        assertEquals(ColumnType.REAL.name(), dbManager.database.tables.get(0).columns.get(0).type);
    }

    @Test
    public void testDeleteColumn() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addColumn(0, "Column1", ColumnType.INT);
        dbManager.addColumn(0, "Column2", ColumnType.STRING);

        JPanel tablePanel = (JPanel) DBMS.getInstance().tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
        JTable table = (JTable) scrollPane.getViewport().getView();
        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        dbManager.deleteColumn(0, 0, tableModel); // Pass null for CustomTableModel in this test
        assertEquals(1, dbManager.database.tables.get(0).columns.size());
        assertEquals("Column2", dbManager.database.tables.get(0).columns.get(0).name);
    }

    @Test
    public void testAddRow() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        Row row = new Row();
        row.values.add("Value1");
        dbManager.addRow(0, row);
        assertEquals(1, dbManager.database.tables.get(0).rows.size());
        assertEquals("Value1", dbManager.database.tables.get(0).rows.get(0).values.get(0));
    }

    @Test
    public void testDeleteRow() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addTable("Table2");
        Row row1 = new Row();
        row1.values.add("Value1");
        Row row2 = new Row();
        row2.values.add("Value2");
        dbManager.addRow(0, row1);
        dbManager.addRow(1, row2);

        JPanel tablePanel = (JPanel) DBMS.getInstance().tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
        JTable table = (JTable) scrollPane.getViewport().getView();
        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        dbManager.deleteRow(0, 0, tableModel); // Pass null for CustomTableModel in this test
        assertEquals(0, dbManager.database.tables.get(0).rows.size());
        assertEquals(1, dbManager.database.tables.get(1).rows.size());
        assertEquals("Value2", dbManager.database.tables.get(1).rows.get(0).values.get(0));
    }

    @Test
    public void testUpdateCellValue() {
        dbManager.createDB("TestDB");
        dbManager.addTable("Table1");
        dbManager.addColumn(0, "Column1", ColumnType.INT);
        Row row = new Row();
        row.values.add("");
        dbManager.addRow(0, row);

        JPanel tablePanel = (JPanel) DBMS.getInstance().tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
        CustomTable table = (CustomTable) scrollPane.getViewport().getView();
        dbManager.updateCellValue("123", 0, 0, 0, table); // Pass null for CustomTable in this test
        assertEquals("123", dbManager.database.tables.get(0).rows.get(0).values.get(0));
    }

}
