package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: An AI-controlled Boggle player.
 *              Uses a simple guess-and-check strategy:
 *                - pick possible words from the dictionary
 *                - check whether the word is already used
 *                - use WordValidator's recursion to see if it is on the board
 *              Difficulty changes which dictionary words the AI tries first.
 *                - BEGINNER: tries shorter words first
 *                - MEDIUM:   tries random words
 *                - SMART:    tries longer words first
 */
import java.util.*;

public class AIPlayer extends Player {

    // ---------------------------------------------------------------
    // Difficulty enum
    // ---------------------------------------------------------------

    /** Three AI difficulty levels that determine word selection strategy. */
    public enum Difficulty {
        BEGINNER,   // tries shorter words first
        MEDIUM,     // tries medium/random words
        SMART       // tries longer words first
    }

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private Difficulty difficulty;
    private Dictionary dictionary;
    private Random     random;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates an AI player with the specified difficulty.
     * @param playerName display name (e.g., "AI (Smart)")
     * @param diff       AI difficulty level
     * @param dict       shared dictionary for word lookup
     */
    public AIPlayer(String playerName, Difficulty diff, Dictionary dict) {
        super(playerName);
        difficulty = diff;
        dictionary = dict;
        random     = new Random();
        isAI       = true;
    }

    // ---------------------------------------------------------------
    // Public AI interface
    // ---------------------------------------------------------------

    /**
     * Determines the AI's move for this turn using a guess-and-check strategy.
     *
     * Iterates the entire dictionary and checks each word against the board
     * using the same recursive DFS as word validation.
     *   - BEGINNER: returns the shortest valid word found (fewest points)
     *   - MEDIUM:   collects all valid words, picks one at random
     *   - SMART:    returns the longest valid word found (most points)
     *
     * Returns null when no valid unplayed word exists on the board — the caller
     * should treat this as a concede.
     *
     * @param board           the current 5x5 board
     * @param globalUsedWords words already played this round by any player
     * @return the chosen word, or null if the AI has no move
     */
    public String getAIMove(char[][] board, ArrayList<String> globalUsedWords) {
        WordValidator validator = new WordValidator(board);
        String[] allWords = dictionary.getAllWords();
        String chosen = null;
        ArrayList<String> valid = new ArrayList<>();

        for (int i = 0; i < allWords.length; i++) {
            String word = allWords[i];

            // Sequential search: skip words already played this round
            boolean alreadyUsed = false;
            for (int j = 0; j < globalUsedWords.size(); j++) {
                if (globalUsedWords.get(j).equalsIgnoreCase(word)) {
                    alreadyUsed = true;
                    break;
                }
            }
            if (alreadyUsed) continue;

            // Check if the word can be traced on the board (recursive DFS)
            if (validator.isWordOnBoard(word) == false) continue;

            if (difficulty == Difficulty.BEGINNER) {
                // Keep shortest word found
                if (chosen == null || word.length() < chosen.length()) {
                    chosen = word;
                }
            } else if (difficulty == Difficulty.SMART) {
                // Keep longest word found
                if (chosen == null || word.length() > chosen.length()) {
                    chosen = word;
                }
            } else {
                // MEDIUM: collect all valid words, pick randomly below
                valid.add(word);
            }
        }

        if (difficulty == Difficulty.MEDIUM) {
            if (valid.isEmpty()) return null;
            return valid.get(random.nextInt(valid.size()));
        }
        return chosen;
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** @return current difficulty level */
    public Difficulty getDifficulty()     { return difficulty; }

    /** @param d new difficulty level */
    public void setDifficulty(Difficulty d) { difficulty = d; }
}
