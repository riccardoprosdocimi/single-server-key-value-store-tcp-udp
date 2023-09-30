package utils;

/**
 * The interface logger contains methods that all types of loggers should support.
 */
public interface ILogger {
  /**
   * Logs any event that occurs.
   *
   * @param msg the message to be logged
   */
  void log(String msg);

  /**
   * Shuts down the logger.
   */
  void close();
}
