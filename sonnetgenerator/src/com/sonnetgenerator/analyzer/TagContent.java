package com.sonnetgenerator.analyzer;

import com.sonnetgenerator.analyzer.IO;
import com.sonnetgenerator.analyzer.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * com.sonnetgenerator.analyzer.TagContent
 *
 * This class is used to tag a line of sonnet to a certain set of topics, like weather, season,
 * mood, background history;
 *
 * One line of sonnet can be tagged in multiple set.
 */
public class TagContent {
  private final Logger logger = new Logger();
  private IO ioInstance = new IO();
  public Map<String, Set<String>> seasonTagged;
  public Map<String, Set<String>> weatherTagged;
  public Map<String, Set<String>> historyTagged;
  public Map<String, Set<String>> moodTagged;
  public Set<String> seasonTags;
  public Set<String> weatherTags;
  public Set<String> historyTags;
  public Set<String> moodTags;

  public TagContent() {
    seasonTagged = new HashMap<>();
    weatherTagged = new HashMap<>();
    historyTagged = new HashMap<>();
    moodTagged = new HashMap<>();
    seasonTags = new HashSet<>();
    weatherTags = new HashSet<>();
    historyTags = new HashSet<>();
    moodTags = new HashSet<>();
  }

  public void readTags() throws RuntimeException, IOException {
    // This will read the poem tags
    String fileName = "tags.txt";

    // This will reference one line at a time
    String line = null;
    String tagName = null;
    String tagClass = null;

    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while ((line = bufferedReader.readLine()) != null) {
        // Read this file, each vocal symbol a line
        // If the line starts with "==Tag", it is the start of a new tag type
        if (line.startsWith("==Tag")) {
          String[] tagLine = line.split(" ");
          if (tagLine.length < 3) {
            throw new RuntimeException("Invalid Input line for tags: " + line);
          }
          tagClass = tagLine[1];
          tagName = tagLine[2];
          logger.log("Starting tag " + tagClass + ": " + tagName);
          switch (tagClass) {
            case "Season":
              seasonTags.add(tagName);
              break;
            case "Weather":
              weatherTags.add(tagName);
              break;
            case "History":
              historyTags.add(tagName);
              break;
            case "Mood":
              moodTags.add(tagName);
              break;
          }
        } else {
          // Then we assume we got a keyword and should save it into keyword dictionary
          switch (tagClass) {
            case "Season":
              if (seasonTagged.containsKey(tagName)) {
                seasonTagged.get(tagName).add(line);
              } else {
                Set<String> words = new HashSet<>();
                words.add(line);
                seasonTagged.put(tagName, words);
              }
              break;
            case "Mood":
              if (moodTagged.containsKey(tagName)) {
                moodTagged.get(tagName).add(line);
              } else {
                Set<String> words = new HashSet<>();
                words.add(line);
                moodTagged.put(tagName, words);
              }
              break;
            case "Weather":
              if (weatherTagged.containsKey(tagName)) {
                weatherTagged.get(tagName).add(line);
              } else {
                Set<String> words = new HashSet<>();
                words.add(line);
                weatherTagged.put(tagName, words);
              }
              break;
            case "History":
              if (historyTagged.containsKey(tagName)) {
                historyTagged.get(tagName).add(line);
              } else {
                Set<String> words = new HashSet<>();
                words.add(line);
                historyTagged.put(tagName, words);
              }
              break;
          }
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

  /**
   * In this function we tag a line with all the labels we collected from readTags(), so that we
   * classify all the poem lines, and they can then be used in the generator module.
   *
   * The tags includes:
   * Seasons
   * Weathers
   * Moods
   * Histories
   *
   * We use com.sonnetgenerator.analyzer.IO utilities to write to local files. The files will be named in the tagNames, like
   * Spring.txt (a season tag).
   */
  public void tagALine(String poemLine) {
    String[] words = poemLine.split("[ ,]");
    for (String word : words) {
      for (String season : seasonTags) {
        for (String tagHint : seasonTagged.get(season)) {
          if (word.equals(tagHint)) {
            ioInstance.writeLineToLocalFile(poemLine, season + ".txt", true);
          }
        }
      }
      for (String weather : weatherTags) {
        for (String tagHint : weatherTagged.get(weather)) {
          if (word.equals(tagHint)) {
            ioInstance.writeLineToLocalFile(poemLine, weather + ".txt", true);
          }
        }
      }
      for (String history : historyTags) {
        for (String tagHint : historyTagged.get(history)) {
          if (word.equals(tagHint)) {
            ioInstance.writeLineToLocalFile(poemLine, history + ".txt", true);
          }
        }
      }
      for (String mood : moodTags) {
        for (String tagHint : moodTagged.get(mood)) {
          if (word.equals(tagHint)) {
            ioInstance.writeLineToLocalFile(poemLine, mood + ".txt", true);
          }
        }
      }
    }
  }
}
