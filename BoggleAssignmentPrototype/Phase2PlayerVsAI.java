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

    /**
     * Creates a Phase 2 (human vs AI) game session.
     *
     * @param playerName      human player's display name
     * @param aiDifficulty    AI difficulty level
     * @param playerGoesFirst if true the human takes the first turn; AI goes first otherwise
     * @param totalRounds     how many rounds to play
     * @param pointTarget     first to reach this score wins (0 = off)
     * @param minWordLength   minimum word length
     * @param dictionaryPath  path to wordlist.txt
     */
    public Phase2PlayerVsAI(String playerName,
                             AIPlayer.Difficulty aiDifficulty,
                             boolean playerGoesFirst,
                             int totalRounds, int pointTarget,
                             int minWordLength, String dictionaryPath) {
        super(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Create one human and one AI player
        Player   human = new Player(playerName);
        AIPlayer ai    = new AIPlayer("AI (" + aiDifficulty + ")", aiDifficulty, dictionary);

        players    = new Player[2];
        players[0] = playerGoesFirst ? human : ai;
        players[1] = playerGoesFirst ? ai    : human;
    }

    // ---------------------------------------------------------------
    // Per-phase end condition (from shouldEndGame logic)
    // ---------------------------------------------------------------

    /**
     * Phase 2 early-end conditions:
     *   - AI concedes → game over immediately (AI exhausted all words)
     *   - Human concedes AND human score is behind AI → game over
     *   - Both concede → game over (base case)
     */
    @Override
    public boolean isGameOverEarly() {
        Player ai    = null;
        Player human = null;
        for (Player p : players) {
            if (p.isAI()) ai    = p;
            else           human = p;
        }
        // AI concedes → game over
        if (ai != null && ai.hasConceded()) return true;
        // Human concedes while losing → game over
        if (human != null && human.hasConceded()
                && ai != null && human.getTotalScore() < ai.getTotalScore()) return true;
        return super.isGameOverEarly();
    }
}
