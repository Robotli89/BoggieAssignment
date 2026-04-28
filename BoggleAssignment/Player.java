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
    private int        totalScore;        // score across rounds
    private int        roundScore;        // score this round
    private ArrayList<String> usedWords;  // words used this round
    private int        consecutivePasses; // pass streak
    public  boolean    isAI;             // true for AI players
    private boolean    playerQuitGame;   // quit state

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates a human player. */
    public Player(String playerName) {
        setUpPlayer(playerName);
    }

    public Player() {
        setUpPlayer("");
    }

    public void setUpPlayer(String playerName) {
        name              = playerName;
        totalScore        = 0;
        roundScore        = 0;
        usedWords         = new ArrayList<>();
        consecutivePasses = 0;
        isAI              = false;
        playerQuitGame    = false;
    }

    // ---------------------------------------------------------------
    // Game actions
    // ---------------------------------------------------------------

    /** Adds points to round and total scores. */
    public void addScore(int points) {
        roundScore += points;
        totalScore += points;
    }

    /** Stores a valid word and resets the pass counter. */
    public void addWord(String word) {
        usedWords.add(word.toUpperCase());
        consecutivePasses = 0;
    }

    /** Increments the pass counter. */
    public void pass() {
        consecutivePasses++;
    }

    /** Marks the player as quit. */
    public void quitGame() {
        playerQuitGame = true;
    }

    /** Checks if this player used the word this round. */
    public boolean hasUsedWord(String word) {
        for (int i = 0; i < usedWords.size(); i++) {
            if (usedWords.get(i).toUpperCase().equals(word.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /** Clears round-only state. */
    public void resetForNewRound() {
        roundScore        = 0;
        consecutivePasses = 0;
        usedWords.clear();
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    /** Player display name. */
    public String getName()              { return name; }

    /** Total score. */
    public int    getTotalScore()        { return totalScore; }

    /** Round score. */
    public int    getRoundScore()        { return roundScore; }

    /** Words used this round. */
    public ArrayList<String> getUsedWords()   { return usedWords; }

    /** Consecutive pass count. */
    public int    getConsecutivePasses() { return consecutivePasses; }

    /** AI player flag. */
    public boolean isAI()               { return isAI; }

    /** Quit state. */
    public boolean didPlayerQuitGame()  { return playerQuitGame; }

    /** Score summary string. */
    public String toString() {
        return name + " (Score: " + totalScore + ")";
    }
}
