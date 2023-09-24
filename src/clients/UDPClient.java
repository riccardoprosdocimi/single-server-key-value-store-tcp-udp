package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * This class represents a client that communicates via the UDP protocol.
 */
public class UDPClient extends AbstractClient {
  private DatagramSocket socket;

  /**
   * Instantiates a new UDP client.
   *
   * @param hostname    the hostname
   * @param port        the port
   * @param loggerName  the logger name
   * @param logFileName the log file name
   */
  public UDPClient(String hostname, String port, String loggerName, String logFileName) {
    super(loggerName, logFileName);
    InetAddress address = this.getHostname(hostname);
    int portNumber = this.getPort(port);
    if (address == null || portNumber == -1) {
      this.shutdown();
    } else {
      this.setAddress(address);
      this.setPortNumber(portNumber);
    }
  }

  /**
   * Sets the UDP socket.
   *
   * @param socket the UDP socket
   */
  void setSocket(DatagramSocket socket) {
    this.socket = socket;
  }

  private DatagramPacket encode(String request) {
    byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
    return new DatagramPacket(requestBytes, requestBytes.length, this.address, this.portNumber);
  }

  /**
   * Sends a UDP packet.
   *
   * @param packet the UDP packet
   * @throws IOException the IO exception
   */
  public void send(DatagramPacket packet) throws IOException {
    this.socket.send(packet);
    this.log("Sent " + "\"" + this.request + "\"" + " to the server");
  }

  /**
   * Receives a UDP packet.
   *
   * @return the UDP packet
   * @throws IOException the IO exception
   */
  public DatagramPacket receive() throws IOException {
    byte[] buffer = new byte[1000];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    this.socket.receive(packet);
    return packet;
  }

  private String decode(DatagramPacket packet) {
    String reply = new String(packet.getData());
    this.log("Received " + "\"" + reply.trim() + "\"" + " from the server");
    return reply;
  }

  /**
   * Starts the client.
   */
  public void execute() {
    try {
      this.setSocket(new DatagramSocket()); // open a new UDP socket
      boolean isRunning = true;
      this.log("UDPClient running...");
      while(isRunning) {
        this.setRequest(this.getRequest()); // get and update the user requests
        if (this.request.equalsIgnoreCase("client shutdown") || this.request.equalsIgnoreCase("client stop")) { // if the user wants to quit
          isRunning = false; // prepare the shutdown process
        } else {
          this.send(this.encode(this.request)); // send the request
          System.out.println("Request sent");
          this.socket.setSoTimeout(5000); // set a 5-second timeout for receiving a response
          try {
            System.out.println(this.decode(this.receive())); // print the server's response
          } catch (SocketTimeoutException e) { // if the server is unresponsive
            System.out.println("Request timed out. Please try again");
            this.log("Request timed out: " + this.request);
          }
        }
      }
      // shut down gracefully
      this.log("Received a request to shut down from the user");
      this.shutdown();
    } catch (SocketException e) {
      this.log("Socket: " + e.getMessage());
    } catch (IOException e) {
      this.log("IO: " + e.getMessage());
    }
  }

  /**
   * Stops the client.
   */
  public void shutdown() {
    System.out.println("Client is shutting down...");
    this.socket.close();
    this.scanner.close();
    this.fileHandler.close();
    System.out.println("Client closed");
    System.exit(0);
  }
}
