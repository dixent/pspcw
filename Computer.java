public class Computer {
  public String id, model, videocard, processor, active;
  public Integer ram, memory, price;
  public String[] returnParams() {
    String[] result = { model, videocard, ram.toString(), memory.toString(), processor, price.toString() };
    return result;
  }
}
