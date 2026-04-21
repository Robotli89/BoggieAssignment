package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 1 — Player vs Player.
 *              Two human players take turns entering words.
 *              No word may be repeated by either player in the same round.
 *              The player with the highest score at the end of each round wins
 *              that round.  The game continues for the agreed number of rounds.
 */
public class Phase1PlayerVsPlayer extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a Phase 1 (2-player human vs human) game session.
     *
     * @param player1Name   display name of player 1
     * @param player2Name   display name of player 2
     * @param totalRounds   how many rounds to play
     * @param pointTarget   first player to reach this total score wins (0 = off)
     * @param minWordLength minimum letters per word (e.g., 3)
     * @param dictionaryPath path to wordlist.txt
     */
    public Phase1PlayerVsPlayer(String player1Name, String player2Name,
                                 int totalRounds, int pointTarget,
                                 int minWordLength, String dictionaryPath) {
        super(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Set up two human players
        players    = new Player[2];
        players[0] = new Player(player1Name);
        players[1] = new Player(player2Name);
    }
}
