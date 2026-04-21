package BoggleAssignment;

public class WinChecker {
    public static boolean shouldEndGame(String phaseName, String[] players, int[] totalScores, boolean[] conceded,
                                        int concededCount, int playerCount) {
        if (phaseName.equals("Phase 1: Player vs Player") || phaseName.equals("Phase 3: Multiplayer")) {
            return false;
        }

        if (phaseName.equals("Phase 2: Player vs AI")) {
            int aiIndex = -1;
            int humanIndex = -1;
            for (int i = 0; i < playerCount; i++) {
                if (isAIName(players[i])) {
                    aiIndex = i;
                } else {
                    humanIndex = i;
                }
            }
            if (aiIndex != -1 && conceded[aiIndex]) {
                return true;
            }
            if (humanIndex != -1 && conceded[humanIndex] && aiIndex != -1 && totalScores[humanIndex] < totalScores[aiIndex]) {
                return true;
            }
            return concededCount == playerCount;
        }

        if (phaseName.equals("Phase 4: Multiplayer + AI")) {
            int aiIndex = -1;
            for (int i = 0; i < playerCount; i++) {
                if (isAIName(players[i])) {
                    aiIndex = i;
                    break;
                }
            }
            if (aiIndex != -1 && conceded[aiIndex]) {
                return true;
            }

            boolean allHumansConceded = true;
            for (int i = 0; i < playerCount; i++) {
                if (i != aiIndex && !conceded[i]) {
                    allHumansConceded = false;
                    break;
                }
            }
            if (aiIndex != -1 && allHumansConceded) {
                boolean aiHighest = true;
                for (int i = 0; i < playerCount; i++) {
                    if (i != aiIndex && totalScores[i] > totalScores[aiIndex]) {
                        aiHighest = false;
                        break;
                    }
                }
                if (aiHighest) {
                    return true;
                }
            }
            return concededCount == playerCount;
        }

        return concededCount == playerCount;
    }

    public static boolean reachedTarget(int[] scores, int target) {
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] >= target) {
                return true;
            }
        }
        return false;
    }

    public static int getOnlyActivePlayer(boolean[] conceded) {
        int idx = -1;
        for (int i = 0; i < conceded.length; i++) {
            if (!conceded[i]) {
                if (idx != -1) {
                    return -1;
                }
                idx = i;
            }
        }
        return idx;
    }

    public static int getMaxOtherScore(int[] scores, int myIndex) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < scores.length; i++) {
            if (i != myIndex && scores[i] > max) {
                max = scores[i];
            }
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    public static boolean shouldGiveSecondAttempt(String phaseName, boolean[] conceded, int playerCount, int currentIndex) {
        if (!(phaseName.equals("Phase 1: Player vs Player") || phaseName.equals("Phase 3: Multiplayer"))) {
            return false;
        }
        int active = 0;
        int activeIndex = -1;
        for (int i = 0; i < playerCount; i++) {
            if (!conceded[i]) {
                active++;
                activeIndex = i;
            }
        }
        return active == 1 && activeIndex == currentIndex;
    }

    public static boolean isAIName(String name) {
        return name.equalsIgnoreCase("AI")
                || name.equalsIgnoreCase("MYAI")
                || name.equalsIgnoreCase("OTHERSTUDENTAI");
    }
}
