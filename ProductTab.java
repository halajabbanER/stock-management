 package com.mycompany.stock.management;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


public class ProductTab {
    JScrollPane scrollPane;
    DefaultTableModel model;
    JTextField deleteField;
    JButton deleteButton;
    JButton viewOrdersButton;

    public ProductTab() {
        // Define column names
        String[] columnNames = {"id", "Product Name", "Category", "Quantity", "Availability", "Price", "Color", "Delivery Terms"};
        String[] dbColumnNames = {"id", "name", "category", "quantity", "availability", "price", "color", "delivery_terms"};

        // Create a DefaultTableModel with no data initially
        model = new DefaultTableModel(columnNames, 0);

        // Fetch initial data from the database
        fetchDataAndUpdateModel();

        // Create a JTable with the DefaultTableModel
        JTable table = new JTable(model);

        // Allow cell editing
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));

        // Listen for cell edits to update the database
        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0) {
                int id = (int) table.getValueAt(row, 0);
                String fieldName = dbColumnNames[column];
                Object data = table.getValueAt(row, column);
                updateDatabase(id, fieldName, data);
            }
        });

        // Create a JScrollPane and add the table to it
        scrollPane = new JScrollPane(table);

        // Create input field and button for deleting a column
        deleteField = new JTextField(5);
        deleteButton = new JButton("Delete Column");

        deleteButton.addActionListener(e -> {
            int columnId;
            try {
                columnId = Integer.parseInt(deleteField.getText());
                if (columnId >= 0 && columnId < table.getColumnCount()) {
                    int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the column: " + columnId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirmation == JOptionPane.YES_OPTION) {
                        deleteColumnFromDatabase(columnId); // Update the database
                        fetchDataAndUpdateModel();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid column ID");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer");
            }
        });

        // Create a button for viewing orders

        // Create a panel for the input field, delete button, and view orders button
        JPanel panel = new JPanel();
        panel.add(new JLabel("Column ID to delete:"));
        panel.add(deleteField);
        panel.add(deleteButton);
 

        // Create the main panel to hold everything
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a frame to display the main panel
        JFrame frame = new JFrame("Product Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    // Method to fetch data from the database and update the model
    private void fetchDataAndUpdateModel() {
        try {
             String url = "jdbc:mysql://localhost:3306/hala";
                String username = "admin";
                String password = "password";
                Connection conn = DriverManager.getConnection(url, username, password);

                if(!tableExists(conn, "stock")){
                    System.out.println("table not exist");
                    return;
                }
            String selectSQL = "SELECT id, name, category, quantity, availability, color, delivery_terms, price FROM stock";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectSQL);
            model.setRowCount(0);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                String availability = resultSet.getString("availability");
                String price = resultSet.getString("price");
                String color = resultSet.getString("color");
                String deliveryTerms = resultSet.getString("delivery_terms");

                model.addRow(new Object[]{id, productName, category, quantity, availability, price, color, deliveryTerms});
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

    // Method to refresh the table
    public void refreshTable() {
        fetchDataAndUpdateModel();
    }

    private void updateDatabase(int id, String fieldName, Object data) {
        try {
          String url = "jdbc:mysql://localhost:3306/hala";
                String username = "admin";
                String password = "password";
            Connection conn = DriverManager.getConnection(url, username, password);
            String updateSQL = "UPDATE stock SET " + fieldName + " = ? WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateSQL);
            preparedStatement.setObject(1, data);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
        } catch (SQLException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

    private void deleteColumnFromDatabase(int columnId) {
        try {
            String url = "jdbc:mysql://localhost:3306/hala";
                String username = "admin";
                String password = "password";
            Connection conn = DriverManager.getConnection(url, username, password);
            String deleteSQL = "DELETE FROM stock where id ="+ columnId; // SQL to drop column
            Statement statement = conn.createStatement();
            statement.executeUpdate(deleteSQL);
            statement.close();
            conn.close();
        } catch (SQLException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

}
