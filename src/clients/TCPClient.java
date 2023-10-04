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
   * Pre-populates the key-value store.
   */
  @Override
  protected void prePopulate() {
    try {
      this.setRequest("put:hello:ciao");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:goodbye:addio");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:thank you:grazie");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:please:per favore");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:yes:sì");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:no:no");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:water:acqua");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:food:cibo");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:friend:amico");
      this.send();
      System.out.println(this.receive());
      this.setRequest("put:love:amore");
      this.send();
      System.out.println(this.receive());
    } catch (IOException e) {
      this.logger.log("IO (pre-populate): " + e.getMessage());
    }
  }

  /**
   * Starts the client.
   */
  @Override
  public void execute() {
    int i = 3;
    while (this.socket == null) { // try to establish a connection 3 times
      try {
        this.setSocket(new Socket(this.address, this.portNumber)); // open a new TCP socket
      } catch (IOException e) {
        this.logger.log("IO: " + e.getMessage());
        System.out.println("Connection failed");
        i--;
        if (i == 0) {
          break;
        }
        try {
          Thread.sleep(5000); // wait 5 seconds before retrying
        } catch (InterruptedException ignored) {
        }
      }
    }
    if (this.socket != null) { // if a connection was established
      boolean isRunning = true;
      this.logger.log("Connection established. TCPClient running...");
      this.logger.log("Pre-populating...");
      this.prePopulate(); // pre-populate the key-value store
      this.logger.log("Pre-population completed");
      while (isRunning) {
        try {
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
