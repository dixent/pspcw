import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class MainWindow {

  Object[][] data;
  JFrame window, newComputer, editComputerWindow, filterWindow;
  JButton addComputer, editComputer, deleteComputer, saveComputer, createComputer, updateComputer, buyComputer, exitButton, myOrders, shop, usersButton, searchButton, filterButton, saveFilter;
  JTable table;
  DefaultTableModel tableModel;

  String[] userColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor", "Price" };
  String[] adminColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor", "Active", "User ID", "Price" };
  String[][] rows;
  JTextField model, videocard, ram, memory, processor, active, price,
    edit_model, edit_videocard, edit_ram, edit_memory, edit_processor, edit_active, edit_price, searchField, 
    filter_model, filter_videocard, filter_ram, filter_memory, filter_processor, filter_active, filter_price;
  JScrollPane scrollPane;
  JMenuBar adminMenu, userMenu;

  String userData; 

  public MainWindow(String login, String password) {
    userData = "login:" + login + ",password:" + password;
    initWindow();
    initNewComputer();
    initEditComputerWindow();
    initFilterWindow();
  }

  private void initFilterWindow() {
    filterWindow = new JFrame("Filter");
    filterWindow.setSize(500, 400);
    filterWindow.setLayout(new GridLayout(8, 2));

    filterWindow.add(new JLabel("Model - "));
    filter_model = new JTextField();
    filterWindow.add(filter_model);

    filterWindow.add(new JLabel("Video card - "));
    filter_videocard = new JTextField();
    filterWindow.add(filter_videocard);

    filterWindow.add(new JLabel("RAM - "));
    filter_ram = new JTextField();
    filterWindow.add(filter_ram);

    filterWindow.add(new JLabel("Memory - "));
    filter_memory = new JTextField();
    filterWindow.add(filter_memory);

    filterWindow.add(new JLabel("Processor - "));
    filter_processor = new JTextField();
    filterWindow.add(filter_processor);

    filterWindow.add(new JLabel("Price($) - "));
    filter_price = new JTextField();
    filterWindow.add(filter_price);

    saveFilter = new JButton("Save filter");
    saveFilter.addActionListener(new ButtonListener());
    filterWindow.add(saveFilter);
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

    usersButton = new JButton("Users");
    usersButton.addActionListener(new ButtonListener());

    searchButton = new JButton("Search");
    searchButton.addActionListener(new ButtonListener());
    searchField = new JTextField();

    filterButton = new JButton("Filter");
    filterButton.addActionListener(new ButtonListener());

    exitButton = new JButton("Sign out");
    exitButton.addActionListener(new ButtonListener());



    adminMenu = new JMenuBar();
    adminMenu.add(addComputer);
    adminMenu.add(editComputer);
    adminMenu.add(deleteComputer);
    adminMenu.add(usersButton);
    adminMenu.add(new JLabel("Search by model: "));
    adminMenu.add(searchField);
    adminMenu.add(searchButton);
    adminMenu.add(filterButton);
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

    searchButton = new JButton("Search");
    searchButton.addActionListener(new ButtonListener());
    searchField = new JTextField();

    exitButton = new JButton("Sign out");
    exitButton.addActionListener(new ButtonListener());

    userMenu = new JMenuBar();
    userMenu.add(shop);
    userMenu.add(buyComputer);
    userMenu.add(myOrders);
    userMenu.add(new JLabel("Search by model: "));
    userMenu.add(searchField);
    userMenu.add(searchButton);
    userMenu.add(exitButton);
    window.add(BorderLayout.PAGE_START, userMenu);
  }

  private void myOrdersAction() {
    initOrders();
    String[] columns = userColumns;
    tableModel = new DefaultTableModel(rows, columns);
    
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    window.add(BorderLayout.CENTER, scrollPane);
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
    table.setAutoCreateRowSorter(true);
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
    newComputer.setLayout(new GridLayout(8, 2));

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

    newComputer.add(new JLabel("Price($) - "));
    price = new JTextField();
    newComputer.add(price);

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
    price.setText("");
  }

  private void initEditComputerWindow() {
    editComputerWindow = new JFrame("Edit");
    editComputerWindow.setSize(500, 400);
    editComputerWindow.setLayout(new GridLayout(8, 2));

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

    editComputerWindow.add(new JLabel("Price($) - "));
    edit_price = new JTextField();
    editComputerWindow.add(edit_price);

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
        case "Users":
          new UsersWindow(userData);
          break;
        case "Search":
          searchAction();
          break;
        case "Sign out":
          exitAction();
          break;
        case "Filter":
          filterWindow.setVisible(true);
          break;
        case "Save filter":
          filterWindow.setVisible(false);
          break;
      }
    }
  }

  private void searchAction() {
    initRows();
    String[][] new_rows = new String[rows.length][rows[0].length];
    String searchValue = searchField.getText();
    int i = 0;
    for(String[] row : rows) {
      if ((row[1].contains(searchValue) || row[1].contains( filter_model.getText())) && row[2].contains(filter_videocard.getText()) && row[3].contains(filter_ram.getText()) && row[4].contains(filter_memory.getText()) && row[5].contains(filter_processor.getText())) {
        new_rows[i] = row;
        i++; 
      }
    }
    while(new_rows[new_rows.length-1][0] == null) {
      new_rows = Arrays.copyOf(new_rows, new_rows.length-1);
    }
    rows = new_rows;
    String[] columns;
    if (userIsAdmin()) {
      columns = adminColumns;
    } else {
      columns = userColumns;
    }
    tableModel = new DefaultTableModel(rows, columns);
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    window.add(BorderLayout.CENTER, scrollPane);
    SwingUtilities.updateComponentTreeUI(window);
  }

  private void createAction() {
    try {
      String newComputerParams = ",model:" + model.getText() + ",videocard:" + videocard.getText() + ",ram:" + ram.getText() + ",memory:" + memory.getText() + ",processor:" + processor.getText() + ",price:" + price.getText();
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
        edit_processor.getText() + ",active:" + edit_active.getText() + ",price:" + edit_price.getText();
        UIClient.send((userData + newComputerParams + ",method:updateComputer").getBytes());
      if(new String(UIClient.get()).trim().equals("1")) {
        JOptionPane.showMessageDialog(null, "Updated successful!");
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
    edit_active.setText(selected_element[7].toString());
  }

  private void reindex() {
    initTableModel();
    for(String[] row : rows) {
      System.out.println(String.join("   ", row));
    }
    
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    window.add(BorderLayout.CENTER, scrollPane);
    SwingUtilities.updateComponentTreeUI(window);
  }
}
