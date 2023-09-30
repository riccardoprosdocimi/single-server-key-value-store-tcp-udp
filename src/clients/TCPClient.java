package clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The class TCP client represents a client that communicates via the TCP protocol.
 */
public class TCPClient extends AbstractClient {
  private Socket socket;
  private BufferedWriter writer;
  private BufferedReader reader;

  /**
   * Instantiates a new TCP client.
   *
   * @param hostname the hostname
   * @param port     the port
   */
  public TCPClient(String hostname, String port) {
    super(hostname, port);
  }

  /**
   * Sets the TCP socket.
   *
   * @param socket the TCP socket
   */
  private void setSocket(Socket socket) {
    this.socket = socket;
  }

  private void setWriter(BufferedWriter writer) {
    this.writer = writer;
  }

  private void setReader(BufferedReader reader) {
    this.reader = reader;
  }

  private void send() throws IOException {
    this.setWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())));
    this.writer.write(this.request);
    this.writer.newLine();
    this.writer.flush();
    this.logger.log("Sent " + "\"" + this.request + "\"" + " to the server");
  }

  private String receive() throws IOException {
    this.setReader(new BufferedReader(new InputStreamReader(this.socket.getInputStream())));
    String reply = this.reader.readLine();
    this.logger.log("Received " + "\"" + reply.trim() + "\"" + " from the server");
    return reply;
  }

  /**
   * Starts the client.
   */
  @Override
  public void execute() {
    try {
      while (this.socket == null) {
        try {
          this.setSocket(new Socket(this.address, this.portNumber)); // open a new TCP socket
        } catch (IOException e) {
          this.logger.log("IO: " + e.getMessage());
          try {
            Thread.sleep(5000);
          } catch (InterruptedException ignored) {
          }
        }
      }
      boolean isRunning = true;
      this.logger.log("TCPClient running...");
      while (isRunning) {
        this.setRequest(this.getRequest()); // get and update the user request
        if (this.request.equalsIgnoreCase("client shutdown") || this.request.equalsIgnoreCase("client stop")) { // if the user wants to quit
          isRunning = false; // prepare the shutdown process
        } else {
          this.send();
          System.out.println("Request sent");
          this.socket.setSoTimeout(5000); // set a 5-second timeout for receiving a response
          try {
            System.out.println(this.receive()); // print the server's response
          } catch (SocketTimeoutException e) { // if the server is unresponsive
            System.out.println("Request timed out. Please try again");
            this.logger.log("Request timed out: " + this.request);
          }
        }
      }
    } catch (IOException e) {
      this.logger.log("IO: " + e.getMessage());
    }
    // shut down gracefully
    this.shutdown();
  }

  /**
   * Stops the client.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down...");
    System.out.println("Client is shutting down...");
    this.scanner.close();
    if (this.socket != null) {
      try {
        this.socket.close();
      } catch (IOException s) {
        this.logger.log("Socket: " + s.getMessage());
      }
    }
    if (this.writer != null) {
      try {
        this.writer.close();
      } catch (IOException w) {
        this.logger.log("Writer: " + w.getMessage());
      }
    }
    if (this.reader != null) {
      try {
        this.reader.close();
      } catch (IOException r) {
        this.logger.log("Reader: " + r.getMessage());
      }
    }
    this.logger.log("TCPClient stopped");
    this.logger.close();
    System.out.println("Client closed");
  }
}
