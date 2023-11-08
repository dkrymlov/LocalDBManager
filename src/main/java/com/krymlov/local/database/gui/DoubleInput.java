package com.krymlov.local.database.gui;

import javax.swing.*;

public class DoubleInput {
   private JTextField xField;
   private JTextField yField;

   public DoubleInput() {
      xField = new JTextField(5);
      yField = new JTextField(5);
   }

   public InputResult showInputDialog() {
      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("min:"));
      myPanel.add(xField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("max:"));
      myPanel.add(yField);

      while (true) {
         int result = JOptionPane.showConfirmDialog(null, myPanel,
                 "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);

         if (result == JOptionPane.OK_OPTION) {
            String xValue = xField.getText();
            String yValue = yField.getText();

            if (validate(xValue) && validate(yValue)) {
               return new InputResult(xValue, yValue);
            } else {
               JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid values.");
            }
         } else {
            // User canceled or closed the dialog
            return null;
         }
      }
   }

   public static class InputResult {
      private String min;
      private String max;

      public InputResult(String min, String max) {
         this.min = min;
         this.max = max;
      }

      public String getMin() {
         return min;
      }

      public String getMax() {
         return max;
      }
   }

   // Validation method
   public boolean validate(String data) {
      if (data == null || data.isEmpty()) {
         return false;
      }

      data = data.replace(",", "");

      try {
         double amount = Double.parseDouble(data);

         if (amount >= 0 && amount <= 10_000_000_000_000.00) {
            String[] parts = data.split("\\.");
            return parts.length == 2 && parts[1].length() == 2;
         }
      } catch (NumberFormatException ignored) {
      }

      return false;
   }

   public static void main(String[] args) {
      DoubleInput dialog = new DoubleInput();
      InputResult result = dialog.showInputDialog();
      if (result != null) {
         System.out.println("x value: " + result.getMin());
         System.out.println("y value: " + result.getMax());
      }
   }
}
