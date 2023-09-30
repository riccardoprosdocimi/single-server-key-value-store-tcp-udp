package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * The class UDP server represents a translation server that communicates via the UDP protocol.
 */
public class UDPServer extends AbstractServer {
  private DatagramSocket socket;
  private DatagramPacket request;

  /**
   * Instantiates a new UDP translation server.
   *
   * @param port the port
   */
  public UDPServer(String port) {
    super(port);
  }

  /**
   * Sets the UDP socket.
   *
   * @param socket the UDP socket
   */
  private void setSocket(DatagramSocket socket) {
    this.socket = socket;
  }

  /**
   * Sets the request.
   *
   * @param request the request
   */
  private void setRequest(DatagramPacket request) {
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
  @Override
  public void execute() {
    try {
      this.setSocket(new DatagramSocket(this.portNumber)); // open a new UDP socket
      byte[] buffer = new byte[8000]; // prepare the buffer for incoming messages
      boolean isRunning = true;
      this.logger.log("UDPServer running...");
      System.out.println("Server is running...");
      while (isRunning) { // keep running
        this.setRequest(new DatagramPacket(buffer, buffer.length)); // prepare the incoming request for processing
        this.socket.receive(this.request); // get the incoming request
        String request = this.decode(this.request); // convert bytes to string
        if (request.equalsIgnoreCase("server shutdown") || request.equalsIgnoreCase("server stop")) { // if the client sends a stop/shutdown request
          this.socket.send(this.encode("Server is shutting down...")); // acknowledge
          isRunning = false; // prepare the shutdown process
        } else {
          String reply = this.parseExecution(request, this.request); // process the request
          this.socket.send(this.encode(reply)); // send the result back to the client
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
   * Stops the server.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down from <" + this.request.getAddress() + ">:<" + this.request.getPort() + ">");
    System.out.println("Server is shutting down...");
    this.socket.close();
    this.logger.log("UDPServer stopped");
    this.logger.close();
    System.out.println("Server closed");
  }
}
