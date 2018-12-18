import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class MainWindow {

  Object[][] data;
  JFrame window, newComputer;
  JButton addComputer, editComputer, deleteComputer, saveComputer, createComputer, updateComputer;
  JTable table;
  TableModel tableModel;

  String[] userColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor" };
  String[] adminColumns = { "Id", "Model", "Video Card", "RAM", "Memory", "Processor", "Active" };
  String[][] rows;
  JTextField model, videocard, ram, memory, processor, active,
    edit_author, edit_year, edit_name, edit_genre;
  JScrollPane scrollPane;
  JMenuBar adminMenu;

  String userData; 

  public MainWindow(String login, String password) {
    userData = "login:" + login + ",password:" + password;
    initWindow();
    initNewComputer();
    //initeditComputer();
    
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

    adminMenu = new JMenuBar();
    adminMenu.add(addComputer);
    adminMenu.add(editComputer);
    adminMenu.add(deleteComputer);
    window.add(BorderLayout.PAGE_START, adminMenu);
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
    }
    initTableModel();
    table = new JTable(tableModel);
    window.add(BorderLayout.CENTER, table);

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

    newComputer.add(new JLabel("Videocard - "));
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

  // private void initeditComputer() {
  //   editComputer = new JFrame("Edit");
  //   editComputer.setSize(500, 400);
  //   editComputer.setLayout(new GridLayout(6, 2));

  //   editComputer.add(new JLabel("Author - "));
  //   edit_author = new JTextField();
  //   editComputer.add(edit_author);

  //   editComputer.add(new JLabel("Year - "));
  //   edit_year = new JTextField();
  //   editComputer.add(edit_year);

  //   editComputer.add(new JLabel("Name - "));
  //   edit_name = new JTextField();
  //   editComputer.add(edit_name);

  //   editComputer.add(new JLabel("Genre - "));
  //   edit_genre = new JTextField();
  //   editComputer.add(edit_genre);

  //   updateComputer = new JButton("Update");
  //   updateComputer.addActionListener(new ButtonListener());
  //   editComputer.add(updateComputer);
  // }

  private class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      switch(e.getActionCommand()) {
        case "New": 
          newComputer.setVisible(true);
          break;
        // case "Edit":
        //   editAction();
        //   if (selected_element != null) {
        //     editComputer.setVisible(true);
        //   }
        //   break;
        case "Create":
          createAction();
          break;
        // case "Update":
        //   updateAction();
        //   break;
        // case "Delete":
        //   deleteAction();
        //   break;
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
        //reindex();
      } else { 
        JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
      }
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
    }
    
  }

  // private void updateAction() {
  //   Object[] data = new Object[5];
  //   try {
  //     data[1] = edit_author.getText();
  //     data[2] = edit_name.getText();
  //     int int_year = Integer.parseInt(edit_year.getText());
  //     if ((int_year < 0) || (int_year > Calendar.getInstance().get(Calendar.YEAR))) {
  //       throw new NullPointerException();
  //     } else {
  //       data[3] = int_year;
  //     }
  //     data[4] = edit_genre.getText();
  //     data[0] = selected_element[0];
  //     Library.updateElementInDB(data);
  //   } catch(Exception e) {
  //     JOptionPane.showMessageDialog(null, "Error! Input not valid data!");
  //   }
  //   editComputer.setVisible(false);
  //   selected_element = null;
  //   reindex();
  // }

  // private void deleteAction() {
  //   Library.deleteElementFromDB(data[table.getSelectedRow()][0].toString());
  //   reindex();
  // }

  // Object[] selected_element;
  // private void editAction() {
  //   selected_element = data[table.getSelectedRow()];
  //   edit_author.setText(selected_element[1].toString());
  //   edit_year.setText(selected_element[2].toString());
  //   edit_name.setText(selected_element[3].toString());
  //   edit_genre.setText(selected_element[4].toString());
  // }

  // private void reindex() {
  //   Library.index();
  //   this.data = Library.data;
    
  //   window.remove(scrollPane);
  //   table = new JTable(data, columnsName);
  //   scrollPane = new JScrollPane(table);
  //   table.setFillsViewportHeight(true);
  //   window.add(BorderLayout.CENTER, scrollPane);
  //   SwingUtilities.updateComponentTreeUI(window);
  // }
}
