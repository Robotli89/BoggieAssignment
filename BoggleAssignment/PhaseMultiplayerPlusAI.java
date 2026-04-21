package BoggleAssignment;

public class PhaseMultiplayerPlusAI {
    public static void run(String[] dictionary) {
        int humans = Main.readInt("How many human players before adding AI? ");
        while (humans < 2 || humans > 4) {
            System.out.println("Pick 2 to 4 humans. With AI, total players will be 3 to 5.");
            humans = Main.readInt("How many human players? ");
        }
        String[] players = new String[humans + 1];
        for (int i = 1; i <= humans; i++) {
            System.out.print("Name for human player " + i + ": ");
            players[i - 1] = Main.emptyFallbackName(Main.SCANNER.nextLine().trim(), "Player" + i);
        }
        players[humans] = "AI";
        System.out.println("AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int aiLevel = Main.readInt("Choose 1-3: ");
        while (aiLevel < 1 || aiLevel > 3) {
            aiLevel = Main.readInt("Choose 1-3: ");
        }
        int[] aiLevels = new int[players.length];
        aiLevels[humans] = aiLevel;
        Main.playMultiRound(players, players.length, dictionary, 1, "Phase 4: Multiplayer + AI", null, aiLevels);
    }
}
