package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 2 — Player vs AI.
 *              One human player competes against an AI opponent.
 *              The program asks who goes first (player or AI) and allows
 *              the AI difficulty to be set (Beginner, Medium, Smart).
 *              Both checks (a) on-board and (b) dictionary are applied.
 */
public class Phase2PlayerVsAI extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates a human-vs-AI game. */
    public Phase2PlayerVsAI(String playerName,
                             int aiDifficulty,
                             boolean playerGoesFirst,
                             int totalRounds, int pointTarget,
                             int minWordLength, String dictionaryPath) {
        setUpGameSession(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Build one human and one AI player.
        Player   human = new Player(playerName);
        AIPlayer ai    = new AIPlayer("AI (" + AIPlayer.getDifficultyName(aiDifficulty) + ")", aiDifficulty, dictionary);

        players    = new Player[2];
        if (playerGoesFirst) {
            players[0] = human;
            players[1] = ai;
        } else {
            players[0] = ai;
            players[1] = human;
        }
    }

    // ---------------------------------------------------------------
    // Per-phase end condition (from shouldEndGame logic)
    // ---------------------------------------------------------------

    /** Applies Phase 2 early game-end rules. */
    public boolean shouldGameEndNow() {
        Player ai    = null;
        Player human = null;
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAI()) {
                ai = players[i];
            } else {
                human = players[i];
            }
        }
        // End when the AI has no move.
        if (ai != null && ai.didPlayerQuitGame()) {
            return true;
        }
        // End when the human quits while behind.
        if (human != null && human.didPlayerQuitGame()
                && ai != null && human.getTotalScore() < ai.getTotalScore()) {
            return true;
        }
        return noActivePlayersLeft();
    }
}
