package clients;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
  private Logger logger;
  /**
   * The file handler.
   */
  protected FileHandler fileHandler;
  private static final String format = "MM-dd-yyyy HH:mm:ss.SSS";

  /**
   * Instantiates a new abstract client.
   *
   * @param loggerName  the logger name
   * @param logFileName the log file name
   */
  protected AbstractClient(String loggerName, String logFileName) {
    this.createLog(loggerName, logFileName);
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
      this.log("Unknown host entered by the user: " + hostname);
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
      this.log("Invalid port entered by the user: " + portNumber);
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

  private void createLog(String loggerName, String logFileName) {
    this.logger = Logger.getLogger(loggerName);
    try {
      this.fileHandler = new FileHandler(logFileName); // create a file handler to write log messages to a file
      this.fileHandler.setFormatter(new SimpleFormatter() { // create a custom formatter for millisecond precision timestamp
        public String format(LogRecord record) {
          String format = AbstractClient.format;
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
          return simpleDateFormat.format(System.currentTimeMillis()) + " - " + record.getMessage() + "\n";
        }
      });
      this.logger.addHandler(this.fileHandler); // add the file handler to the logger
    } catch (IOException e) {
      String format = AbstractClient.format;
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
      System.err.println(simpleDateFormat.format(System.currentTimeMillis()) + " - " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Logs any event that occurs.
   *
   * @param msg the message to be logged
   */
  public void log(String msg) {
    this.logger.info(msg); // log a message with millisecond precision timestamp
  }
}
