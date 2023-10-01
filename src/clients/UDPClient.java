package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * The class UDP client represents a client that communicates via the UDP protocol.
 */
public class UDPClient extends AbstractClient {
  private DatagramSocket socket;

  /**
   * Instantiates a new UDP client.
   *
   * @param hostname the hostname
   * @param port     the port
   */
  public UDPClient(String hostname, String port) {
    super(hostname, port);
  }

  /**
   * Sets the UDP socket.
   *
   * @param socket the UDP socket
   */
  private void setSocket(DatagramSocket socket) {
    this.socket = socket;
  }

  private DatagramPacket encode(String request) {
    byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
    return new DatagramPacket(requestBytes, requestBytes.length, this.address, this.portNumber);
  }

  private void send(DatagramPacket packet) throws IOException {
    this.socket.send(packet);
    this.logger.log("Sent " + "\"" + this.request + "\"" + " to the server");
  }

  private DatagramPacket receive() throws IOException {
    byte[] buffer = new byte[1000];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    this.socket.receive(packet);
    return packet;
  }

  private String decode(DatagramPacket packet) {
    String reply = new String(packet.getData());
    this.logger.log("Received " + "\"" + reply.trim() + "\"" + " from the server");
    return reply;
  }

  /**
   * Starts the client.
   */
  @Override
  public void execute() {
    try {
      this.setSocket(new DatagramSocket()); // open a new UDP socket
      this.logger.log("UDPClient running...");
    } catch (SocketException e) {
      this.logger.log("Socket: " + e.getMessage());
    }
    if (this.socket != null) { // if a socket could be opened
      boolean isRunning = true;
      this.logger.log("Socket opened. UDPClient running...");
      while(isRunning) {
        try {
          this.setRequest(this.getRequest()); // get and update the user request
          if (this.request.equalsIgnoreCase("client shutdown") || this.request.equalsIgnoreCase("client stop")) { // if the user wants to quit
            isRunning = false; // prepare the shutdown process
          } else {
            this.send(this.encode(this.request)); // send the request
            System.out.println("Request sent");
            this.socket.setSoTimeout(5000); // set a 5-second timeout for receiving a response
            try {
              System.out.println(this.decode(this.receive())); // print the server's response
            } catch (SocketTimeoutException e) { // if the server is unresponsive
              this.logger.log("Request timed out: " + this.request);
              System.out.println("Request timed out. Please try again");
            }
          }
        } catch (IOException e) {
          this.logger.log("IO: " + e.getMessage());
        }
      }
    }
    // shut down gracefully
    this.shutdown();
  }

  /**
   * Stops the client.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down");
    System.out.println("Client is shutting down...");
    this.socket.close();
    this.scanner.close();
    this.logger.log("UDPClient stopped");
    this.logger.close();
    System.out.println("Client closed");
  }
}
