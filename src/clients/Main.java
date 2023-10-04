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
      System.err.println("Usage: javac clients/*.java | then | java clients.Main <Hostname> <Port#> <TCP/UDP> ");
      System.exit(1);
    } else {
      if (args[2].equalsIgnoreCase("TCP")) { // create a TCP client object
        IClient TCPClient = new TCPClient(args[0], args[1]);
        TCPClient.setLogger(new Logger("TCPClientLogger", "TCPClientLog.log"));
        TCPClient.execute();
      } else if (args[2].equalsIgnoreCase("UDP")) { // create a UDP client object
      IClient UDPClient = new UDPClient(args[0], args[1]);
      UDPClient.setLogger(new Logger("UDPClientLogger", "UDPClientLog.log"));
      UDPClient.execute();
    } else {
        System.err.println("Please enter either 'TCP' or 'UDP'");
        System.exit(1);
      }
    }
  }
}
