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
  private static DatagramPacket request;
  private static Statement connection;
  public static void runServer() throws IOException {//метод сервера runServer
    try {
      boolean stopFlag = false;//создание флага stopFlag и его инициализация значением false
      byte[] data = new byte[512];//буфер для приема/передачи дейтаграммы реальному объекту с портом DEFAULT_PORT
      System.out.println("UDPServer: Started on " + s.getLocalAddress() + ":" + s.getLocalPort()); //вывод к консоль сообщения
      while(!stopFlag) {//цикл до тех пор, пока флаг не примет значение true
        data = new byte[512];
        request = new DatagramPacket(data, data.length);//создание объекта дейтаграммы для получения данных
        s.receive(request);//помещение полученного содержимого в
        try {
          for( String param : new String(request.getData()).trim().split(",")) {
            String[] kv = param.split(": ");
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
    switch(method.trim()) {
      case "login": 
        user.login = value;
        break;
      case "password":
        user.password = value;
        break;
      case "method":
        initMethod(value);
        break;
    }
  }

  public static void initMethod(String method) {
    System.out.println("|" + method + "|");
    switch(method.trim()) {
      case "findUser":
        System.out.println("==== SERVER START FIND USER ====");
        findUser();
        break;
      case "createUser":
        System.out.println("==== SERVER START CREATE USER ====");
        createUser();
        break;
      case "findLogin":
        System.out.println("==== SERVER START FIND LOGIN ====");
        findLogin();
        break;
      default:
        send("0".getBytes());
        break;
    }
    //System.out.println("==== NOTHING HAPPANED ====");
  }

  public static void createUser() {
    try {
      connection.executeUpdate("INSERT INTO user(login, password) "
        + "VALUES ('" + user.login + "', '" + user.password + "');");
      send("1".getBytes());
    } catch(Exception e) {
      System.out.println("==== SERVER CREATE USER EXCEPTION ====");
      send("0".getBytes());
    }
  }

  public static void findUser() {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM user u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';"
      );
      System.out.println("SELECT u.id FROM user u WHERE u.login='" + user.login + "' and u.password='" + user.password + "';");
      myResultSet.next();
      System.out.print(myResultSet.getString("id"));
      System.out.println("==== FIND USER TRUE ====");
      send(String.valueOf(myResultSet.getString("id")).getBytes());
    } catch(Exception e) {
      System.out.println("==== SERVER FIND USER EXCEPTION ====");
      e.printStackTrace();
      send("0".getBytes());
    }
  }

  public static void findLogin() {
    try {
      ResultSet myResultSet = connection.executeQuery(
        "SELECT u.id FROM user u WHERE u.login='" + user.login + "';"
      );
      myResultSet.next();
      System.out.println("==== SERVER FIND LOGIN TRUE ====");
      send(String.valueOf(myResultSet.getString("id")).getBytes());
    } catch(Exception e) {
      System.out.println("==== SERVER FIND LOGIN EXCEPTION ====");
      e.printStackTrace();
      send("0".getBytes());
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
