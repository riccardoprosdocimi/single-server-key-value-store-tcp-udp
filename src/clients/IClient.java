package clients;

import java.net.InetAddress;

/**
 * The interface Client contains methods that all types of clients should support.
 */
public interface IClient {
  /**
   * Gets the hostname.
   *
   * @param hostname the hostname
   * @return the IP address
   */
  InetAddress getHostname(String hostname);

  /**
   * Sets the IP address.
   *
   * @param address the IP address
   */
  void setAddress(InetAddress address);

  /**
   * Gets the port.
   *
   * @param port the port
   * @return the port number
   */
  int getPort(String port);

  /**
   * Sets the port number.
   *
   * @param portNumber the port number
   */
  void setPortNumber(int portNumber);

  /**
   * Gets the user request.
   *
   * @return the user request
   */
  String getRequest();

  /**
   * Sets the user request.
   *
   * @param request the user request
   */
  void setRequest(String request);

  /**
   * Starts the client.
   */
  void execute();

  /**
   * Stops the client.
   */
  void shutdown();
}
