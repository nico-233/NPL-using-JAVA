package com.sonnetgenerator.analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.sonnetgenerator.analyzer.Collector
 *
 * This is used to read in all the natural language texts.
 *
 */
public class Collector {
  private final Logger logger = new Logger();
  public TagContent tagger = new TagContent();
  public Map<String, String> rhymeDict;
  public Map<String, String> pentameterDict;
  public Set<String> rhymeSet;

  /**
   * We assume that the text input is pre-processed already, thus the following requirements must
   * be satisfied:
   * 1, A line of "====" followed by "Shakesword start" indicates the start line of the poems;
   * 2, For a piece of poem, a one line topic should be the start, followed by at least one space
   *   line.
   * 3, A line of words could have some space as prefix or suffix, if they do, we will remove them;
   * 4, A line of words may contain "[", "]", "(", ")", "{", "}", we will remove these signs with
   *   the content between them will be also removed;
   * 5, A line of words may contain numbers, we just remove the number, especially if it is at the
   *   end of a line;
   * 6, A line of "====" followed by "Shakesword end" indicates the end line of the poems.
   */
  public void readSourcesFromFile(String fileName) throws RuntimeException, IOException {
    // This will reference one line at a time
    String line = null;

    logger.log("Reading file " + fileName);

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((line = bufferedReader.readLine()) != null) {
        if (line.equals("====Shakesword start")) {
          break;
        }
      }

      while ((line = bufferedReader.readLine()) != null) {
        if (line.equals("====Shakesword end")) {
          break;
        }
        //Trim the line
        line = line.trim();
        List<Pattern> patterns =
            Arrays.asList(
                Pattern.compile("\\[.*\\]"),
                Pattern.compile("\\(.*\\)"),
                Pattern.compile("\\{.*\\}"),
                Pattern.compile("\\d+"));
        for (Pattern pattern : patterns) {
          Matcher matcher = pattern.matcher(line);
          while (matcher.find()) {
            String matched = line.substring(matcher.start(), matcher.end());
            line = line.replace(matched, "");
            matcher = pattern.matcher(line);
          }
        }
        line = line.replaceAll(" +", " ");
        if (line.contains("-") || line.contains("_") || line.contains("^")
            || line.contains("(") || line.contains(")") || line.contains("[")
            || line.contains("]") || line.contains("{") || line.contains("}")
            || ((Arrays.asList(line.split(" ")).size() < 5) && !line.equals(""))) {
          continue;
        }
        //call tagger
        tagger.tagALine(line);
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      logger.log("Unable to open file '" + fileName + "'");
    } catch (IOException ex) {
      logger.log("Error reading file '" + fileName + "'");
    }
  }

  public void readCmuDict() throws RuntimeException, IOException {
    // This will read the CMU dict
    String fileName = "cmudict.dict";

    // This will reference one line at a time
    String line = null;

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((line = bufferedReader.readLine()) != null) {
        List<String> symbols = Arrays.asList(line.split(" "));
        // Read this file, save all the pronounciation to a dictionary
        // Use the word as a key, and the rhyme as the value
        if (symbols.size() < 2) {
          throw new RuntimeException("Invalid Input Symbols: " + symbols);
        }
        String keyWord = symbols.get(0);
        String fullRyhmeThisWord = "-";
        String fullPentameter = "";
        // Now we find the rhyme of this word, starting form the last "A" "E" "I" "O" "U" symbol.
        // For example:
        // abandon AH0 B AE1 N D AH0 N
        // then abandon's full rhyme should be AH0-N
        for (String symbol : symbols) {
          if (rhymeSet.contains(symbol)) {
            fullRyhmeThisWord = symbol;
            fullPentameter += "[" + symbol + "]";
          } else {
            fullRyhmeThisWord += "-" + symbol;
          }
        }
        rhymeDict.put(keyWord, fullRyhmeThisWord);
        pentameterDict.put(keyWord, fullPentameter);
        logger.log(keyWord + " has a rhyme: " + fullRyhmeThisWord);
        logger.log(keyWord + " has a pentameter: " + fullPentameter);
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      logger.log("Unable to open file '" + fileName + "'");
    } catch (IOException ex) {
      logger.log("Error reading file '" + fileName + "'");
    }
  }

  public void readCmuRhymeSet() throws RuntimeException, IOException {
    // This will read the CMU dict
    String fileName = "cmudict.symbols";

    // This will reference one line at a time
    String symbol = null;

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((symbol = bufferedReader.readLine()) != null) {
        // Read this file, each vocal symbol a line
        // If the symbol starts with "A" "E" "I" "O" "U", then we regard it as a rhyme
        if (symbol.startsWith("A") || symbol.startsWith("E") || symbol.startsWith("I")
                || symbol.startsWith("O") || symbol.startsWith("U")) {
          rhymeSet.add(symbol);
          logger.log("Adding rhyme " + symbol);
        }
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      logger.log("Unable to open file '" + fileName + "'");
    } catch (IOException ex) {
      logger.log("Error reading file '" + fileName + "'");
    }
  }

  public Collector() throws RuntimeException, IOException {
    rhymeDict = new HashMap<>();
    pentameterDict = new HashMap<>();
    rhymeSet = new HashSet<>();
    //initialize tagger
    tagger.readTags();
    readCmuRhymeSet();
    readCmuDict();
    //read resource file
    readSourcesFromFile("20158-0.txt");
    readSourcesFromFile("21141-0.txt");
    readSourcesFromFile("3238-0.txt");
    readSourcesFromFile("19978-0.txt");
    readSourcesFromFile("49716-0.txt");
    readSourcesFromFile("56244-0.txt");
    readSourcesFromFile("60454-0.txt");
  }
}
