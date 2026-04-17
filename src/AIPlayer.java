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
     * @param name       display name (e.g., "AI (Smart)")
     * @param difficulty AI difficulty level
     * @param dictionary shared dictionary for word lookup
     */
    public AIPlayer(String name, Difficulty difficulty, Dictionary dictionary) {
        super(name);
        this.difficulty  = difficulty;
        this.dictionary  = dictionary;
        this.random      = new Random();
        this.isAI        = true;
    }

    // ---------------------------------------------------------------
    // Public AI interface
    // ---------------------------------------------------------------

    /**
     * Determines the AI's move for this turn.
     * The AI does not solve the whole board. It guesses dictionary words and
     * lets WordValidator check each guess using the same recursion as a human
     * player's submitted word.
     *
     * @param board           the current 5x5 board
     * @param globalUsedWords words already played this round by any player
     * @return the chosen word, or null if the AI must pass
     */
    public String getAIMove(char[][] board, ArrayList<String> globalUsedWords) {
        WordValidator validator = new WordValidator(board);
        ArrayList<String> guesses = buildGuessList(globalUsedWords, true);
        String move = tryGuesses(validator, guesses);

        if (move != null) {
            return move;
        }

        // If the difficulty words did not work, try the rest before passing.
        ArrayList<String> fallbackGuesses = buildGuessList(globalUsedWords, false);
        return tryGuesses(validator, fallbackGuesses);
    }

    // ---------------------------------------------------------------
    // Guess-and-check AI helpers
    // ---------------------------------------------------------------

    /**
     * Builds a list of dictionary words for the AI to try.
     * Uses ArrayList and loops so the logic stays close to the course level.
     *
     * @param globalUsedWords words already played this round
     * @param useDifficulty true to filter by difficulty, false to try all words
     * @return possible dictionary guesses
     */
    private ArrayList<String> buildGuessList(ArrayList<String> globalUsedWords,
                                             boolean useDifficulty) {
        ArrayList<String> guesses = new ArrayList<>();
        String[] allWords = dictionary.getAllWords();

        for (int i = 0; i < allWords.length; i++) {
            String word = allWords[i];
            if (!wasAlreadyUsed(word, globalUsedWords)
                    && (!useDifficulty || matchesDifficulty(word))) {
                guesses.add(word);
            }
        }

        return guesses;
    }

    /**
     * Randomly tries words until one appears on the board.
     * @param validator checks whether a word is on the board
     * @param guesses dictionary words to try
     * @return a valid board word, or null if none were found
     */
    private String tryGuesses(WordValidator validator, ArrayList<String> guesses) {
        while (!guesses.isEmpty()) {
            int index = random.nextInt(guesses.size());
            String guess = guesses.remove(index);

            if (validator.isWordOnBoard(guess)) {
                return guess;
            }
        }

        return null;
    }

    /**
     * Chooses which word lengths each difficulty should try first.
     * @param word dictionary word to test
     * @return true if this difficulty should try the word
     */
    private boolean matchesDifficulty(String word) {
        int min = dictionary.getMinWordLength();

        switch (difficulty) {
            case BEGINNER:
                return word.length() <= min + 1;
            case MEDIUM:
                return word.length() <= min + 3;
            case SMART:
                return word.length() >= min + 3;
            default:
                return true;
        }
    }

    /**
     * Sequentially checks if a word is already in the played-word list.
     * @param word word to check
     * @param usedWords words already played this round
     * @return true if the word was already used
     */
    private boolean wasAlreadyUsed(String word, ArrayList<String> usedWords) {
        for (int i = 0; i < usedWords.size(); i++) {
            if (usedWords.get(i).equalsIgnoreCase(word)) return true;
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** @return current difficulty level */
    public Difficulty getDifficulty()              { return difficulty; }

    /** @param d new difficulty level */
    public void setDifficulty(Difficulty d)        { this.difficulty = d; }
}
