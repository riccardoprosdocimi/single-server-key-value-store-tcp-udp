package clients;

public class Main {
  public static void main(String[] args) {
    if (args.length != 2) { // need to only enter hostname and port number
      System.err.println("Usage: javac clients/*.java | then | java clients.Main <Host name> <Port number>");
      System.exit(1);
    } else {
      UDPClient client = new UDPClient(args[0], args[1], "UDPClientLogger", "UDPClientLog.log");
      client.execute();
    }
  }
}