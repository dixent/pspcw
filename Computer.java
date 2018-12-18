public class Computer {
  public String id, model, videocard, processor, active;
  public Integer ram, memory;
  public String[] returnParams() {
    String[] result = { model, videocard, ram.toString(), memory.toString(), processor };
    return result;
  }
}
