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

public class Dictionary {

    private String[] words;   // sorted uppercase words
    private int      wordCount;
    private int      minWordLength;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Loads valid words from the dictionary file. */
    public Dictionary(String filename, int minLen) {
        minWordLength = minLen;
        loadDictionary(filename);
    }

    // ---------------------------------------------------------------
    // Loading
    // ---------------------------------------------------------------

    /** Reads, filters, and sorts dictionary words. */
    private void loadDictionary(String filename) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toUpperCase();
                // Keep alphabetic words that meet the length limit.
                if (line.length() >= minWordLength && isLettersOnly(line)) {
                    list.add(line);
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Warning: Could not load dictionary \"" + filename
                    + "\": " + e.getMessage());
            System.err.println("Word validation will fail for all entries.");
        }

        words = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            words[i] = list.get(i);
        }
        insertionSort(words);
        wordCount = words.length;
        System.out.println("Dictionary loaded: " + wordCount + " words (min length = "
                + minWordLength + ").");
    }

    private boolean isLettersOnly(String word) {
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if (letter < 'A' || letter > 'Z') {
                return false;
            }
        }
        return true;
    }

    private void insertionSort(String[] array) {
        for (int i = 1; i < array.length; i++) {
            String current = array[i];
            int j = i - 1;

            while (j >= 0 && array[j].compareTo(current) > 0) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = current;
        }
    }

    // ---------------------------------------------------------------
    // Word lookup — recursive binary search
    // ---------------------------------------------------------------

    /** Checks if a word exists in the sorted dictionary. */
    public boolean isValidWord(String word) {
        if (word == null || word.length() < minWordLength) {
            return false;
        }
        return binarySearch(word.toUpperCase(), 0, wordCount - 1);
    }

    /** Recursive binary search over the sorted word array. */
    private boolean binarySearch(String target, int low, int high) {
        if (low > high) {
            return false;
        }
        int mid = (low + high) / 2;
        int cmp = words[mid].compareTo(target);
        if (cmp == 0) {
            return true;
        } else if (cmp > 0) {
            return binarySearch(target, low, mid - 1);
        } else {
            return binarySearch(target, mid + 1, high);
        }
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** Sorted word array. */
    public String[] getAllWords() {
        return words;
    }

    /** Current minimum word length. */
    public int getMinWordLength() {
        return minWordLength;
    }

    /** Updates the minimum word length. */
    public void setMinWordLength(int min) {
        minWordLength = min;
    }

    /** Loaded word count. */
    public int getWordCount() {
        return wordCount;
    }
}
