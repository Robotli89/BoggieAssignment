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
 *              Each Phase subclass calls super() to initialize this engine and
 *              then sets up the players[] array according to the phase rules.
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
    // ---------------------------------------------------------------

    protected BoggleBoard   boggleBoard;
    protected Dictionary    dictionary;
    protected WordValidator wordValidator;
    protected Player[]      players;
    protected int           currentPlayerIndex;
    protected int           currentRound;
    protected int           totalRounds;
    protected int           pointTarget;        // 0 = round-count wins only
    protected int           minWordLength;
    protected ArrayList<String> usedWordsThisRound; // all words played in this round
    protected int           consecutivePasses;  // resets when any valid word is played
    protected FileHandler   fileHandler;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Initializes the shared game engine.
     * Subclasses must set players[] after calling super().
     *
     * @param totalRounds    number of rounds to play
     * @param pointTarget    first player to reach this total score wins (0 = off)
     * @param minWordLength  minimum letters required per word
     * @param dictionaryPath path to wordlist.txt
     */
    public GameSession(int totalRounds, int pointTarget,
                       int minWordLength, String dictionaryPath) {
        this.totalRounds    = totalRounds;
        this.pointTarget    = pointTarget;
        this.minWordLength  = minWordLength;

        boggleBoard          = new BoggleBoard();
        dictionary           = new Dictionary(dictionaryPath, minWordLength);
        wordValidator        = new WordValidator(boggleBoard.getBoard());
        usedWordsThisRound   = new ArrayList<>();
        currentPlayerIndex   = 0;
        currentRound         = 1;
        consecutivePasses    = 0;
        fileHandler          = new FileHandler();
    }

    // ---------------------------------------------------------------
    // Core gameplay methods
    // ---------------------------------------------------------------

    /**
     * Submits a word on behalf of the current player.
     * Performs four checks in order:
     *   1. Minimum word length
     *   2. Word not already used this round (by any player)
     *   3. Word can be formed on the board
     *   4. Word exists in the dictionary
     * On success, awards points equal to word length and advances the turn.
     *
     * @param word the word the player wishes to play
     * @return a RESULT_* constant describing the outcome
     */
    public String submitWord(String word) {
        if (word == null) return RESULT_TOO_SHORT;
        word = word.trim().toUpperCase();

        Player current = getCurrentPlayer();

        // Check 1: minimum word length
        if (word.length() < minWordLength) return RESULT_TOO_SHORT;

        // Check 2: not already played this round
        if (wasWordUsedThisRound(word)) return RESULT_ALREADY_USED;

        // Check 3: word exists on the board (DFS)
        if (!wordValidator.isWordOnBoard(word)) return RESULT_NOT_ON_BOARD;

        // Check 4: valid English word (binary search)
        if (!dictionary.isValidWord(word)) return RESULT_NOT_IN_DICT;

        // ---- Word accepted ----
        int points = word.length();  // score = number of letters
        current.addScore(points);
        current.addWord(word);
        usedWordsThisRound.add(word);
        consecutivePasses = 0;

        fileHandler.logWordPlayed(currentRound, current.getName(), word, points);
        advancePlayer();
        return RESULT_VALID;
    }

    /**
     * Records a pass for the current player and advances the turn.
     * The round ends when ALL players pass consecutively.
     */
    public void pass() {
        fileHandler.logPass(currentRound, getCurrentPlayer().getName());
        getCurrentPlayer().pass();
        consecutivePasses++;
        advancePlayer();
    }

    /**
     * Advances the turn to the next player in circular order.
     */
    protected void advancePlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    // ---------------------------------------------------------------
    // Round / game state queries
    // ---------------------------------------------------------------

    /**
     * @return true when all players have passed consecutively
     *         (nobody can find any more words — end this round)
     */
    public boolean isRoundOver() {
        return consecutivePasses >= players.length;
    }

    /**
     * @return true when the game should end:
     *         all rounds have been played, OR a player reached the point target
     */
    public boolean isGameOver() {
        if (currentRound > totalRounds) return true;
        if (pointTarget > 0) {
            for (Player p : players) {
                if (p.getTotalScore() >= pointTarget) return true;
            }
        }
        return false;
    }

    /**
     * Advances to the next round: generates a new board and resets round state.
     */
    public void nextRound() {
        currentRound++;
        boggleBoard.generateBoard();
        wordValidator.setBoard(boggleBoard.getBoard());
        usedWordsThisRound.clear();
        consecutivePasses  = 0;
        currentPlayerIndex = 0;
        for (Player p : players) p.resetForNewRound();
        fileHandler.logNewRound(currentRound);
    }

    /**
     * Regenerates the board without advancing the round.
     * Triggered when both players pass twice (extra feature).
     */
    public void shakeUpBoard() {
        boggleBoard.generateBoard();
        wordValidator.setBoard(boggleBoard.getBoard());
        usedWordsThisRound.clear();
        consecutivePasses = 0;
        fileHandler.logEvent("Board shaken up (Round " + currentRound + ")");
    }

    // ---------------------------------------------------------------
    // Winner determination
    // ---------------------------------------------------------------

    /**
     * @return the player with the highest round score
     */
    public Player getRoundWinner() {
        Player best = players[0];
        for (Player p : players) {
            if (p.getRoundScore() > best.getRoundScore()) best = p;
        }
        return best;
    }

    /**
     * @return the player with the highest total score
     */
    public Player getGameWinner() {
        Player best = players[0];
        for (Player p : players) {
            if (p.getTotalScore() > best.getTotalScore()) best = p;
        }
        return best;
    }

    // ---------------------------------------------------------------
    // AI support
    // ---------------------------------------------------------------

    /**
     * Returns the AI's chosen word for this turn, or null if the current
     * player is human (or if the AI decides to pass).
     * The GUI calls this when it detects an AI turn.
     *
     * @return word to play, or null for a pass
     */
    public String getAIMove() {
        Player current = getCurrentPlayer();
        if (current instanceof AIPlayer) {
            return ((AIPlayer) current).getAIMove(
                    boggleBoard.getBoard(), usedWordsThisRound);
        }
        return null; // human turn — no auto-move
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    /** @return the player whose turn it currently is */
    public Player getCurrentPlayer()            { return players[currentPlayerIndex]; }

    /** @return all players in turn order */
    public Player[] getPlayers()                { return players; }

    /** @return the board manager */
    public BoggleBoard getBoggleBoard()         { return boggleBoard; }

    /** @return the dictionary */
    public Dictionary getDictionary()           { return dictionary; }

    /** @return current round number (1-based) */
    public int getCurrentRound()                { return currentRound; }

    /** @return total number of rounds configured */
    public int getTotalRounds()                 { return totalRounds; }

    /** @return the point target (0 if disabled) */
    public int getPointTarget()                 { return pointTarget; }

    /** @return all words played this round by any player */
    public ArrayList<String> getUsedWordsThisRound()  { return usedWordsThisRound; }

    /** @return current minimum word length */
    public int getMinWordLength()               { return minWordLength; }

    /**
     * Updates the minimum word length setting.
     * @param min new minimum word length
     */
    public void setMinWordLength(int min) {
        this.minWordLength = min;
        dictionary.setMinWordLength(min);
    }

    /** @return the file handler (for save/load from GUI) */
    public FileHandler getFileHandler()         { return fileHandler; }

    /**
     * Sequentially checks whether a word has already been played this round.
     * @param word word to check
     * @return true if the word is already in the list
     */
    private boolean wasWordUsedThisRound(String word) {
        for (int i = 0; i < usedWordsThisRound.size(); i++) {
            if (usedWordsThisRound.get(i).equalsIgnoreCase(word)) return true;
        }
        return false;
    }
}
