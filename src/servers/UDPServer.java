package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * This class represents a translation server that communicates via the UDP protocol.
 */
public class UDPServer extends AbstractServer {
  private DatagramSocket socket;
  private DatagramPacket request;

  /**
   * Instantiates a new UDP translation server.
   *
   * @param port               the port
   * @param translationService the translation service
   * @param loggerName         the logger name
   * @param logFileName        the log file name
   */
  public UDPServer(String port, ITranslationService translationService, String loggerName, String logFileName) {
    super(port, translationService, loggerName, logFileName);
  }

  /**
   * Sets the UDP socket.
   *
   * @param socket the UDP socket
   */
  public void setSocket(DatagramSocket socket) {
    this.socket = socket;
  }

  /**
   * Sets the request.
   *
   * @param request the request
   */
  public void setRequest(DatagramPacket request) {
    this.request = request;
  }

  private String decode(DatagramPacket packet) {
    return new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8); // convert bytes to string
  }

  private DatagramPacket encode(String reply) {
    byte[] message = reply.getBytes(StandardCharsets.UTF_8); // convert string to bytes
    return new DatagramPacket(message, message.length, this.request.getAddress(), this.request.getPort());
  }

  /**
   * Starts the server.
   */
  public void execute() {
    try {
      this.setSocket(new DatagramSocket(this.portNumber)); // open a new UDP socket
      byte[] buffer = new byte[1000]; // prepare the buffer for incoming messages
      boolean isRunning = true;
      System.out.println("Server is running...");
      this.logger.log("UDPServer running...");
      while (isRunning) { // keep running
        this.setRequest(new DatagramPacket(buffer, buffer.length)); // prepare the incoming request for processing
        this.socket.receive(this.request); // get the incoming request
        String request = this.decode(this.request); // convert bytes to string
        if (request.equalsIgnoreCase("server shutdown") || request.equalsIgnoreCase("server stop")) { // if the client sends a stop/shutdown request
          this.socket.send(this.encode("Server is shutting down...")); // acknowledge
          isRunning = false; // prepare the shutdown process
        } else {
          String reply = this.parseExecution(request); // process the request
          this.socket.send(this.encode(reply)); // send the result
        }
      }
      // shut down gracefully
      this.shutdown();
    } catch (SocketException e) {
      this.logger.log("Socket: " + e.getMessage());
    } catch (IOException e) {
      this.logger.log("IO: " + e.getMessage());
    }
  }

  /**
   * Parses the client's request. The predefined protocol for the translation operation is
   * PUT/GET/DELETE:key:value[with PUT only].
   *
   * @param request the operation the server needs to execute
   * @return the result of the operation
   */
  private String parseExecution(String request) {
    String result;
    String[] elements = request.split(":");
    if (elements.length < 2 || elements.length > 3) { // the protocol is not followed
      this.logger.log("Received malformed request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
      return "FAIL: the server received a malformed request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
    } else {
      String operation;
      try {
        operation = elements[0]; // PUT/GET/DELETE
      } catch (Exception e) {
        this.logger.log("Parsing error: operation. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
        return "FAIL: the server could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String key;
      try {
        key = elements[1]; // word to be translated
      } catch (Exception e) {
        this.logger.log("Parsing error: key. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
        return "FAIL: the server could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String value;
      switch (operation.toUpperCase()) {
        case "PUT":
          try {
            value = elements[2]; // word to translate
            this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\" from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
          } catch (Exception e) {
            this.logger.log("Parsing error: value. Request of length " + this.request.getLength() + " from " + "<" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
            return "FAIL: the server could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
          }
          result = this.translationService.put(key, value);
          this.logger.log("Responded with " + result);
          break;
        case "GET":
          result = this.translationService.get(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to retrieve the value mapped to a nonexistent key " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
          } else {
            this.logger.log("Received a request to retrieve the value mapped to " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
          }
          this.logger.log("Responded with " + result);
          break;
        case "DELETE":
          result = this.translationService.delete(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to delete a nonexistent key-value pair associated with " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
          } else {
            this.logger.log("Received a request to delete the key-value pair associated with " + "\"" + key + "\" " + "from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
          }
          this.logger.log("Responded with " + result);
          break;
        default: // invalid request
          this.logger.log("Received the request: " + request);
          String reply = "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
          this.logger.log("Responded with " + reply);
          return reply;
      }
    }
    return result;
  }

  /**
   * Stops the server.
   */
  public void shutdown() {
    this.logger.log("Received a request to shut down from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
    System.out.println("Server is shutting down...");
    this.socket.close();
    this.logger.log("UDPServer stopped");
    this.logger.close();
    System.out.println("Server closed");
  }
}
