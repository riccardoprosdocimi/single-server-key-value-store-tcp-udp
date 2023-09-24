package servers;

import java.net.DatagramPacket;
import java.net.SocketException;

/**
 * The interface Server contains methods that all types of dictionary servers should support.
 */
public interface IServer {
  /**
   * Binds the port number to the server's socket.
   *
   * @param port the port number
   */
  void bindPort(String port) throws SocketException;

  /**
   * Starts the server.
   */
  void execute(String port);

  /**
   * Logs the requests received and any errors that occur.
   *
   * @param msg the message to be logged
   */
  void log(String msg);

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the English word to be translated
   * @param value the Italian translation
   * @return the outcome of the operation
   */
  String put(String key, String value);

  /**
   * Retrieves the value.
   *
   * @param key the English word
   * @return the Italian word
   */
  String get(String key);

  /**
   * Deletes a key-value pair.
   *
   * @param key the English word
   * @return the outcome of the operation
   */
  String delete(String key);

  /**
   * Decodes a UDP packet.
   *
   * @param packet the UDP packet
   * @return the message
   */
  String decode(DatagramPacket packet);

  /**
   * Encodes a UDP packet.
   *
   * @param reply the message
   * @return the UDP packet
   */
  DatagramPacket encode(String reply);
}
