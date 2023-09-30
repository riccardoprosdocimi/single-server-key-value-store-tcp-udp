package clients;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import utils.ILogger;

/**
 * The class abstract client contains attributes and methods for all types of client.
 */
public abstract class AbstractClient implements IClient {
  /**
   * The IP address.
   */
  protected InetAddress address;
  /**
   * The port number.
   */
  protected int portNumber;
  /**
   * The scanner.
   */
  protected final Scanner scanner = new Scanner(System.in);
  /**
   * The user request.
   */
  protected String request;
  /**
   * The logger.
   */
  protected ILogger logger;

  /**
   * Instantiates a new abstract client.
   *
   * @param hostname the hostname
   * @param port     the port
   */
  protected AbstractClient(String hostname, String port) {
    InetAddress address = this.getHostname(hostname);
    int portNumber = this.getPort(port);
    if (address == null || portNumber == -1) {
      this.logger.close();
      System.exit(1);
    } else {
      this.setAddress(address);
      this.setPortNumber(portNumber);
    }
  }

  /**
   * Gets the hostname.
   *
   * @param hostname the hostname
   * @return the IP address
   */
  @Override
  public InetAddress getHostname(String hostname) {
    try {
      return InetAddress.getByName(hostname);
    } catch (UnknownHostException e) {
      System.err.println("Unknown host entered");
      this.logger.log("Unknown host entered by the user: " + hostname);
      return null;
    }
  }

  /**
   * Sets the IP address.
   *
   * @param address the IP address
   */
  @Override
  public void setAddress(InetAddress address) {
    this.address = address;
  }

  /**
   * Gets the port.
   *
   * @param port the port
   * @return the port number
   */
  @Override
  public int getPort(String port) {
    int portNumber = Integer.parseInt(port);
    if (portNumber >= 49152 && portNumber <= 65535) {
      return portNumber;
    } else {
      System.err.println("Invalid port entered");
      this.logger.log("Invalid port entered by the user: " + portNumber);
      return -1;
    }
  }

  /**
   * Sets the port number.
   *
   * @param portNumber the port number
   */
  @Override
  public void setPortNumber(int portNumber) {
    this.portNumber = portNumber;
  }

  /**
   * Sets the logger.
   *
   * @param logger the logger
   */
  @Override
  public void setLogger(ILogger logger) {
    this.logger = logger;
  }

  /**
   * Gets the user request.
   *
   * @return the user request
   */
  @Override
  public String getRequest() {
    System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
    return this.scanner.nextLine();
  }

  /**
   * Sets the user request.
   *
   * @param request the user request
   */
  @Override
  public void setRequest(String request) {
    this.request = request;
  }
}
