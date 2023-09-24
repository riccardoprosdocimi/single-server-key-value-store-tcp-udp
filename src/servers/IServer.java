package servers;

/**
 * The interface Server contains methods that all types of translation servers should support.
 */
public interface IServer {
  /**
   * Sets the port number.
   *
   * @param port the port
   */
  void setPortNumber(String port);

  /**
   * Sets the translation service.
   *
   * @param translationService the translation service
   */
  void setTranslationService(ITranslationService translationService);

  /**
   * Starts the server.
   */
  void execute();

  /**
   * Logs any event that occurs.
   *
   * @param msg the message to be logged
   */
  void log(String msg);

  /**
   * Stops the server.
   */
  void shutdown();
}
