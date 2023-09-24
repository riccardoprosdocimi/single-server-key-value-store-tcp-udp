package servers;

import java.net.Socket;

public class TCPServer {
  private Socket socket;

  public void setSocket(Socket socket) {
    this.socket = socket;
  }
}
