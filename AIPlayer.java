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
import java.util.ArrayList;
import java.util.Random;

public class AIPlayer extends Player {

    // ---------------------------------------------------------------
    // Difficulty constants
    // ---------------------------------------------------------------

    public static final int BEGINNER = 1;
    public static final int MEDIUM   = 2;
    public static final int SMART    = 3;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private int difficulty;
    private Dictionary dictionary;
    private Random     random;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates an AI player with a difficulty setting. */
    public AIPlayer(String playerName, int diff, Dictionary dict) {
        setUpPlayer(playerName);
        difficulty = diff;
        dictionary = dict;
        random     = new Random();
        isAI       = true;
    }

    // ---------------------------------------------------------------
    // Public AI interface
    // ---------------------------------------------------------------

    /** Finds an unplayed board word based on the AI difficulty. */
    public String getAIMove(char[][] board, ArrayList<String> globalUsedWords) {
        WordValidator validator = new WordValidator(board);
        String[] allWords = dictionary.getAllWords();
        String chosen = null;
        ArrayList<String> valid = new ArrayList<>();

        for (int i = 0; i < allWords.length; i++) {
            String word = allWords[i];

            // Skip words already used this round.
            boolean alreadyUsed = false;
            for (int j = 0; j < globalUsedWords.size(); j++) {
                if (globalUsedWords.get(j).toUpperCase().equals(word.toUpperCase())) {
                    alreadyUsed = true;
                    break;
                }
            }

            // Require a valid board path.
            if (alreadyUsed == false && validator.isWordOnBoard(word)) {
                if (difficulty == BEGINNER) {
                    // Beginner chooses the shortest valid word.
                    if (chosen == null || word.length() < chosen.length()) {
                        chosen = word;
                    }
                } else if (difficulty == SMART) {
                    // Smart chooses the longest valid word.
                    if (chosen == null || word.length() > chosen.length()) {
                        chosen = word;
                    }
                } else {
                    // Medium stores candidates for random selection.
                    valid.add(word);
                }
            }
        }

        if (difficulty == MEDIUM) {
            if (valid.size() == 0) {
                return null;
            }
            return valid.get(random.nextInt(valid.size()));
        }
        return chosen;
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** Current difficulty level. */
    public int getDifficulty() {
        return difficulty;
    }

    public static String getDifficultyName(int difficulty) {
        if (difficulty == BEGINNER) {
            return "Beginner";
        } else if (difficulty == MEDIUM) {
            return "Medium";
        } else {
            return "Smart";
        }
    }
}
