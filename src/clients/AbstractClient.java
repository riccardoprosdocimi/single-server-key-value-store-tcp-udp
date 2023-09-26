package clients;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import utils.ILogger;
import utils.Logger;

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
   * @param loggerName  the logger name
   * @param logFileName the log file name
   */
  protected AbstractClient(String loggerName, String logFileName) {
    this.logger = new Logger(loggerName, logFileName);
  }

  /**
   * Gets the hostname.
   *
   * @param hostname the hostname
   * @return the IP address
   */
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
  public void setAddress(InetAddress address) {
    this.address = address;
  }

  /**
   * Gets the port.
   *
   * @param port the port
   * @return the port number
   */
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
  public void setPortNumber(int portNumber) {
    this.portNumber = portNumber;
  }

  /**
   * Gets the user request.
   *
   * @return the user request
   */
  public String getRequest() {
    System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
    return this.scanner.nextLine();
  }

  /**
   * Sets the user request.
   *
   * @param request the user request
   */
  public void setRequest(String request) {
    this.request = request;
  }
}
