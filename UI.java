
/*
CLASSPATH=$CLASSPATH:/usr/share/java/mysql.jar
export CLASSPATH
*/

public class UI {
  public static BD bd;
  public static void main(String[] args) {
    bd = new BD();
    Authorization a = new Authorization(); 
  }

  private void initConnect() {

  }
}
