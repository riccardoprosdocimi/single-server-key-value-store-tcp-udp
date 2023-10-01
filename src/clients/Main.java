package clients;

import utils.Logger;

/**
 * The class main is the entry point of the client application.
 */
public class Main {
  /**
   * The entry point of the client application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    if (args.length != 3) { // need to only enter type of client, hostname and port number
      System.err.println("Usage: javac clients/*.java | then | java clients.Main <TCPClient/UDPClient> <Hostname> <Port#>");
      System.exit(1);
    } else {
      if (args[0].equalsIgnoreCase("TCPClient")) { // create a TCP client object
        IClient TCPClient = new TCPClient(args[1], args[2]);
        TCPClient.setLogger(new Logger("TCPClientLogger", "TCPClientLog.log"));
        TCPClient.execute();
      } else if (args[0].equalsIgnoreCase("UDPClient")) { // create a UDP client object
      IClient UDPClient = new UDPClient(args[1], args[2]);
      UDPClient.setLogger(new Logger("UDPClientLogger", "UDPClientLog.log"));
      UDPClient.execute();
    } else {
        System.err.println("Please enter either 'TCPClient' or 'UDPClient'");
        System.exit(1);
      }
    }
  }
}
