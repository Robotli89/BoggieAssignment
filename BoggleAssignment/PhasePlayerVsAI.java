package BoggleAssignment;

public class PhasePlayerVsAI {
    public static void run(String[] dictionary) {
        System.out.print("Human player name: ");
        String human = Main.emptyFallbackName(Main.SCANNER.nextLine().trim(), "Human");
        System.out.println("AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int aiLevel = Main.readInt("Choose 1-3: ");
        while (aiLevel < 1 || aiLevel > 3) {
            aiLevel = Main.readInt("Choose 1-3: ");
        }
        String[] players = new String[2];
        if (Main.RANDOM.nextBoolean()) {
            players[0] = "AI";
            players[1] = human;
        } else {
            players[0] = human;
            players[1] = "AI";
        }
        System.out.println("Random toss: " + players[0] + " goes first.");
        int[] aiLevels = new int[2];
        aiLevels[0] = players[0].equals("AI") ? aiLevel : 0;
        aiLevels[1] = players[1].equals("AI") ? aiLevel : 0;
        Main.playMultiRound(players, 2, dictionary, 1, "Phase 2: Player vs AI", null, aiLevels);
    }
}
