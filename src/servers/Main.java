package servers;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) { // need to only enter the port number
      System.err.println("Usage: javac servers/*.java | then | java servers.Main <Port Number>");
      System.exit(1);
    } else {
      IServer UDPServer = new UDPServer(args[0], new TranslationService(), "UDPServerLogger", "UDPServerLog.log");
      //IServer TCPServer = new TCPServer(args[0], "UDPServerLogger", "UDPServerLog.log");
      UDPServer.execute();
    }
  }
}