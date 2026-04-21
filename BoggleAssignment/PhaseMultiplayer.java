package BoggleAssignment;

public class PhaseMultiplayer {
    public static void run(String[] dictionary) {
        int count = Main.readInt("How many human players? ");
        while (count < 3 || count > 5) {
            System.out.println("For multiplayer, choose 3 to 5 players.");
            count = Main.readInt("How many human players? ");
        }
        String[] players = new String[count];
        for (int i = 1; i <= count; i++) {
            System.out.print("Name for player " + i + ": ");
            players[i - 1] = Main.emptyFallbackName(Main.SCANNER.nextLine().trim(), "Player" + i);
        }
        Main.playMultiRound(players, count, dictionary, 0, "Phase 3: Multiplayer", null);
    }
}
