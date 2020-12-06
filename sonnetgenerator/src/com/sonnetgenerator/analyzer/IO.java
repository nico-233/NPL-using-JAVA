package com.sonnetgenerator.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.RuntimeException;
import java.lang.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * com.sonnetgenerator.analyzer.IO controls outstream and instream for the Shakesword program.
 *
 * Theoretically, we could replace the com.sonnetgenerator.analyzer.IO by a graphic interface without too much change of the rest
 * of code.
 *
 * This com.sonnetgenerator.analyzer.IO version will be based on console conversation (more for testing purpose).
 */
public class IO {

  private String defaultFileName = "default.txt";

  /**
   * Reads text from a file from local file system.
   *
   * @param fileName the name of the file.
   *
   * Returns the 2D list of String read from the file.
   */
  public List<List<String>> readTextFromLocalFile(String fileName) {
    // Use the default file name when there is no file name assigned.
    if (fileName == null) {
      fileName = defaultFileName;
    }

    List<List<String>> output = new ArrayList<List<String>>();

    // This will reference one line at a time
    String line = null;

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((line = bufferedReader.readLine()) != null) {
        // Works with words separated by ", ", the config files should be written in this format.
        List<String> words = Arrays.asList(line.split(",[ ]*"));
        output.add(words);
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      System.out.println("Unable to open file '" + fileName + "'");
    } catch (IOException ex) {
      System.out.println("Error reading file '" + fileName + "'");
    }
    return output;
  }

  /**
   * Reads lines from a file from local file system.
   *
   * @param fileName the name of the file.
   *
   * Returns the list of String lines read from the file.
   */
  public List<String> readLinesFromLocalFile(String fileName) {
    // Use the default file name when there is no file name assigned.
    if (fileName == null) {
      fileName = defaultFileName;
    }

    List<String> output = new ArrayList<String>();

    // This will reference one line at a time
    String line = null;

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((line = bufferedReader.readLine()) != null) {
        // Reads the whole line of text and adds it to the output list.
        output.add(line);
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      System.out.println("Unable to open file '" + fileName + "'");
    } catch (IOException ex) {
      System.out.println("Error reading file '" + fileName + "'");
    }
    return output;
  }

  /**
   * Writes line to a file at local file system.
   *
   * @param line String lines to write.
   * @param fileName the name of the file.
   * @param append if you want to append lines to the existing file.
   */
  public void writeLineToLocalFile(String line, String fileName, Boolean append) {
    // Use the default file name when there is no file name assigned.
    if (fileName == null) {
      fileName = defaultFileName;
    }

    try {
      File file = new File(fileName);

      // if file doesn't exists, then create it
      if (!file.exists()) {
        file.createNewFile();
      }

      // FileReader reads text files in the default encoding, true = append file
      FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), append);

      // Always wrap FileReader in BufferedReader.
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      bufferedWriter.write(line + '\n');

      // Always close files.
      bufferedWriter.close();
    } catch (FileNotFoundException ex) {
      System.out.println("Unable to open file " + fileName);
    } catch (IOException ex) {
      System.out.println("Error writing file " + fileName);
    }
  }

  /**
   * Writes line to the console.
   *
   * @param line String lines to print.
   */
  public void printLineToScreen(String line) {
    System.out.println("Sonnet Generator Log: " + line);
  }

  /**
   * Read a line from console.
   *
   * @param enterText String for telling user what to input.
   */
  public String readLineFromConsole(String enterText) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, UTF_8));
    System.out.print(enterText);
    return reader.readLine();
  }

}
