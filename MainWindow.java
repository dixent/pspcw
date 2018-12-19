import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class MainWindow {

  Object[][] data;
  JFrame window, newComputer, editComputerWindow;
  JButton addComputer, editComputer, deleteComputer, saveComputer, createComputer, updateComputer, buyComputer, exitButton, myOrders, shop;
  JTable table;
  DefaultTableModel tableModel;

  String[] userColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor" };
  String[] adminColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor", "Active", "User ID" };
  String[][] rows;
  JTextField model, videocard, ram, memory, processor, active,
    edit_model, edit_videocard, edit_ram, edit_memory, edit_processor, edit_active;
  JScrollPane scrollPane;
  JMenuBar adminMenu, userMenu;

  String userData; 

  public MainWindow(String login, String password) {
    userData = "login:" + login + ",password:" + password;
    initWindow();
    initNewComputer();
    initEditComputerWindow();
    
  }

  private Boolean userIsAdmin() {
    UIClient.send((userData + ",method:checkAdmin").getBytes());
    if (Integer.parseInt(new String(UIClient.get()).trim()) == 1) {
      return true;
    } else {
      return false;
    }
  }

  private void initAdminMenu() {
    addComputer = new JButton("New");
    addComputer.addActionListener(new ButtonListener());

    editComputer = new JButton("Edit");
    editComputer.addActionListener(new ButtonListener());

    deleteComputer = new JButton("Delete");
    deleteComputer.addActionListener(new ButtonListener());

    exitButton = new JButton("Sign out");
    exitButton.addActionListener(new ButtonListener());

    adminMenu = new JMenuBar();
    adminMenu.add(addComputer);
    adminMenu.add(editComputer);
    adminMenu.add(deleteComputer);
    adminMenu.add(exitButton);
    window.add(BorderLayout.PAGE_START, adminMenu);
  }

  private void initUserMenu() {
    shop = new JButton("Shop");
    shop.addActionListener(new ButtonListener());
    
    buyComputer = new JButton("Buy");
    buyComputer.addActionListener(new ButtonListener());

    myOrders = new JButton("My orders");
    myOrders.addActionListener(new ButtonListener());

    exitButton = new JButton("Sign out");
    exitButton.addActionListener(new ButtonListener());

    userMenu = new JMenuBar();
    userMenu.add(shop);
    userMenu.add(buyComputer);
    userMenu.add(myOrders);
    userMenu.add(exitButton);
    window.add(BorderLayout.PAGE_START, userMenu);
  }

  private void myOrdersAction() {
    initOrders();
    String[] columns = userColumns;
    tableModel = new DefaultTableModel(rows, columns);
    
    window.remove(table);
    table = new JTable(tableModel);
    //scrollPane = new JScrollPane(table);
    //table.setFillsViewportHeight(true);
    window.add(BorderLayout.CENTER, table);
    SwingUtilities.updateComponentTreeUI(window);
  }

  private void buyComputerAction() {
    try {
      UIClient.send((userData + ",id:" + rows[table.getSelectedRow()][0].toString() + ",method:buyComputer").getBytes());
      if(new String(UIClient.get()).trim().equals("1")) {
        JOptionPane.showMessageDialog(null, "The purchase was successful!");
      } else {
        JOptionPane.showMessageDialog(null, "Error of purchase!");
      }
      reindex();
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "Error of purchase!");
    }
    
  }

  private void exitAction() {
    window.setVisible(false);
    new Authorization();
  }
  private void initOrders(){
    UIClient.send((userData + ",method:myOrders").getBytes());
    String result = new String(UIClient.get()).trim();
    System.out.println(result);
    if (!result.equals("0")) {
      String[] strings = result.split("#");
      rows = new String[strings.length][strings[0].split("&").length];
      System.out.println(strings[0] + " " + rows.length + " " + rows[0].length);
      for(int i = 0; i < strings.length; i++) {
        String[] string = strings[i].split("&");
        for(int j = 0; j < rows[i].length; j++) {
          rows[i][j] = string[j]; 
        } 
      }
      //rows[0] = strings[0].split("|");
    } else {
      JOptionPane.showMessageDialog(null, "Something wrong with DB!");
    }
  }

  private void initRows() {
    UIClient.send((userData + ",method:indexComputers").getBytes());
    String result = new String(UIClient.get()).trim();
    System.out.println(result);
    if (!result.equals("0")) {
      String[] strings = result.split("#");
      rows = new String[strings.length][strings[0].split("&").length];
      System.out.println(strings[0] + " " + rows.length + " " + rows[0].length);
      for(int i = 0; i < strings.length; i++) {
        String[] string = strings[i].split("&");
        for(int j = 0; j < rows[i].length; j++) {
          rows[i][j] = string[j]; 
        } 
      }
      //rows[0] = strings[0].split("|");
    } else {
      JOptionPane.showMessageDialog(null, "Something wrong with DB!");
    }
  }

  private void initTableModel() {
    initRows();
    String[] columns;
    if (userIsAdmin()) {
      columns = adminColumns;
    } else {
      columns = userColumns;
    }
    tableModel = new DefaultTableModel(rows, columns);
    //tableModel
  }

  

  private void index() {

  }
  
  private void initWindow() {
    window = new JFrame("Shop");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(1000, 800);
    window.setLayout(new BorderLayout());
    if (userIsAdmin()) {
      initAdminMenu();
    } else {
      initUserMenu();
    }
    initTableModel();
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    window.add(BorderLayout.CENTER, scrollPane);

    // newComputer = new JButton("New");
    // newComputer.addActionListener(new ButtonListener());

    // editComputer = new JButton("Edit");
    // editComputer.addActionListener(new ButtonListener());

    // deleteComputer = new JButton("Delete");
    // deleteComputer.addActionListener(new ButtonListener());

    // saveComputer = new JButton("Save");

    // menu = new JMenuBar();
    // menu.add(newComputer);
    // menu.add(editComputer);
    // menu.add(deleteComputer);
    // menu.add(saveComputer);
    // window.add(BorderLayout.PAGE_START, menu);

    //table = new JTable(data, columnsName);

    //scrollPane = new JScrollPane(table);
    //table.setFillsViewportHeight(true);
    //window.add(BorderLayout.CENTER, scrollPane);
    
    window.setVisible(true);
  }

  private void initNewComputer() {
    newComputer = new JFrame("New");
    newComputer.setSize(500, 400);
    newComputer.setLayout(new GridLayout(7, 2));

    newComputer.add(new JLabel("Model - "));
    model = new JTextField();
    newComputer.add(model);

    newComputer.add(new JLabel("Video card - "));
    videocard = new JTextField();
    newComputer.add(videocard);

    newComputer.add(new JLabel("RAM - "));
    ram = new JTextField();
    newComputer.add(ram);

    newComputer.add(new JLabel("Memory - "));
    memory = new JTextField();
    newComputer.add(memory);

    newComputer.add(new JLabel("Processor - "));
    processor = new JTextField();
    newComputer.add(processor);

    createComputer = new JButton("Create");
    createComputer.addActionListener(new ButtonListener());
    newComputer.add(createComputer);
  }

  private void resetInputFields() {
    model.setText("");
    videocard.setText("");
    ram.setText("");
    memory.setText("");
    processor.setText("");
  }

  private void initEditComputerWindow() {
    editComputerWindow = new JFrame("Edit");
    editComputerWindow.setSize(500, 400);
    editComputerWindow.setLayout(new GridLayout(7, 2));

    editComputerWindow.add(new JLabel("Model - "));
    edit_model = new JTextField();
    editComputerWindow.add(edit_model);

    editComputerWindow.add(new JLabel("Video card - "));
    edit_videocard = new JTextField();
    editComputerWindow.add(edit_videocard);

    editComputerWindow.add(new JLabel("RAM - "));
    edit_ram = new JTextField();
    editComputerWindow.add(edit_ram);

    editComputerWindow.add(new JLabel("Memory - "));
    edit_memory = new JTextField();
    editComputerWindow.add(edit_memory);

    editComputerWindow.add(new JLabel("Processor - "));
    edit_processor = new JTextField();
    editComputerWindow.add(edit_processor);

    editComputerWindow.add(new JLabel("Active - "));
    edit_active = new JTextField();
    editComputerWindow.add(edit_active);

    updateComputer = new JButton("Update");
    updateComputer.addActionListener(new ButtonListener());
    editComputerWindow.add(updateComputer);
  }

  private class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      switch(e.getActionCommand()) {
        case "New": 
          newComputer.setVisible(true);
          break;
        case "Edit":
          editAction();
          if (selected_element != null) {
            editComputerWindow.setVisible(true);
          }
          break;
        case "Create":
          createAction();
          break;
        case "Update":
          updateAction();
          break;
        case "Delete":
          deleteAction();
          break;
        case "Buy":
          buyComputerAction();
          break;
        case "My orders":
          myOrdersAction();
          break;
        case "Shop":
          reindex();
          break;
        case "Sign out":
          exitAction();
          break;
      }
    }
  }

  private void createAction() {
    try {
      String newComputerParams = ",model:" + model.getText() + ",videocard:" + videocard.getText() + ",ram:" + ram.getText() + ",memory:" + memory.getText() + ",processor:" + processor.getText();
      UIClient.send((userData + newComputerParams + ",method:createComputer").getBytes());
      if(Integer.parseInt(new String(UIClient.get()).trim()) > 0) {
        JOptionPane.showMessageDialog(null, "Computer created successfully!");
        newComputer.setVisible(false);
        resetInputFields();
        reindex();
      } else { 
        JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
      }
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
    }
    
  }

  private void updateAction() {
    try {  
      String newComputerParams = ",id:" + rows[table.getSelectedRow()][0].toString() +  
        ",model:" + edit_model.getText() + ",videocard:" + edit_videocard.getText() + ",ram:" + 
        edit_ram.getText() + ",memory:" + edit_memory.getText() + ",processor:" +
        edit_processor.getText() + ",active:" + edit_active.getText() ;
        UIClient.send((userData + newComputerParams + ",method:updateComputer").getBytes());
      if(new String(UIClient.get()).trim().equals("1")) {
        JOptionPane.showMessageDialog(null, "Deleted successful!");
        editComputerWindow.setVisible(false);
        selected_element = null;
        reindex();
      } else {
        JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
      }
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
    }
  }

  private void deleteAction() {
    UIClient.send((userData + ",id:" + rows[table.getSelectedRow()][0].toString() + ",method:deleteComputer").getBytes());
    if(new String(UIClient.get()).trim().equals("1")) {
      JOptionPane.showMessageDialog(null, "Deleted successful!");
    } else {
      JOptionPane.showMessageDialog(null, "Error delete!");
    }
    reindex();
  }

  Object[] selected_element;
  private void editAction() {
    selected_element = rows[table.getSelectedRow()];
    edit_model.setText(selected_element[1].toString());
    edit_videocard.setText(selected_element[2].toString());
    edit_ram.setText(selected_element[3].toString());
    edit_memory.setText(selected_element[4].toString());
    edit_processor.setText(selected_element[5].toString());
    edit_active.setText(selected_element[6].toString());
  }

  private void reindex() {
    initTableModel();
    
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    window.add(BorderLayout.CENTER, scrollPane);
    SwingUtilities.updateComponentTreeUI(window);
  }
}
