import java.net.*;
import java.io.*;
public class UIClient {
  public static InetAddress address;
  public final static int PORT = 8001;
  public static DatagramSocket s;
  public static byte[] data = new byte[10240];

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
  public static void main(String[] args) {
    try {
      address = InetAddress.getByName("127.0.0.1");
      s = new DatagramSocket();
      new Authorization();
    } catch(IOException ex) {
      ex.printStackTrace();
      System.out.println("==== 2 EXCEPTION ====");
    }
  }
}
