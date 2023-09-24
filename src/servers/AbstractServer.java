package servers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
  private Logger logger;
  /**
   * The file handler.
   */
  protected FileHandler fileHandler;
  private static final String format = "MM-dd-yyyy HH:mm:ss.SSS";

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
    this.createLog(loggerName, logFileName);
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

  private void createLog(String loggerName, String logFileName) {
    this.logger = Logger.getLogger(loggerName);
    try {
      this.fileHandler = new FileHandler(logFileName); // create a file handler to write log messages to a file
      this.fileHandler.setFormatter(new SimpleFormatter() { // create a custom formatter for millisecond precision timestamp
        public String format(LogRecord record) {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AbstractServer.format);
          return simpleDateFormat.format(System.currentTimeMillis()) + " - " + record.getMessage() + "\n";
        }
      });
      this.logger.addHandler(this.fileHandler); // add the file handler to the logger
    } catch (IOException e) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AbstractServer.format);
      System.err.println(simpleDateFormat.format(System.currentTimeMillis()) + " - " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Logs any event that occurs.
   *
   * @param msg the message to be logged
   */
  public void log(String msg) {
    this.logger.info(msg); // log a message with millisecond precision timestamp
  }
}
