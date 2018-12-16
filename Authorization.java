import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Authorization {
  JFrame window;
  JTextField login, password;
  public Authorization() {
    window = new JFrame("Authorization");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(250, 150);
    window.setLayout(new GridLayout(0,2));
    initFields();
    initButtons();
    window.setVisible(true);
  }

  public void initButtons() {
    JButton in = new JButton("Sign In");
    in.addActionListener(new ButtonListener());
    window.add(in);
    
    JButton up = new JButton("Sign Up");
    up.addActionListener(new ButtonListener());
    window.add(up);
  }

  public void initFields() {
    window.add(new JLabel("Login"));
    login = new JTextField();
    window.add(login);
    window.add(new JLabel("Password"));
    password = new JTextField();
    window.add(password);
  }



  private class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (login.getText().isEmpty() || password.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Fields must be filled!");
        return;
      }
      int result;
      switch(e.getActionCommand()) {
        case "Sign In":
          UIClient.send(("login: " + login.getText() + ",password: " + password.getText() + ",method: findUser").getBytes());
          //id = UI.bd.findUser(login.getText(), password.getText());
          result = Integer.parseInt(new String(UIClient.get()).trim());
          System.out.print(result);
          if(result > 0) {
            JOptionPane.showMessageDialog(null, "True");
          } else {
            JOptionPane.showMessageDialog(null, "Invalid data entered!");
          }
          break;
        case "Sign Up":
          UIClient.send(("login: " + login.getText() + ",password: " + password.getText() + ",method: findLogin").getBytes());
          result = Integer.parseInt(new String(UIClient.get()).trim());
          System.out.print(result);
          if (result > 0) {
            JOptionPane.showMessageDialog(null, "This login already exists!");
          } else {
            UIClient.send(("login: " + login.getText() + ",password: " + password.getText() + ",method: createUser").getBytes());
            result = Integer.parseInt(new String(UIClient.get()).trim());
            if (result > 0) {
              JOptionPane.showMessageDialog(null, "User registered successfully! Sign in. Login will be done automatically");
            } else {
              JOptionPane.showMessageDialog(null, "Error! Can not create User. Not valid Data!");
            }
          }
          break;
      }
    }
  }
}
