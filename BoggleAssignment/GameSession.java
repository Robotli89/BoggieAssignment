package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Core game engine shared by all five phases.
 *              Manages: board state, dictionary, word validation, player turns,
 *              scoring, round/game end conditions, and file logging.
 *
 *              Each Phase subclass sets up this shared engine, then creates
 *              the players[] array according to the phase rules.
 */
import java.util.ArrayList;

public class GameSession {

    // ---------------------------------------------------------------
    // Result codes returned by submitWord()
    // ---------------------------------------------------------------
    public static final String RESULT_VALID        = "VALID";
    public static final String RESULT_ALREADY_USED = "ALREADY_USED";
    public static final String RESULT_NOT_ON_BOARD = "NOT_ON_BOARD";
    public static final String RESULT_NOT_IN_DICT  = "NOT_IN_DICT";
    public static final String RESULT_TOO_SHORT    = "TOO_SHORT";

    // ---------------------------------------------------------------
    // Shared game state
    // Phase classes set public fields during setup.
    // ---------------------------------------------------------------

    public  BoggleBoard   boggleBoard;
    public  Dictionary    dictionary;
    public  WordValidator wordChecker;
    public  Player[]      players;

    private int           currentPlayerIndex;
    private int           currentRound;
    private int           totalRounds;
    private int           pointTarget;        // 0 disables target score
    private int           minWordLength;
    private ArrayList<String> wordsUsedThisRound; // round word cache
    private int           consecutivePasses;  // active-player pass counter

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public GameSession() {
    }

    public void setUpGameSession(int rounds, int target, int minLen, String dictPath) {
        totalRounds   = rounds;
        pointTarget   = target;
        minWordLength = minLen;

        boggleBoard        = new BoggleBoard();
        dictionary         = new Dictionary(dictPath, minLen);
        wordChecker        = new WordValidator(boggleBoard.getBoard());
        wordsUsedThisRound = new ArrayList<>();
        currentPlayerIndex = 0;
        currentRound       = 1;
        consecutivePasses  = 0;
    }

    // ---------------------------------------------------------------
    // Core gameplay methods
    // ---------------------------------------------------------------

    /** Validates the current player's word, scores it, then advances the turn. */
    public String submitWord(String word) {
        if (word == null) {
            return RESULT_TOO_SHORT;
        }
        word = word.trim().toUpperCase();

        Player current = getCurrentPlayer();

        // Enforce minimum length.
        if (word.length() < minWordLength) {
            return RESULT_TOO_SHORT;
        }

        // Block repeated round words.
        if (wasWordUsedThisRound(word)) {
            return RESULT_ALREADY_USED;
        }

        // Check board path with DFS.
        if (wordChecker.isWordOnBoard(word) == false) {
            return RESULT_NOT_ON_BOARD;
        }

        // Check dictionary with binary search.
        if (dictionary.isValidWord(word) == false) {
            return RESULT_NOT_IN_DICT;
        }

        // Score valid words by length.
        int points = word.length();
        current.addScore(points);
        current.addWord(word);
        wordsUsedThisRound.add(word);
        consecutivePasses = 0;

        advancePlayer();
        return RESULT_VALID;
    }

    /** Records a pass and moves to the next active player. */
    public void pass() {
        getCurrentPlayer().pass();
        consecutivePasses++;
        advancePlayer();
    }

    /** Marks the current player as quit and advances the turn. */
    public void playerQuitsGame() {
        getCurrentPlayer().quitGame();
        advancePlayer();
    }

    /** Skips quit players while moving to the next turn. */
    private void advancePlayer() {
        int steps = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            steps++;
        } while (players[currentPlayerIndex].didPlayerQuitGame() && steps < players.length);
    }

    // ---------------------------------------------------------------
    // Round / game state queries
    // ---------------------------------------------------------------

    /** Returns true when all active players pass in sequence. */
    public boolean isRoundOver() {
        int active = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].didPlayerQuitGame() == false) {
                active++;
            }
        }
        if (active == 0) {
            return true;
        } else if (consecutivePasses >= active) {
            return true;
        } else {
            return false;
        }
    }

    /** Checks phase-specific early game-end rules. */
    public boolean shouldGameEndNow() {
        return noActivePlayersLeft();
    }

    public boolean noActivePlayersLeft() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].didPlayerQuitGame() == false) {
                return false;
            }
        }
        return true;
    }

    /** Returns true when rounds are done or a player reaches the point target. */
    public boolean isGameOver() {
        if (currentRound > totalRounds) {
            return true;
        }
        if (pointTarget > 0) {
            for (int i = 0; i < players.length; i++) {
                if (players[i].getTotalScore() >= pointTarget) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Starts the next round with a fresh board and cleared round data. */
    public void nextRound() {
        currentRound++;
        boggleBoard.generateBoard();
        wordChecker.setBoard(boggleBoard.getBoard());
        wordsUsedThisRound.clear();
        consecutivePasses  = 0;
        currentPlayerIndex = 0;
        for (int i = 0; i < players.length; i++) {
            players[i].resetForNewRound();
        }
    }

    /** Regenerates the board without changing the round number. */
    public void shakeUpBoard() {
        boggleBoard.generateBoard();
        wordChecker.setBoard(boggleBoard.getBoard());
        wordsUsedThisRound.clear();
        consecutivePasses = 0;
    }

    // ---------------------------------------------------------------
    // Winner determination
    // ---------------------------------------------------------------

    /** Returns the player with the top round score. */
    public Player getRoundWinner() {
        Player best = players[0];
        for (int i = 0; i < players.length; i++) {
            if (players[i].getRoundScore() > best.getRoundScore()) {
                best = players[i];
            }
        }
        return best;
    }

    /** Returns the player with the top total score. */
    public Player getGameWinner() {
        Player best = players[0];
        for (int i = 0; i < players.length; i++) {
            if (players[i].getTotalScore() > best.getTotalScore()) {
                best = players[i];
            }
        }
        return best;
    }

    // ---------------------------------------------------------------
    // AI support
    // ---------------------------------------------------------------

    /** Returns the current AI move, or null for a human/no move. */
    public String getAIMove() {
        Player current = getCurrentPlayer();
        if (current.isAI()) {
            AIPlayer ai = (AIPlayer) current;
            return ai.getAIMove(boggleBoard.getBoard(), wordsUsedThisRound);
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** Current turn player. */
    public Player getCurrentPlayer()            { return players[currentPlayerIndex]; }

    /** Players in turn order. */
    public Player[] getPlayers()                { return players; }

    /** Board manager. */
    public BoggleBoard getBoggleBoard()         { return boggleBoard; }

    /** Current round number. */
    public int getCurrentRound()                { return currentRound; }

    /** Configured round count. */
    public int getTotalRounds()                 { return totalRounds; }

    /** Minimum word length. */
    public int getMinWordLength()               { return minWordLength; }

    /** Checks the round word cache for a duplicate. */
    private boolean wasWordUsedThisRound(String word) {
        for (int i = 0; i < wordsUsedThisRound.size(); i++) {
            if (wordsUsedThisRound.get(i).toUpperCase().equals(word.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
