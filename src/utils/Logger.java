package utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * This class represents a logger.
 */
public class Logger implements ILogger {
  private java.util.logging.Logger logger;
  private FileHandler fileHandler;
  private static final String format = "MM-dd-yyyy HH:mm:ss.SSS";

  /**
   * Instantiates a new logger.
   *
   * @param loggerName  the logger name
   * @param logFileName the log file name
   */
  public Logger(String loggerName, String logFileName) {
    this.createLog(loggerName, logFileName);
  }

  private void createLog(String loggerName, String logFileName) {
    this.logger = java.util.logging.Logger.getLogger(loggerName);
    try {
      this.fileHandler = new FileHandler(logFileName, true); // create a file handler to write log messages to a file
      this.fileHandler.setFormatter(new SimpleFormatter() { // create a custom formatter for millisecond precision timestamp
        public String format(LogRecord record) {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.format);
          return simpleDateFormat.format(System.currentTimeMillis()) + " - " + record.getMessage() + "\n";
        }
      });
      this.logger.addHandler(this.fileHandler); // add the file handler to the logger
    } catch (IOException e) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.format);
      System.err.println(simpleDateFormat.format(System.currentTimeMillis()) + " - " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Logs any event that occurs.
   *
   * @param msg the message to be logged
   */
  @Override
  public void log(String msg) {
    this.logger.info(msg); // log a message with millisecond precision timestamp
  }

  /**
   * Shuts down the logger.
   */
  @Override
  public void close() {
    this.fileHandler.close();
  }
}
