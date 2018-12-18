import java.net.*;
import java.sql.*;
import java.util.Properties;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class ShopServer {
  public final static int DEFAULT_PORT = 8001;//определение порта
  public static DatagramSocket s;
  private static User user = new User();
  private static Computer computer = new Computer();
  private static DatagramPacket request;
  private static Statement connection;
  public static void runServer() throws IOException {//метод сервера runServer
    try {
      boolean stopFlag = false;//создание флага stopFlag и его инициализация значением false
      byte[] data;//буфер для приема/передачи дейтаграммы реальному объекту с портом DEFAULT_PORT
      System.out.println("UDPServer: Started on " + s.getLocalAddress() + ":" + s.getLocalPort()); //вывод к консоль сообщения
      while(!stopFlag) {//цикл до тех пор, пока флаг не примет значение true
        data = new byte[10240];
        request = new DatagramPacket(data, data.length);//создание объекта дейтаграммы для получения данных
        s.receive(request);//помещение полученного содержимого в
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
        s.close();//закрытие сокета сервера
      }
    }
  }

  public static void main(String[] args) {//метод main
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
          computer.active = value;
          break;
        case "method":
          initMethod(value);
          break;
      }
    } catch(Exception e) {
      send("0".getBytes());
    }
  }

  public static void initMethod(String method) {
    System.out.println("|" + method + "|");
    switch(method.trim()) {
      case "findUser":
        System.out.println("==== SERVER START FIND USER ====");
        send(String.valueOf(findUser()).getBytes());
        break;
      case "createUser":
        System.out.println("==== SERVER START CREATE USER ====");
        send(String.valueOf(createUser()).getBytes());
        break;
      case "checkAdmin":
        System.out.println("==== SERVER START CHECK ADMIN ====");
        send(String.valueOf(checkAdmin()).getBytes());
        break;
      case "createComputer":
        System.out.println("==== SERVER START CREATE COMPUTER ====");
        send(String.valueOf(createComputer()).getBytes());
        break;
      case "indexComputers":
        System.out.println("==== SERVER START INDEX COMPUTERS ====");
        send(String.valueOf(indexComputers()).getBytes());
        break;
      case "deleteComputer":
        System.out.println("==== SERVER START DELETE COMPUTER ====");
        send(String.valueOf(deleteComputer()).getBytes());
        break;
      case "updateComputer":
        System.out.println("==== SERVER START UPDATE COMPUTER ====");
        send(String.valueOf(updateComputer()).getBytes());
        break;
      default:
        send("0".getBytes());
        break;
    }
    //System.out.println("==== NOTHING HAPPANED ====");
  }
  public static String updateComputer(){
    try {
      connection.executeUpdate("UPDATE computers SET model='" + computer.model + "', videocard='" + 
        computer.videocard + "', ram=" + String.valueOf(computer.ram) + ", memory=" + 
        String.valueOf(computer.memory) + ", processor='" + computer.processor + "', active=" + computer.active + " where id=" + String.valueOf(computer.id) + ";");
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
            myResultSet.getString("memory"), myResultSet.getString("processor") };
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
            myResultSet.getString("memory"), myResultSet.getString("processor"), myResultSet.getString("active") };
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
      connection.executeUpdate("INSERT INTO computers(model, videocard, ram, memory, processor) "
        + "VALUES ('" + String.join("', '", computer.returnParams()) + "');");
      System.out.println("==== CREATE COMPUTER TRUE ====");
      return "1";
    } catch(Exception e) {
      System.out.println("==== SERVER CREATE COMPUTER EXCEPTION ====");
      return "0";
    }
  }

  // public static String findComputer() {
  //   try {
  //     ResultSet myResultSet = connection.executeQuery(
  //       "SELECT u.id FROM users u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';"
  //     );
  //     System.out.println("SELECT u.id FROM users u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';");
  //     myResultSet.next();
  //     System.out.print(myResultSet.getString("id"));
  //     System.out.println("==== FIND USER TRUE ====");
  //     return myResultSet.getString("id");
  //     //send(String.valueOf(myResultSet.getString("id")).getBytes());
  //   } catch(Exception e) {
  //     System.out.println("==== SERVER FIND USER EXCEPTION ====");
  //     e.printStackTrace();
  //     return "0";
  //     //send("0".getBytes());
  //   }
  // }

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
      System.out.print(myResultSet.getString("id"));
      System.out.println("==== FIND USER TRUE ====");
      return myResultSet.getString("id");
      //send(String.valueOf(myResultSet.getString("id")).getBytes());
    } catch(Exception e) {
      System.out.println("==== SERVER FIND USER EXCEPTION ====");
      e.printStackTrace();
      return "0";
      //send("0".getBytes());
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
      //send(String.valueOf(myResultSet.getString("id")).getBytes());
    } catch(Exception e) {
      System.out.println("==== SERVER FIND LOGIN EXCEPTION ====");
      e.printStackTrace();
      //send("0".getBytes());
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
