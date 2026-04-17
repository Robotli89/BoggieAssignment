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

    protected String     name;
    protected int        totalScore;        // cumulative score across all rounds
    protected int        roundScore;        // score earned in the current round
    protected ArrayList<String> usedWords;  // words the player has played this round
    protected int        consecutivePasses; // passes in a row (resets on valid word)
    protected boolean    isAI;             // true for AI players

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a new human player with the given name.
     * @param name the player's display name
     */
    public Player(String name) {
        this.name              = name;
        this.totalScore        = 0;
        this.roundScore        = 0;
        this.usedWords         = new ArrayList<>();
        this.consecutivePasses = 0;
        this.isAI              = false;
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

    /** @return a short summary string for display */
    @Override
    public String toString() {
        return name + " (Score: " + totalScore + ")";
    }
}
