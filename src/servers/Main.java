package servers;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: javac servers/*.java | then | java servers.Main <Port Number>");
      System.exit(1);
    } else {
      UDPServer server = new UDPServer();
      server.execute(args[0]);
    }
  }
}