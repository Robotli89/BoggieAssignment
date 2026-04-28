package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date: [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 4 — Multi-player game with one AI player.
 * Same rules as Phase 3, but one of the players is the AI
 * from Phase 2. The AI is inserted at the chosen position
 * in the turn order.
 */
public class Phase4MultiPlayerAI extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates a multiplayer game with one AI. */
    public Phase4MultiPlayerAI(String[] humanNames, int aiDifficulty, int aiPosition, int totalRounds, int pointTarget,
            int minWordLength, String dictionaryPath) {
        setUpGameSession(totalRounds, pointTarget, minWordLength, dictionaryPath);

        int totalPlayers = humanNames.length + 1; // humans plus AI
        if (aiPosition < 0) {
            aiPosition = 0;
        }
        if (aiPosition > totalPlayers - 1) {
            aiPosition = totalPlayers - 1;
        }

        // Insert AI at the selected turn index.
        players = new Player[totalPlayers];
        AIPlayer ai = new AIPlayer("AI (" + AIPlayer.getDifficultyName(aiDifficulty) + ")", aiDifficulty, dictionary);
        int humanIdx = 0;

        for (int i = 0; i < totalPlayers; i++) {
            if (i == aiPosition) {
                players[i] = ai;
            } else {
                players[i] = new Player(humanNames[humanIdx]);
                humanIdx++;
            }
        }

    }

    // ---------------------------------------------------------------
    // Per-phase end condition (from shouldEndGame logic)
    // ---------------------------------------------------------------

    /** Applies Phase 4 early game-end rules. */
    public boolean shouldGameEndNow() {
        // Locate the AI player.
        Player ai = null;
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAI()) {
                ai = players[i];
                break;
            }
        }

        // End when the AI has no move.
        if (ai != null && ai.didPlayerQuitGame()) {
            return true;
        }

        // Check whether all humans quit.
        boolean allHumansQuit = true;
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAI() == false && players[i].didPlayerQuitGame() == false) {
                allHumansQuit = false;
                break;
            }
        }

        // End if only a leading AI remains.
        if (allHumansQuit && ai != null) {
            boolean aiHighest = true;
            for (int i = 0; i < players.length; i++) {
                if (players[i].isAI() == false && players[i].getTotalScore() > ai.getTotalScore()) {
                    aiHighest = false;
                    break;
                }
            }
            if (aiHighest) {
                return true;
            }
        }

        return noActivePlayersLeft();
    }
}
