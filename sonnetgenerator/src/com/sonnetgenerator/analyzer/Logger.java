package com.sonnetgenerator.analyzer;

import com.sonnetgenerator.analyzer.IO;

/**
 * com.sonnetgenerator.analyzer.Logger records all the detailed debugging information to a local file.
 *
 * This module is the essential part for debugging process.
 */
public class Logger {

  private IO ioInstance = new IO();
  private String logFilename = "SonnetGeneratorLog.txt";

  public void log(String line) {
    // Save log to file
    ioInstance.writeLineToLocalFile(line + '\n', logFilename, true);
  }

  public void logEssential(String line) {
    // Display log line on screen
    ioInstance.printLineToScreen(line);

    // Save log "line" to file
    ioInstance.writeLineToLocalFile(line + '\n', logFilename, true);
  }
}
