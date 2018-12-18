import java.net.*;
import java.io.*;
public class UIClient {//описание класса клиента
  public static InetAddress address;
  public final static int PORT = 8001;
  public static DatagramSocket s;
  public static byte[] data = new byte[512];
  // public static void runClient() throws IOException {//метод клиента runClient
  //   try {
  //     byte[] buf = new byte[512]; //буфер для приема/передачи дейтаграммы
  //     s = new DatagramSocket();//привязка сокета к реальному объету
  //     System.out.println("UDPClient: Started");
  //     byte[] verCmd = "VERS".getBytes();
  //     DatagramPacket sendPacket = new DatagramPacket(verCmd, verCmd.length, , 8001);//создание
  //     //дейтаграммы для отсылки данных
  //     s.send(sendPacket);//посылка дейтаграммы
  //     DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);//создание дейтаграммы для получения данных
  //     s.receive(recvPacket);//получение дейтаграммы
  //     String version = new String(recvPacket.getData()).trim();//извлечение
  //     //данных (версии сервера)
  //     System.out.println("UDPClient: Server Version: " + version);
  //     byte[] quitCmd = "QUIT".getBytes();
  //     sendPacket.setData(quitCmd);//установить массив посылаемых данных
  //     sendPacket.setLength(quitCmd.length);//установить длину посылаемых
  //     // данных
  //     s.send(sendPacket); //послать данные серверу
  //     System.out.println("UDPClient: Ended");
  //   } finally {
  //     if (s != null) {
  //     s.close();//закрытие сокета клиента
  //     } 
  //   } 
  // }
  public static void send(byte[] data) {
    try {
      s.send(new DatagramPacket(data, data.length, address, PORT));
    } catch(Exception e) {
      System.out.println("==== 1 EXCEPTION ====");
    }
  }

  public static byte[] get() {
    try {
      data = new byte[512];
      DatagramPacket result = new DatagramPacket(data, data.length);
      s.receive(result);
      return result.getData();
    } catch(Exception e) {
      return "0".getBytes();
    }
  }
  public static void main(String[] args) {//метод main
    try {
      address = InetAddress.getByName("127.0.0.1");
      s = new DatagramSocket();
      Authorization authorication = new Authorization();
      //runClient();//вызов метода объекта client
    } catch(IOException ex) {
      ex.printStackTrace();
      System.out.println("==== 2 EXCEPTION ====");
    }
  }

  public static int convertByteToInt(byte[] b) {           
    int value= 0;
    for(int i=0; i<b.length; i++)
      value = (value << 8) | b[i];     
    return value;       
  }
}
