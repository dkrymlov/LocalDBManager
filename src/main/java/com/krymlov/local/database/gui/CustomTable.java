package com.krymlov.local.database.gui;

import com.krymlov.local.database.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomTable extends JTable {

    public CustomTable(DefaultTableModel tableModel) {
        super(tableModel);
        addPropertyChangeListener(evt -> {
            if ("tableCellEditor".equals(evt.getPropertyName())) {
                if (!isEditing()){
                    processEditingStopped();
                }
            }
        });
    }

    public void processEditingStopped() {
        int editingRow = getEditingRow();
        int editingColumn = getEditingColumn();

        if (editingRow != -1 && editingColumn != -1) {
            Object newValue = getValueAt(editingRow, editingColumn);
            int selectedTab = DBMS.getInstance().tabbedPane.getSelectedIndex();
            DatabaseManager.getInstance().updateCellValue((String) newValue, selectedTab,editingColumn, editingRow, this);
        }
    }
}