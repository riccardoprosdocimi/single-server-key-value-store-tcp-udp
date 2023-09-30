package servers;

import java.net.DatagramPacket;
import java.net.Socket;

import utils.ILogger;

/**
 * The class abstract server contains attributes and methods for all types of translation servers.
 */
public abstract class AbstractServer implements IServer {
  /**
   * The port number.
   */
  protected int portNumber;
  /**
   * The translation service.
   */
  protected ITranslationService translationService;
  /**
   * The logger.
   */
  protected ILogger logger;

  /**
   * Instantiates a new abstract translation server.
   *
   * @param port the port
   */
  protected AbstractServer(String port) {
    this.setPortNumber(port);
  }

  /**
   * Sets the port number.
   *
   * @param port the port
   */
  @Override
  public void setPortNumber(String port) {
    int portNumber = Integer.parseInt(port);
    if (portNumber >= 49152 && portNumber <= 65535) {
      this.portNumber = portNumber;
    } else {
      System.err.println("Invalid port entered");
      this.logger.log("Invalid port entered by the user: " + portNumber);
      this.logger.close();
      System.exit(1);
    }
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
   * Sets the translation service.
   *
   * @param translationService the translation service
   */
  @Override
  public void setTranslationService(ITranslationService translationService) {
    this.translationService = translationService;
  }

  /**
   * Parses the client's request. The predefined protocol for the translation operation is
   * PUT/GET/DELETE:key:value[with PUT only].
   *
   * @param request the operation the server needs to execute
   * @param packet  the UDP packet
   * @return the result of the operation
   */
  protected String parseExecution(String request, DatagramPacket packet) {
    String result;
    String[] elements = request.split(":");
    if (elements.length < 2 || elements.length > 3) { // the protocol is not followed
      this.logger.log("Received malformed request of length " + packet.getLength() + " from " + "<" + packet.getAddress() + ">:<" + packet.getPort() + ">");
      return "FAIL: the server received a malformed request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
    } else {
      String operation;
      try {
        operation = elements[0]; // PUT/GET/DELETE
      } catch (Exception e) {
        this.logger.log("Parsing error: operation. Request of length " + packet.getLength() + " from " + "<" + packet.getAddress() + ">:<" + packet.getPort() + ">");
        return "FAIL: the server could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String key;
      try {
        key = elements[1]; // word to be translated
      } catch (Exception e) {
        this.logger.log("Parsing error: key. Request of length " + packet.getLength() + " from " + "<" + packet.getAddress() + ">:<" + packet.getPort() + ">");
        return "FAIL: the server could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String value;
      switch (operation.toUpperCase()) {
        case "PUT":
          try {
            value = elements[2]; // word to translate
            this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\" from <" + packet.getAddress() + ">:<" + packet.getPort() + ">");
          } catch (Exception e) {
            this.logger.log("Parsing error: value. Request of length " + packet.getLength() + " from " + "<" + packet.getAddress() + ">:<" + packet.getPort() + ">");
            return "FAIL: the server could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
          }
          result = this.translationService.put(key, value);
          this.logger.log("Responded with " + result);
          break;
        case "GET":
          result = this.translationService.get(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to retrieve the value mapped to a nonexistent key " + "\"" + key + "\" " + "from <" + packet.getAddress() + ">:<" + packet.getPort() + ">");
          } else {
            this.logger.log("Received a request to retrieve the value mapped to " + "\"" + key + "\" " + "from <" + packet.getAddress() + ">:<" + packet.getPort() + ">");
          }
          this.logger.log("Responded with " + result);
          break;
        case "DELETE":
          result = this.translationService.delete(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to delete a nonexistent key-value pair associated with " + "\"" + key + "\" " + "from <" + packet.getAddress() + ">:<" + packet.getPort() + ">");
          } else {
            this.logger.log("Received a request to delete the key-value pair associated with " + "\"" + key + "\" " + "from <" + packet.getAddress() + ">:<" + packet.getPort() + ">");
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
   * Parses the client's request. The predefined protocol for the translation operation is
   * PUT/GET/DELETE:key:value[with PUT only].
   *
   * @param request    the operation the server needs to execute
   * @param socket     the TCP socket
   * @param packetSize the packet size
   * @return the result of the operation
   */
  protected String parseExecution(String request, Socket socket, int packetSize) {
    String result;
    String[] elements = request.split(":");
    if (elements.length < 2 || elements.length > 3) { // the protocol is not followed
      this.logger.log("Received malformed request of length " + packetSize + " from " + "<" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
      return "FAIL: the server received a malformed request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
    } else {
      String operation;
      try {
        operation = elements[0]; // PUT/GET/DELETE
      } catch (Exception e) {
        this.logger.log("Parsing error: operation. Request of length " + packetSize + " from " + "<" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
        return "FAIL: the server could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String key;
      try {
        key = elements[1]; // word to be translated
      } catch (Exception e) {
        this.logger.log("Parsing error: key. Request of length " + packetSize + " from " + "<" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
        return "FAIL: the server could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
      }
      String value;
      switch (operation.toUpperCase()) {
        case "PUT":
          try {
            value = elements[2]; // word to translate
            this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\" from <" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
          } catch (Exception e) {
            this.logger.log("Parsing error: value. Request of length " + packetSize + " from " + "<" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
            return "FAIL: the server could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again\n";
          }
          result = this.translationService.put(key, value);
          this.logger.log("Responded with " + result);
          break;
        case "GET":
          result = this.translationService.get(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to retrieve the value mapped to a nonexistent key " + "\"" + key + "\" " + "from <" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
          } else {
            this.logger.log("Received a request to retrieve the value mapped to " + "\"" + key + "\" " + "from <" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
          }
          this.logger.log("Responded with " + result);
          break;
        case "DELETE":
          result = this.translationService.delete(key);
          if (result.startsWith("FAIL:")) {
            this.logger.log("Received a request to delete a nonexistent key-value pair associated with " + "\"" + key + "\" " + "from <" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
          } else {
            this.logger.log("Received a request to delete the key-value pair associated with " + "\"" + key + "\" " + "from <" + socket.getInetAddress() + ">:<" + socket.getPort() + ">");
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
}
