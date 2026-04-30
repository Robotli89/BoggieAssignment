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

    /** Creates a two-player human game. */
    public Phase1PlayerVsPlayer(String player1Name, String player2Name,
                                 int totalRounds, int pointTarget,
                                 int minWordLength, String dictionaryPath) {
        setUpGameSession(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Add two human players.
        players    = new Player[2];
        players[0] = new Player(player1Name);
        players[1] = new Player(player2Name);
    }
}
