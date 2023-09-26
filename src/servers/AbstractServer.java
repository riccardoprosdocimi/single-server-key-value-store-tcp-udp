package servers;

import utils.ILogger;
import utils.Logger;

/**
 * The class abstract server contains attributes and methods for all types of translation servers.
 */
public abstract class AbstractServer implements IServer {
  /**
   * The port number.
   */
  protected int portNumber;
  /**
   * The translation service.
   */
  protected ITranslationService translationService;
  /**
   * The logger.
   */
  protected ILogger logger;

  /**
   * Instantiates a new abstract translation server.
   *
   * @param port               the port
   * @param translationService the translation service
   * @param loggerName         the logger name
   * @param logFileName        the log file name
   */
  protected AbstractServer(String port, ITranslationService translationService, String loggerName, String logFileName) {
    this.setPortNumber(port);
    this.setTranslationService(translationService);
    this.logger = new Logger(loggerName, logFileName);
  }

  /**
   * Sets the port number.
   *
   * @param port the port
   */
  public void setPortNumber(String port) {
    this.portNumber = Integer.parseInt(port);
  }

  /**
   * Sets the translation service.
   *
   * @param translationService the translation service
   */
  public void setTranslationService(ITranslationService translationService) {
    this.translationService = translationService;
  }
}
