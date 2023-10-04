package servers;

import utils.Logger;

/**
 * The class main is the entry point of the server application.
 */
public class Main {
  /**
   * The entry point of the server application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    if (args.length != 2) { // need to only enter type of server and port number
      System.err.println("Usage: javac servers/*.java | then | java servers.Main <Port#> <TCP/UDP>");
      System.exit(1);
    } else {
      if (args[1].equalsIgnoreCase("UDP")) { // create a UDP server object
        IServer UDPServer = new UDPServer(args[0]);
        UDPServer.setLogger(new Logger("UDPServerLogger", "UDPServerLog.log"));
        UDPServer.setTranslationService(new TranslationService());
        UDPServer.execute();
      } else if (args[1].equalsIgnoreCase("TCP")) { // create a TCP server object
        IServer TCPServer = new TCPServer(args[0]);
        TCPServer.setLogger(new Logger("TCPServerLogger", "TCPServerLog.log"));
        TCPServer.setTranslationService(new TranslationService());
        TCPServer.execute();
      } else {
        System.err.println("Please enter either 'TCP' or 'UDP'");
        System.exit(1);
      }
    }
  }
}
