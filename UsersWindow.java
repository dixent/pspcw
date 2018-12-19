import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class UsersWindow {

  JFrame window, editUserWindow;
  JButton addComputer, editUser, deleteUser, updateUser, exitButton, searchButton;
  JTable table;
  DefaultTableModel tableModel;

  String[] columns = { "Id", "Login", "Password", "Admin" };
  String[][] rows;
  JTextField edit_login, edit_password, edit_admin, searchField;
  JScrollPane scrollPane;
  JMenuBar adminMenu;

  String userData; 

  public UsersWindow(String userData) {
    this.userData = userData;
    initWindow();
    initEdit();
  }

  private void initAdminMenu() {

    editUser = new JButton("Edit");
    editUser.addActionListener(new ButtonListener());

    deleteUser = new JButton("Delete");
    deleteUser.addActionListener(new ButtonListener());

    searchButton = new JButton("Search");
    searchButton.addActionListener(new ButtonListener());
    searchField = new JTextField();

    exitButton = new JButton("Close");
    exitButton.addActionListener(new ButtonListener());

    adminMenu = new JMenuBar();
    adminMenu.add(editUser);
    adminMenu.add(deleteUser);
    adminMenu.add(new JLabel("Search by login: "));
    adminMenu.add(searchField);
    adminMenu.add(searchButton);
    adminMenu.add(exitButton);
    window.add(BorderLayout.PAGE_START, adminMenu);
  }

  private void exitAction() {
    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
  }

  private void initRows() {
    UIClient.send((userData + ",method:indexUsers").getBytes());
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

  private void initTableModel() {
    initRows();
    tableModel = new DefaultTableModel(rows, columns);
  }
  
  private void initWindow() {
    window = new JFrame("Users");
    window.setSize(800, 600);
    window.setLayout(new BorderLayout());
    initAdminMenu();
    initTableModel();
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    window.add(BorderLayout.CENTER, scrollPane);
    window.setVisible(true);
  }

  private void initEdit() {
    editUserWindow = new JFrame("Edit");
    editUserWindow.setSize(400, 250);
    editUserWindow.setLayout(new GridLayout(5, 2));

    editUserWindow.add(new JLabel("Login - "));
    edit_login = new JTextField();
    editUserWindow.add(edit_login);

    editUserWindow.add(new JLabel("Password - "));
    edit_password = new JTextField();
    editUserWindow.add(edit_password);

    editUserWindow.add(new JLabel("Admin? - "));
    edit_admin = new JTextField();
    editUserWindow.add(edit_admin);

    updateUser = new JButton("Update");
    updateUser.addActionListener(new ButtonListener());
    editUserWindow.add(updateUser);
  }

  private class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      switch(e.getActionCommand()) {
        case "Edit":
          editAction();
          if (selected_element != null) {
            editUserWindow.setVisible(true);
          }
          break;
        case "Update":
          updateAction();
          break;
        case "Delete":
          deleteAction();
          break;
        case "Search":
          searchAction();
          break;
        case "Close":
          exitAction();
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
      if (row[1].contains(searchValue)) {
        new_rows[i] = row;
        i++; 
      }
    }
    while(new_rows[new_rows.length-1][0] == null) {
      new_rows = Arrays.copyOf(new_rows, new_rows.length-1);
    }
    rows = new_rows;
    tableModel = new DefaultTableModel(rows, columns);
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    window.add(BorderLayout.CENTER, scrollPane);
    SwingUtilities.updateComponentTreeUI(window);
  }

  private void updateAction() {
    try {  
      String newUserParams = ",user_id:" + rows[table.getSelectedRow()][0].toString() +  
        ",user_login:" + edit_login.getText() + ",user_password:" + edit_password.getText() + 
        ",user_admin:" + edit_admin.getText();
      UIClient.send((userData + newUserParams + ",method:updateUser").getBytes());
      if(new String(UIClient.get()).trim().equals("1")) {
        JOptionPane.showMessageDialog(null, "Updated successful!");
        editUserWindow.setVisible(false);
        selected_element = null;
        reindex();
      } else {
        System.out.println("OOPS!");
        JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.out.println("OOPS2!");
      JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
    }
  }

  private void deleteAction() {
    UIClient.send((userData + ",user_id:" + rows[table.getSelectedRow()][0].toString() + ",method:deleteUser").getBytes());
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
    edit_login.setText(selected_element[1].toString());
    edit_password.setText(selected_element[2].toString());
  }

  private void reindex() {
    initTableModel();
    
    window.remove(scrollPane);
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    window.add(BorderLayout.CENTER, scrollPane);
    SwingUtilities.updateComponentTreeUI(window);
  }
}
