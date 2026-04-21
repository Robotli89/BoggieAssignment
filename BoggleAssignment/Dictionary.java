package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Loads the word list from "wordlist.txt" into a sorted array.
 *              Provides efficient word lookup via recursive binary search.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dictionary {

    private String[] words;   // sorted array of valid words (uppercase)
    private int      wordCount;
    private int      minWordLength;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Loads the dictionary from the given file.
     * Only words at or above minWordLength and containing only A-Z are kept.
     * @param filename      path to the word list file
     * @param minWordLength minimum letters required for a valid word
     */
    public Dictionary(String filename, int minLen) {
        minWordLength = minLen;
        loadDictionary(filename);
    }

    // ---------------------------------------------------------------
    // Loading
    // ---------------------------------------------------------------

    /**
     * Reads words from the file, filters by length and alphabet, then sorts.
     * @param filename path to wordlist.txt
     */
    private void loadDictionary(String filename) {
        List<String> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toUpperCase();
                // Keep only pure alphabetic words at or above minimum length
                if (line.length() >= minWordLength && line.matches("[A-Z]+")) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load dictionary \"" + filename
                    + "\": " + e.getMessage());
            System.err.println("Word validation will fail for all entries.");
        }

        words     = list.toArray(new String[0]);
        Arrays.sort(words);        // sort for binary search
        wordCount = words.length;
        System.out.println("Dictionary loaded: " + wordCount + " words (min length = "
                + minWordLength + ").");
    }

    // ---------------------------------------------------------------
    // Word lookup — recursive binary search
    // ---------------------------------------------------------------

    /**
     * Checks whether the given word exists in the dictionary.
     * Uses recursive binary search for O(log n) performance.
     * @param word the word to look up (case-insensitive)
     * @return true if the word is a valid English word
     */
    public boolean isValidWord(String word) {
        if (word == null || word.length() < minWordLength) return false;
        return binarySearch(word.toUpperCase(), 0, wordCount - 1);
    }

    /**
     * Recursive binary search on the sorted words array.
     * @param target word to find
     * @param low    lower bound index (inclusive)
     * @param high   upper bound index (inclusive)
     * @return true if target is found
     */
    private boolean binarySearch(String target, int low, int high) {
        if (low > high) return false;                    // not found
        int mid = (low + high) / 2;
        int cmp = words[mid].compareTo(target);
        if (cmp == 0) return true;                       // found
        if (cmp > 0)  return binarySearch(target, low,     mid - 1); // search left
        return              binarySearch(target, mid + 1, high);     // search right
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** @return the sorted words array (do not modify) */
    public String[] getAllWords()     { return words; }

    /** @return the current minimum word length setting */
    public int getMinWordLength()     { return minWordLength; }

    /**
     * Updates the minimum word length filter.
     * @param min new minimum word length (e.g. 3, 4, or 5)
     */
    public void setMinWordLength(int min) { minWordLength = min; }

    /** @return total number of words loaded */
    public int getWordCount()         { return wordCount; }
}
