package servers;

import utils.Logger;

public class Main {
  public static void main(String[] args) {
    if (args.length != 2) { // need to only enter type of server and port number
      System.err.println("Usage: javac servers/*.java | then | java servers.Main <TCPServer/UDPServer> <Port#>");
      System.exit(1);
    } else {
      if (args[0].equalsIgnoreCase("UDPServer")) {
        IServer UDPServer = new UDPServer(args[1]);
        UDPServer.setLogger(new Logger("UDPServerLogger", "UDPServerLog.log"));
        UDPServer.setTranslationService(new TranslationService());
        UDPServer.execute();
      } else if (args[0].equalsIgnoreCase("TCPServer")) {
        IServer TCPServer = new TCPServer(args[1]);
        TCPServer.setLogger(new Logger("TCPServerLogger", "TCPServerLog.log"));
        TCPServer.setTranslationService(new TranslationService());
        TCPServer.execute();
      } else {
        System.err.println("Please enter either 'TCPServer' or 'UDPServer'");
        System.exit(1);
      }
    }
  }
}
