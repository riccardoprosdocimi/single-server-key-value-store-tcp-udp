package servers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class TCP server represents a translation server that communicates via the TCP protocol.
 */
public class TCPServer extends AbstractServer {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private int packetSize;
  private BufferedReader reader;
  private BufferedWriter writer;

  /**
   * Instantiates a new TCP translation server.
   *
   * @param port the port
   */
  public TCPServer(String port) {
    super(port);
  }

  /**
   * Sets the server socket.
   *
   * @param serverSocket the server socket
   */
  private void setServerSocket(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  /**
   * Sets the TCP socket.
   *
   * @param clientSocket the TCP socket
   */
  private void setClientSocket(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  /**
   * Sets the buffer reader.
   *
   * @param reader the buffer reader
   */
  private void setReader(BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * Sets the buffer writer.
   *
   * @param writer the buffer writer
   */
  private void setWriter(BufferedWriter writer) {
    this.writer = writer;
  }

  /**
   * Sets the packet size.
   *
   * @param packetSize the packet size
   */
  private void setPacketSize(int packetSize) {
    this.packetSize = packetSize;
  }

  private String receive() throws IOException {
    this.setReader(new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream())));
    String request = this.reader.readLine();
    this.setPacketSize(request.getBytes().length);
    return request;
  }

  private void send(String msg) throws IOException {
    this.setWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())));
    this.writer.write(msg);
    this.writer.newLine();
    this.writer.flush();
  }

  /**
   * Starts the server.
   */
  @Override
  public void execute() {
    try {
      this.setServerSocket(new ServerSocket(this.portNumber)); // create a new TCP server socket
      boolean isRunning = true;
      this.logger.log("TCPServer running...");
      System.out.println("Server is running...");
      this.setClientSocket(serverSocket.accept()); // wait for client's connection
      this.logger.log("Connection with " + this.clientSocket.getInetAddress() + " established");
      System.out.println("Connection established");
      while (isRunning) { // keep running
        try {
          String request = this.receive(); // get the incoming requests
          if (request.equalsIgnoreCase("server shutdown") || request.equalsIgnoreCase("server stop")) { // if the client sends a stop/shutdown request
            this.send("Server is shutting down..."); // acknowledge
            isRunning = false; // prepare the shutdown process
          } else {
            String reply = this.parseExecution(request, this.clientSocket, this.packetSize);
            this.send(reply); // process the request and send the result back to the client
            this.logger.log("Responded with " + reply); // log the response
          }
        } catch (IOException e) {
          System.out.println("IO: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.out.println("Socket: " + e.getMessage());
    }
    // shut down gracefully
    this.shutdown();
  }

  /**
   * Stops the server.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down from <" + this.clientSocket.getInetAddress() + ">:<" + this.clientSocket.getPort() + ">");
    System.out.println("Server is shutting down...");
    try {
      this.reader.close();
    } catch (IOException r) {
      this.logger.log("Reader: " + r.getMessage());
    }
    try {
      this.writer.close();
    } catch (IOException w) {
      this.logger.log("Writer: " + w.getMessage());
    }
    try {
      this.serverSocket.close();
    } catch (IOException ss) {
      this.logger.log("Server socket: " + ss.getMessage());
    }
    try {
      this.clientSocket.close();
    } catch (IOException cs) {
      this.logger.log("Client socket: " + cs.getMessage());
    }
    this.logger.log("TCPServer stopped");
    this.logger.close();
    System.out.println("Server closed");
  }
}
