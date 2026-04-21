package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Represents a single Boggle player.
 *              Tracks the player's name, total score, round score,
 *              words used this round, and consecutive pass count.
 *              AIPlayer extends this class.
 */
import java.util.ArrayList;

public class Player {

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private String     name;
    private int        totalScore;        // cumulative score across all rounds
    private int        roundScore;        // score earned in the current round
    private ArrayList<String> usedWords;  // words the player has played this round
    private int        consecutivePasses; // passes in a row (resets on valid word)
    public  boolean    isAI;             // true for AI players (public so AIPlayer can set it)
    private boolean    hasConceded;      // true once player permanently concedes

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a new human player with the given name.
     * @param playerName the player's display name
     */
    public Player(String playerName) {
        name              = playerName;
        totalScore        = 0;
        roundScore        = 0;
        usedWords         = new ArrayList<>();
        consecutivePasses = 0;
        isAI              = false;
        hasConceded       = false;
    }

    // ---------------------------------------------------------------
    // Game actions
    // ---------------------------------------------------------------

    /**
     * Awards points to the player for a valid word.
     * Score = number of letters in the word.
     * @param points points to add (equal to word length)
     */
    public void addScore(int points) {
        roundScore += points;
        totalScore += points;
    }

    /**
     * Records a word as used by this player and resets the pass counter.
     * @param word the valid word that was played (will be stored uppercase)
     */
    public void addWord(String word) {
        usedWords.add(word.toUpperCase());
        consecutivePasses = 0;
    }

    /**
     * Increments the consecutive pass counter.
     * Called when this player chooses to pass their turn.
     */
    public void pass() {
        consecutivePasses++;
    }

    /**
     * Permanently marks this player as having conceded.
     * A conceded player is skipped for all future turns.
     */
    public void concede() {
        hasConceded = true;
    }

    /**
     * Checks whether this player has already played the given word this round.
     * @param word word to check (case-insensitive)
     * @return true if the word has already been used by this player
     */
    public boolean hasUsedWord(String word) {
        for (int i = 0; i < usedWords.size(); i++) {
            if (usedWords.get(i).equalsIgnoreCase(word)) return true;
        }
        return false;
    }

    /**
     * Resets per-round state: round score, used words, consecutive passes.
     * Called by GameSession at the start of each new round.
     */
    public void resetForNewRound() {
        roundScore        = 0;
        consecutivePasses = 0;
        usedWords.clear();
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    /** @return the player's display name */
    public String getName()              { return name; }

    /** @return total score across all rounds */
    public int    getTotalScore()        { return totalScore; }

    /** @return score earned in the current round only */
    public int    getRoundScore()        { return roundScore; }

    /** @return words played by this player this round */
    public ArrayList<String> getUsedWords()   { return usedWords; }

    /** @return number of consecutive passes without a valid word */
    public int    getConsecutivePasses() { return consecutivePasses; }

    /** @return true if this player is controlled by the AI */
    public boolean isAI()               { return isAI; }

    /** @return true if this player has permanently conceded */
    public boolean hasConceded()        { return hasConceded; }

    /** @return a short summary string for display */
    @Override
    public String toString() {
        return name + " (Score: " + totalScore + ")";
    }
}
