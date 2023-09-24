package clients;

import java.io.IOException;

public interface IClient {
  void getHostname(String address);

  void getPort(int portNumber);

  String getMessage();

  void execute(String hostname, String port);

  void send(String msg, String hostname, String port) throws IOException;

  void receive() throws IOException;

  void log(String msg);
}
