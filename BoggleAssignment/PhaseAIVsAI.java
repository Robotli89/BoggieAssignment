package BoggleAssignment;

public class PhaseAIVsAI {
    public static void run(String[] dictionary) {
        System.out.println("AI vs AI contest mode.");
        System.out.print("Enter teacher board file path (.txt): ");
        String path = Main.SCANNER.nextLine().trim();
        while (path.length() == 0) {
            path = Main.SCANNER.nextLine().trim();
        }

        char[][] board = Main.loadBoardFromFile(path);
        if (board == null) {
            System.out.println("Invalid board file. This mode requires teacher provided board file.");
            return;
        }

        System.out.println("My AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int myLevel = Main.readInt("Choose 1-3: ");
        while (myLevel < 1 || myLevel > 3) {
            myLevel = Main.readInt("Choose 1-3: ");
        }

        System.out.println("Other student AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int otherLevel = Main.readInt("Choose 1-3: ");
        while (otherLevel < 1 || otherLevel > 3) {
            otherLevel = Main.readInt("Choose 1-3: ");
        }

        String[] players = new String[2];
        int[] aiLevels = new int[2];
        if (Main.RANDOM.nextBoolean()) {
            players[0] = "MyAI";
            players[1] = "OtherStudentAI";
            aiLevels[0] = myLevel;
            aiLevels[1] = otherLevel;
        } else {
            players[0] = "OtherStudentAI";
            players[1] = "MyAI";
            aiLevels[0] = otherLevel;
            aiLevels[1] = myLevel;
        }

        System.out.println("Coin toss complete. First player: " + players[0]);
        Main.playMultiRound(players, 2, dictionary, 1, "Phase 5: AI vs AI Contest", board, aiLevels);
    }
}
