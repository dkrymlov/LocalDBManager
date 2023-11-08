package com.krymlov.local.database.gui;

import com.krymlov.local.database.parts.Column;

import javax.swing.*;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class CustomInputDialog {
   List<Column> columns = new ArrayList<>();
   private JComboBox<String> column;
   private JComboBox<String> operator;
   private JTextField value;

   public CustomInputDialog(List<Column> columns) {
      this.columns = columns;
      String[] columnNames = columns.stream()
              .map(Column::getName)
              .toArray(String[]::new);
      String[] options = {">", "<", ">=", "<=", "=="};
      column = new JComboBox<>(columnNames);
      operator = new JComboBox<>(options);
      value = new JTextField(10);
   }

   public InputResult showInputDialog() {
      JPanel myPanel = new JPanel(new FlowLayout());
      myPanel.add(new JLabel("Column:"));
      myPanel.add(column);
      myPanel.add(new JLabel("Operator"));
      myPanel.add(operator);
      myPanel.add(new JLabel("Value"));
      myPanel.add(value);

      while (true) {
         int result = JOptionPane.showConfirmDialog(null, myPanel,
                 "Pick", JOptionPane.OK_CANCEL_OPTION);

         if (result == JOptionPane.OK_OPTION) {
            String selectedColumnName = (String) this.column.getSelectedItem();
            String operator = (String) this.operator.getSelectedItem();
            String inputValue = value.getText();

            Column selectedColumn = null;

            if (selectedColumnName != null) {
               for (Column col : this.columns) {
                  if (col.getName().equals(selectedColumnName)) {
                     selectedColumn = col;
                     break;  // Found the selected column, exit the loop
                  }
               }
            }

            if (selectedColumn.validate(inputValue)) {
               return new InputResult(selectedColumn, operator, inputValue);
            } else {
               JOptionPane.showMessageDialog(null, "Bad value");
            }
         } else {
            // User canceled or closed the dialog
            return null;
         }
      }
   }

   public static class InputResult {
      private Column column;
      private String option2;
      private String inputValue;

      public InputResult(Column column, String option2, String inputValue) {
         this.column = column;
         this.option2 = option2;
         this.inputValue = inputValue;
      }

      public Column getColumn() {
         return column;
      }

      public String getOption2() {
         return option2;
      }

      public String getInputValue() {
         return inputValue;
      }
   }

   // Validation method (you can customize it as needed)
   public boolean validate(String column, String data) {
      // Add your validation logic here
      return true; // Placeholder validation
   }

//   public static void main(String[] args) {
//      CustomInputDialog dialog = new CustomInputDialog();
//      InputResult result = dialog.showInputDialog();
//      if (result != null) {
//         System.out.println("Option 1: " + result.getOption1());
//         System.out.println("Option 2: " + result.getOption2());
//         System.out.println("Input Value: " + result.getInputValue());
//      }
//   }
}