public class Computer {
  public String model, videocard, processor;
  public Integer ram, memory;
  public String[] returnParams() {
    String[] result = { model, videocard, ram.toString(), memory.toString(), processor };
    return result;
  }
}
