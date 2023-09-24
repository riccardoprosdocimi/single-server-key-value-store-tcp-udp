package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UDPClient implements IClient {
  private InetAddress hostname;
  private int port;
  private DatagramSocket socket = null;
  private final Scanner scanner = new Scanner(System.in);
  private final Logger logger;

  public UDPClient() {
    this.logger = Logger.getLogger("UDPClient logger");
    try {
      FileHandler fileHandler = new FileHandler("UDPClientLog.log"); // create a file handler to write log messages to a file
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

  public void log(String msg) {
    this.logger.info(msg); // log a message with millisecond precision timestamp
  }

  public void getHostname(String address) {
    try {
      this.hostname = InetAddress.getByName(address);
    } catch (UnknownHostException e) {
      System.err.println("Unknown host entered. Client is shutting down...");
      this.socket.close();
      this.scanner.close();
      this.log("Unknown host entered by the user: " + address);
      System.out.println("Client closed");
      System.exit(0);
    }
  }


  public void getPort(int portNumber) {
    if (portNumber >= 1024) {
      this.port = portNumber;
    } else {
      System.err.println("Invalid port entered. Client is shutting down...");
      this.socket.close();
      this.scanner.close();
      this.log("Invalid port entered by the user: " + portNumber);
      System.out.println("Client closed");
      System.exit(0);
    }
  }


  public String getMessage() {
    System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
    return this.scanner.nextLine();
  }


  public void send(String msg, String hostname, String port) throws IOException {
    byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
    this.getHostname(hostname);
    this.getPort(Integer.parseInt(port));
    DatagramPacket request = new DatagramPacket(msgBytes, msgBytes.length, this.hostname, this.port);
    this.socket.send(request);
    this.log("Sent " + "\"" + msg + "\"" + " to the server");
  }


  public void receive() throws IOException {
    byte[] buffer = new byte[1000];
    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
    this.socket.receive(reply);
    String replyString = new String(reply.getData());
    System.out.println("Reply: " + replyString);
    this.log("Received " + "\"" + replyString.trim() + "\"" + " from the server");
  }


  public void execute(String hostname, String port) {
    try {
      this.socket = new DatagramSocket();
      boolean isRunning = true;
      while(isRunning) {
        String userInput = this.getMessage();
        if (userInput.equalsIgnoreCase("client shutdown") || userInput.equalsIgnoreCase("client stop")) {
          isRunning = false;
        } else {
          this.send(userInput, hostname, port);
          System.out.println("Request sent");
          this.socket.setSoTimeout(5000); // set a 5-second timeout for receiving a response
          try {
            this.receive();
          } catch (SocketTimeoutException e) {
            System.out.println("Request timed out. Please try again");
            this.log("Request timed out: " + userInput);
          }
        }
      }
      System.out.println("Client is shutting down...");
      this.socket.close();
      this.scanner.close();
      this.log("Received a request to shut down from the user");
      System.out.println("Client closed");
    } catch (SocketException e) {
      this.log("Socket: " + e.getMessage());
    } catch (IOException e) {
      this.log("IO: " + e.getMessage());
    }
  }
}
