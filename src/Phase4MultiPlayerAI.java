/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 4 — Multi-player game with one AI player.
 *              Same rules as Phase 3, but one of the players is the AI
 *              from Phase 2.  The AI is inserted at the chosen position
 *              in the turn order.
 */
public class Phase4MultiPlayerAI extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a Phase 4 (multi-player + AI) game session.
     *
     * @param humanNames     display names of the human players (1 or more)
     * @param aiDifficulty   difficulty of the AI player
     * @param aiPosition     0-based index in turn order where AI is inserted
     *                       (clamped to valid range)
     * @param totalRounds    how many rounds to play
     * @param pointTarget    first to reach this score wins (0 = off)
     * @param minWordLength  minimum word length
     * @param dictionaryPath path to wordlist.txt
     */
    public Phase4MultiPlayerAI(String[] humanNames,
                                AIPlayer.Difficulty aiDifficulty,
                                int aiPosition,
                                int totalRounds, int pointTarget,
                                int minWordLength, String dictionaryPath) {
        super(totalRounds, pointTarget, minWordLength, dictionaryPath);

        int totalPlayers = humanNames.length + 1; // humans + 1 AI
        // Clamp AI position to valid range [0, totalPlayers-1]
        aiPosition = Math.max(0, Math.min(aiPosition, totalPlayers - 1));

        // Build players array: insert AI at aiPosition, humans fill the rest
        players       = new Player[totalPlayers];
        AIPlayer ai   = new AIPlayer("AI (" + aiDifficulty + ")", aiDifficulty, dictionary);
        int humanIdx  = 0;

        for (int i = 0; i < totalPlayers; i++) {
            players[i] = (i == aiPosition) ? ai : new Player(humanNames[humanIdx++]);
        }

    }

    // ---------------------------------------------------------------
    // Per-phase end condition (from shouldEndGame logic)
    // ---------------------------------------------------------------

    /**
     * Phase 4 early-end conditions:
     *   - AI concedes → game over immediately
     *   - All humans concede AND AI has the highest score → game over
     *   - All players concede → game over (base case)
     */
    @Override
    public boolean isGameOverEarly() {
        // Find the AI player
        Player ai = null;
        for (Player p : players) {
            if (p.isAI()) { ai = p; break; }
        }

        // AI concedes → game over
        if (ai != null && ai.hasConceded()) return true;

        // Sequential check: have all human players conceded?
        boolean allHumansConceded = true;
        for (Player p : players) {
            if (p.isAI() == false && p.hasConceded() == false) {
                allHumansConceded = false;
                break;
            }
        }

        // All humans conceded + AI is leading → game over
        if (allHumansConceded && ai != null) {
            boolean aiHighest = true;
            for (Player p : players) {
                if (p.isAI() == false && p.getTotalScore() > ai.getTotalScore()) {
                    aiHighest = false;
                    break;
                }
            }
            if (aiHighest) return true;
        }

        return super.isGameOverEarly();
    }
}
