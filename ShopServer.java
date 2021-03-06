import java.net.*;
import java.sql.*;
import java.util.Properties;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class ShopServer {
  public final static int DEFAULT_PORT = 8001;
  public static DatagramSocket s;
  private static User user = new User();
  private static Computer computer = new Computer();
  private static DatagramPacket request;
  private static Statement connection;
  public static void runServer() throws IOException {
    try {
      boolean stopFlag = false;
      byte[] data;
      System.out.println("UDPServer: Started on " + s.getLocalAddress() + ":" + s.getLocalPort());
      while(!stopFlag) {
        data = new byte[10240];
        request = new DatagramPacket(data, data.length);
        s.receive(request);
        try {
          System.out.println(new String(request.getData()).trim());
          for( String param : new String(request.getData()).trim().split(",")) {
            String[] kv = param.split(":");
            System.out.println(kv[0]+ " " + kv[1]);
            initParams(kv[0], kv[1]);
          }
        } catch(Exception e) {
          System.out.println("==== SERVER RUN SERVER EXCEPTION ====");
          e.printStackTrace();
          send("0".getBytes());    
        }
      }
      System.out.println("UDPServer: Stopped");
    } finally {
      if (s != null) {
        s.close();
      }
    }
  }

  public static void main(String[] args) {
    try {
      s = new DatagramSocket(DEFAULT_PORT);
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ComputerShop?autoReconnect=true&useSSL=true", "root", "password").createStatement();
      runServer();
    } catch(Exception e) {
      System.out.println("==== SERVER CONNECT EXCEPTION ====");
      e.printStackTrace();
    }
  }

  public static void initParams(String method, String value) {
     try { 
      switch(method.trim()) {
        case "login": 
          user.login = value;
          break;
        case "password":
          user.password = value;
          break;
        case "id":
          computer.id = value;
          break;
        case "model":
          computer.model = value;
          break;
        case "videocard":
          computer.videocard = value;
          break;
        case "ram":
          computer.ram = Integer.parseInt(value);
          break;
        case "memory":
          computer.memory = Integer.parseInt(value);
          break;
        case "processor":
          computer.processor = value;
          break;
        case "active":
          computer.active = String.valueOf(Boolean.valueOf(value));
          break;
        case "price":
          computer.price = Integer.parseInt(value);
          break;
        case "user_id":
          user.user_id = value;
          break;
        case "user_login":
          user.user_login = value;
          break;
        case "user_password":
          user.user_password = value;
          break;
        case "user_admin":
          user.user_admin = value;
          break;
        case "method":
          initMethod(value);
          break;
      }
    } catch(Exception e) {
      System.out.println("==== SERVER INIT PARAMS EXCEPTION ====");
      send("0".getBytes());
    }
  }

  public static void initMethod(String method) {
    System.out.println("|" + method + "|");
    switch(method.trim()) {
      case "findUser":
        System.out.println("==== SERVER START FIND USER ====");
        send(findUser().getBytes());
        break;
      case "createUser":
        System.out.println("==== SERVER START CREATE USER ====");
        send(createUser().getBytes());
        break;
      case "checkAdmin":
        System.out.println("==== SERVER START CHECK ADMIN ====");
        send(checkAdmin().getBytes());
        break;
      case "createComputer":
        System.out.println("==== SERVER START CREATE COMPUTER ====");
        send(createComputer().getBytes());
        break;
      case "indexComputers":
        System.out.println("==== SERVER START INDEX COMPUTERS ====");
        send(indexComputers().getBytes());
        break;
      case "deleteComputer":
        System.out.println("==== SERVER START DELETE COMPUTER ====");
        send(deleteComputer().getBytes());
        break;
      case "updateComputer":
        System.out.println("==== SERVER START UPDATE COMPUTER ====");
        send(updateComputer().getBytes());
        break;
      case "buyComputer":
        System.out.println("==== SERVER START BUY COMPUTER ====");
        send(buyComputer().getBytes());
        break;
      case "myOrders":
        System.out.println("==== SERVER START MY ORDERS ====");
        send(myOrders().getBytes());
        break;
      case "indexUsers":
        System.out.println("==== SERVER START INDEX USERS ====");
        send(indexUsers().getBytes());
        break;
      case "updateUser":
        System.out.println("==== SERVER START UPDATE USER ====");
        send(updateUser().getBytes());
        break;
      case "deleteUser":
        System.out.println("==== SERVER START DELETE USER ====");
        send(deleteUser().getBytes());
        break;
      default:
        System.out.println("==== SERVER INIT METHOD EXCEPTION ====");
        send("0".getBytes());
        break;
    }
  }

  public static String buyComputer() {
    try {
      connection.execute("SET FOREIGN_KEY_CHECKS=1;");
      connection.executeUpdate("UPDATE computers SET user_id=" + findUser() + ", active=false WHERE id=" + computer.id + ";"); 
      System.out.println("==== TRUE BUY COMPUTER ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== BUY COMPUTER EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String myOrders() {
    try {
      ResultSet myResultSet = connection.executeQuery(
          "SELECT COUNT(*) AS count_objects FROM computers WHERE user_id=" + findUser() + ";"
        );
      myResultSet.next();
      String[] data = new String[myResultSet.getInt("count_objects")];
      myResultSet = connection.executeQuery("SELECT * FROM computers WHERE user_id=" + findUser() + ";");
      int i = 0;
      while(myResultSet.next()) {
        String[] row = { myResultSet.getString("id"), myResultSet.getString("model"), 
        myResultSet.getString("videocard"), myResultSet.getString("ram"), 
        myResultSet.getString("memory"), myResultSet.getString("processor"), myResultSet.getString("price") };
        data[i] = String.join("&", row);
        System.out.println(data[i]);
        i++; 
      }
      
      System.out.println("==== TRUE MY ORDERS ====");
      return String.join("#", data);
    } catch(Exception e) {
      System.out.println("==== MY ORDERS EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String updateComputer(){
    try {
      connection.executeUpdate("UPDATE computers SET model='" + computer.model + "', videocard='" + 
        computer.videocard + "', ram=" + String.valueOf(computer.ram) + ", memory=" + 
        String.valueOf(computer.memory) + ", processor='" + computer.processor + "', active=" + computer.active + 
        ", price=" + String.valueOf(computer.price) + " where id=" + String.valueOf(computer.id) + ";");
      System.out.println("==== UPDATE COMPUTER TRUE ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== SERVER UPDATE COMPUTER EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String deleteComputer() {
    try {
      connection.executeUpdate("DELETE FROM computers WHERE id=" + computer.id + ";"); 
      System.out.println("==== TRUE DELETE COMPUTER ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== DELETE COMPUTER EXCEPTION ====");
      return "0";
    }
  }

  public static String deleteUser() {
    try {
      connection.executeUpdate("DELETE FROM users WHERE id=" + user.user_id + ";"); 
      System.out.println("==== TRUE DELETE USER ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== DELETE USER EXCEPTION ====");
      return "0";
    }
  }

  public static String indexUsers() {
    try {
      String[] data;
      ResultSet myResultSet = connection.executeQuery(
        "SELECT COUNT(*) AS count_objects FROM users;"
      );
      myResultSet.next();
      data = new String[myResultSet.getInt("count_objects")];
      myResultSet = connection.executeQuery("SELECT * FROM users;");
      int i = 0;
      while(myResultSet.next()) {
        String[] row = { myResultSet.getString("id"), myResultSet.getString("login"), 
          myResultSet.getString("password"), myResultSet.getString("admin")};
        data[i] = String.join("&", row);
        System.out.println(data[i]);
        i++;
      }
      return String.join("#", data);
    } catch(Exception e) {
      System.out.println("==== INDEX USERS EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String updateUser(){
    try {
      connection.executeUpdate("UPDATE users SET login='" + user.user_login + "', password='" + user.user_password + "', admin='" + user.user_admin + "' WHERE id=" + user.user_id + ";");
      System.out.println("==== UPDATE USER TRUE ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== SERVER UPDATE USER EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String indexComputers() {
    try {
      String[] data;
      if(checkAdmin().equals("0")) {
        ResultSet myResultSet = connection.executeQuery(
          "SELECT COUNT(*) AS count_objects FROM computers WHERE active=true;"
        );
        myResultSet.next();
        data = new String[myResultSet.getInt("count_objects")];
        myResultSet = connection.executeQuery("SELECT * FROM computers WHERE active=true;");
        int i = 0;
        while(myResultSet.next()) {
          String[] row = { myResultSet.getString("id"), myResultSet.getString("model"), 
            myResultSet.getString("videocard"), myResultSet.getString("ram"), 
            myResultSet.getString("memory"), myResultSet.getString("processor"), myResultSet.getString("price") };
          data[i] = String.join("&", row);
          System.out.println(data[i]);
          i++;
        }
      } else {
        ResultSet myResultSet = connection.executeQuery(
          "SELECT COUNT(*) AS count_objects FROM computers;"
        );
        myResultSet.next();
        data = new String[myResultSet.getInt("count_objects")];
        myResultSet = connection.executeQuery("SELECT * FROM computers;");
        int i = 0;
        while(myResultSet.next()) {
          String[] row = { myResultSet.getString("id"), myResultSet.getString("model"), 
            myResultSet.getString("videocard"), myResultSet.getString("ram"), 
            myResultSet.getString("memory"), myResultSet.getString("processor"), myResultSet.getString("active"),
            myResultSet.getString("user_id"), myResultSet.getString("price") };
          data[i] = String.join("&", row);
          System.out.println(data[i]);
          i++;
        }
      }
      return String.join("#", data);
    } catch(Exception e) {
      System.out.println("==== INDEX COMPUTERS EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }


  public static String createComputer() {
    try {
      connection.executeUpdate("INSERT INTO computers(model, videocard, ram, memory, processor, price, user_id) "
        + "VALUES ('" + String.join("', '", computer.returnParams()) + "'," + findUser() + ");");
      System.out.println("==== CREATE COMPUTER TRUE ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== SERVER CREATE COMPUTER EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String checkAdmin() {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.admin FROM users u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';"
      );
      myResultSet.next();
      System.out.print(myResultSet.getString("admin"));
      System.out.println("==== CHECK ADMIN TRUE ====");
      return myResultSet.getString("admin");
    } catch(Exception e) {
      System.out.println("==== SERVER CHECK ADMIN EXCEPTION ====");
      return "0";
    }
  }

  public static String createUser() {
    if (Integer.valueOf(findLogin()) > 0) {
      System.out.println("==== SERVER CREATE USER EXCEPTION ====");
      return "0";
    } else {
      try {
        connection.executeUpdate("INSERT INTO users(login, password) "
          + "VALUES ('" + user.login + "', '" + user.password + "');");
        System.out.println("==== CREATE USER TRUE ====");
        return String.valueOf(findUser());
      } catch(Exception e) {
        System.out.println("==== SERVER CREATE USER EXCEPTION ====");
        return "0";
      }
    }
  }

  public static String findUser() {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM users u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';"
      );
      System.out.println("SELECT u.id FROM users u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';");
      myResultSet.next();
      System.out.println("==== FIND USER TRUE ====");
      return myResultSet.getString("id");
    } catch(Exception e) {
      System.out.println("==== SERVER FIND USER EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static String findLogin() {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM users u WHERE u.login='" + user.login + "';"
      );
      myResultSet.next();
      System.out.println("==== SERVER FIND LOGIN TRUE ====");
      return myResultSet.getString("id");
    } catch(Exception e) {
      System.out.println("==== SERVER FIND LOGIN EXCEPTION ====");
      e.printStackTrace();
      return "0";
    }
  }

  public static void send(byte[] data) {
    try {
      s.send(new DatagramPacket(data, data.length, request.getAddress(), request.getPort()));
      System.out.println("SEND MESSAGE");
    } catch(Exception e) {
      System.out.println("==== SERVER SEND EXCEPTION ====");
    }
  }
}
