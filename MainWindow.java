package com.mycompany.stock.management;
import javax.swing.*;

public class MainWindow {

    private JFrame frame;
    
        private ProductTab productTab = new ProductTab();

    
    private CreateProductTab createProductTab = new CreateProductTab(productTab);
    

    
    public MainWindow() {
        // Create a JFrame (window)
        JFrame frame = new JFrame("My Swing Application");
        // Set the size of the window
        frame.setSize(700, 800);
   
        
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create a JPanel
        JPanel panel = new JPanel();
        panel.add(new JLabel("This is a JPanel"));
        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        // Add the table to a scroll pan
      tabbedPane.addTab("products list",productTab.scrollPane);
       tabbedPane.addTab("create product", createProductTab.panel);


        tabbedPane.setSize(700, 800);
        frame.add(panel);
        frame.add(tabbedPane);
        // Make the window visible
        frame.setVisible(true);
    }

    public void show(){
        frame.setVisible(true);
    }
}
