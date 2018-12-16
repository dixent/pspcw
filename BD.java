import java.sql.*;
import java.util.Properties;
import java.util.*;
import javax.swing.*;

public class BD {
  Statement connection;
  public BD() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ComputerShop?autoReconnect=true&useSSL=true", "root", "password").createStatement();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public int createUser(String login, String password) {
    try {
      connection.executeUpdate("INSERT INTO user(login, password) "
        + "VALUES ('" + login + "', '" + password + "');");
      return 1; 
    } catch(Exception e) {
      return 0;
    }
  }

  public int findUser(String login, String password) {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM user u WHERE u.login='" + login + "' and u.password='" + password + "';"
      );
      myResultSet.next();
      return myResultSet.getInt("id");
    } catch(Exception e) {
      return 0;
    }
  }

  public int findLogin(String login) {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM user u WHERE u.login='" + login + "';"
      );
      myResultSet.next();
      //System.out.print(myResultSet);
      return myResultSet.getInt("id");
    } catch(Exception e) {
      return 0;
    }
  }
}
