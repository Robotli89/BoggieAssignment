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
    // public: Phase subclasses need to read/write these directly
    // private: only used inside GameSession
    // ---------------------------------------------------------------

    public  BoggleBoard   boggleBoard;
    public  Dictionary    dictionary;
    public  WordValidator wordValidator;
    public  Player[]      players;

    private int           currentPlayerIndex;
    private int           currentRound;
    private int           totalRounds;
    private int           pointTarget;        // 0 = round-count wins only
    private int           minWordLength;
    private ArrayList<String> usedWordsThisRound; // all words played in this round
    private int           consecutivePasses;  // resets when any valid word is played

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Initializes the shared game engine.
     * Subclasses must set players[] after calling super().
     *
     * @param rounds    number of rounds to play
     * @param target    first player to reach this total score wins (0 = off)
     * @param minLen    minimum letters required per word
     * @param dictPath  path to wordlist.txt
     */
    public GameSession(int rounds, int target, int minLen, String dictPath) {
        totalRounds   = rounds;
        pointTarget   = target;
        minWordLength = minLen;

        boggleBoard        = new BoggleBoard();
        dictionary         = new Dictionary(dictPath, minLen);
        wordValidator      = new WordValidator(boggleBoard.getBoard());
        usedWordsThisRound = new ArrayList<>();
        currentPlayerIndex = 0;
        currentRound       = 1;
        consecutivePasses  = 0;
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
        if (wordValidator.isWordOnBoard(word) == false) return RESULT_NOT_ON_BOARD;

        // Check 4: valid English word (binary search)
        if (dictionary.isValidWord(word) == false) return RESULT_NOT_IN_DICT;

        // ---- Word accepted ----
        int points = word.length();  // score = number of letters
        current.addScore(points);
        current.addWord(word);
        usedWordsThisRound.add(word);
        consecutivePasses = 0;

        advancePlayer();
        return RESULT_VALID;
    }

    /**
     * Records a pass for the current player and advances the turn.
     * The round ends when ALL active (non-conceded) players pass consecutively.
     */
    public void pass() {
        getCurrentPlayer().pass();
        consecutivePasses++;
        advancePlayer();
    }

    /**
     * Permanently removes the current player from the game (concede).
     * Unlike a pass, concede does not count toward the consecutive-pass total —
     * the remaining active players still each need to pass to end the round.
     * After conceding, check isGameOverEarly() to see if the game should end.
     */
    public void concede() {
        getCurrentPlayer().concede();
        advancePlayer();
    }

    /**
     * Advances the turn to the next non-conceded player in circular order.
     */
    private void advancePlayer() {
        int steps = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            steps++;
        } while (players[currentPlayerIndex].hasConceded() && steps < players.length);
    }

    // ---------------------------------------------------------------
    // Round / game state queries
    // ---------------------------------------------------------------

    /**
     * @return true when all active (non-conceded) players have passed
     *         consecutively, or when every player has conceded
     */
    public boolean isRoundOver() {
        int active = 0;
        for (Player p : players) {
            if (p.hasConceded() == false) active++;
        }
        return active == 0 || consecutivePasses >= active;
    }

    /**
     * Checks per-phase early end conditions (e.g., AI concedes in Phase 2/4).
     * Default: game ends early only when every player has conceded.
     * Phase subclasses override this for additional end conditions.
     *
     * @return true if the game should end immediately
     */
    public boolean isGameOverEarly() {
        // Sequential check: game ends when no active players remain
        for (Player p : players) {
            if (p.hasConceded() == false) return false;
        }
        return true;
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
        minWordLength = min;
        dictionary.setMinWordLength(min);
    }

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
