package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UDPServer implements IServer {
  private DatagramSocket socket = null;
  private final HashMap<String, String> dictionary = new HashMap<>();
  private DatagramPacket request = null;
  private final Logger logger;

  public UDPServer() {
    this.logger = Logger.getLogger("UDPServer logger");
    try {
      FileHandler fileHandler = new FileHandler("UDPServerLog.log"); // create a file handler to write log messages to a file
      fileHandler.setFormatter(new SimpleFormatter() { // create a custom formatter for millisecond precision timestamp
        public String format(LogRecord record) {
          String format = "MM-dd-yyyy HH:mm:ss.SSS";
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
          return simpleDateFormat.format(System.currentTimeMillis()) + " - " + record.getMessage() + "\n";
        }
      });
      this.logger.addHandler(fileHandler); // add the file handler to the logger
    } catch (IOException e) {
      String format = "MM-dd-yyyy HH:mm:ss.SSS";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
      System.err.println(simpleDateFormat.format(System.currentTimeMillis()) + " - " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Binds the port number to the server's socket.
   *
   * @param port the port number
   */
  public void bindPort(String port) throws SocketException {
    int portNumber = Integer.parseInt(port);
    this.socket = new DatagramSocket(portNumber);
  }

  /**
   * Decodes a UDP packet.
   *
   * @param packet the UDP packet
   * @return the message
   */
  public String decode(DatagramPacket packet) {
    return new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8); // convert bytes to string
  }

  /**
   * Encodes a UDP packet.
   *
   * @param reply the message
   * @return the UDP packet
   */
  public DatagramPacket encode(String reply) {
    byte[] message = reply.getBytes(StandardCharsets.UTF_8); // convert string to bytes
    return new DatagramPacket(message, message.length, this.request.getAddress(), this.request.getPort());
  }

  /**
   * Starts the server.
   */
  public void execute(String port) {
    try {
      System.out.println("Server is running...");
      byte[] buffer = new byte[1000];
      this.bindPort(port);
      boolean isRunning = true;
      while (isRunning) {
        this.request = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(this.request);
        String request = this.decode(this.request);
        if (request.equalsIgnoreCase("server shutdown") || request.equalsIgnoreCase("server stop")) {
          this.socket.send(this.encode("Server is shutting down..."));
          isRunning = false;
        } else {
          String reply = this.parseExecution(request);
          this.socket.send(this.encode(reply));
        }
      }
      System.out.println("Server is shutting down...");
      this.socket.close();
      this.log("Received a request to shut down from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      System.out.println("Server closed");
    } catch (SocketException e) {
      this.log("Socket: " + e.getMessage());
    } catch (IOException e) {
      this.log("IO: " + e);
    }
  }

  /**
   * Logs the requests received and any errors that occur.
   *
   * @param msg the message to be logged
   */
  public void log(String msg) {
    this.logger.info(msg); // log a message with millisecond precision timestamp
  }

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the English word to be translated
   * @param value the Italian translation
   * @return the outcome of the operation
   */
  public String put(String key, String value) {
    this.dictionary.put(key.toLowerCase(), value.toLowerCase());
    this.log("Received a request to save " + "\"" + key + "\"" + "mapped to " + "\"" + value + "\" from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
    return "SUCCESS";
  }

  /**
   * Retrieves the value.
   *
   * @param key the English word
   * @return the Italian word
   */
  public String get(String key) {
    String translation = "FAIL: I don't know the Italian translation for " + "\"" + key + "\"" + " yet";
    if (this.dictionary.containsKey(key)) {
      this.log("Received a request to retrieve the value mapped to " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      translation = this.dictionary.get(key);
    } else {
      this.log("Received a request to retrieve the value mapped to a nonexistent key " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
    }
    return translation;
  }

  /**
   * Deletes a key-value pair.
   *
   * @param key the English word
   * @return the outcome of the operation
   */
  public String delete(String key) {
    if (this.dictionary.containsKey(key)) {
      this.dictionary.remove(key);
      this.log("Received a request to delete the key-value pair associated with " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      return "SUCCESS";
    } else {
      this.log("Received a request to delete a nonexistent key-value pair associated with " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      return "FAIL: " + "\"" + key + "\" " + "does not exist\n";
    }
  }

  /**
   * Parses the client's request. The predefined protocol for the dictionary operation is
   * PUT/GET/DELETE:key:value[only with PUT].
   *
   * @param request the operation the server needs to execute
   * @return the result of the operation
   */
  private String parseExecution(String request) {
    String result;
    String[] elements = request.split(":");
    if (elements.length < 2 || elements.length > 3) {
      this.log("Received malformed request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      return "FAIL: the server received a malformed request. Please follow the predefined protocol PUT/GET/DELETE:key:value[only with PUT] and try again\n";
    } else {
      String operation;
      try {
        operation = elements[0];
      } catch (Exception e) {
        this.log("Parsing error: operation. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
        return "FAIL: the server could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[only with PUT] and try again\n";
      }
      String key;
      try {
        key = elements[1];
      } catch (Exception e) {
        this.log("Parsing error: key. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
        return "FAIL: the server could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[only with PUT] and try again\n";
      }
      String value;
      switch (operation.toUpperCase()) {
        case "PUT":
          try {
            value = elements[2];
          } catch (Exception e) {
            this.log("Parsing error: value. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
            return "FAIL: the server could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[only with PUT] and try again\n";
          }
          result = this.put(key, value);
          this.log("Responded with " + result);
          break;
        case "GET":
          result = this.get(key);
          this.log("Responded with " + result);
          break;
        case "DELETE":
          result = this.delete(key);
          this.log("Responded with " + result);
          break;
        default:
          this.log("Received the request: " + request);
          String reply = "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[only with PUT] and try again\n";
          this.log("Responded with " + reply);
          return reply;
      }
    }
    return result;
  }
}
