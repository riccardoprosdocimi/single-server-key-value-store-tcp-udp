package clients;

public class Main {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Usage: java UDPClient <Host name> <Port number>");
      System.exit(1);
    } else {
      UDPClient client = new UDPClient();
      client.execute(args[0], args[1]);
    }
  }
}