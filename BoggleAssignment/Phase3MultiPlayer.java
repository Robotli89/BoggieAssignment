package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 3 — Multi-player game.
 *              Supports 2–6 human players taking turns in order.
 *              All standard checks (a) on-board and (b) dictionary apply.
 *              No word may be repeated by any player within a round.
 *              The turn order is determined before the game begins.
 */
public class Phase3MultiPlayer extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a Phase 3 (multi-player human-only) game session.
     *
     * @param playerNames    display names of all human players (2–6)
     * @param totalRounds    how many rounds to play
     * @param pointTarget    first to reach this score wins (0 = off)
     * @param minWordLength  minimum word length
     * @param dictionaryPath path to wordlist.txt
     */
    public Phase3MultiPlayer(String[] playerNames,
                              int totalRounds, int pointTarget,
                              int minWordLength, String dictionaryPath) {
        super(totalRounds, pointTarget, minWordLength, dictionaryPath);

        if (playerNames.length < 2) {
            throw new IllegalArgumentException("Phase 3 requires at least 2 players.");
        }

        // Create a human Player for each name
        players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i]);
        }
    }
}
