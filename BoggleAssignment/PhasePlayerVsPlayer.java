package BoggleAssignment;

public class PhasePlayerVsPlayer {
    public static void run(String[] dictionary) {
        System.out.print("Player 1 name: ");
        String p1 = Main.SCANNER.nextLine().trim();
        System.out.print("Player 2 name: ");
        String p2 = Main.SCANNER.nextLine().trim();
        String[] players = new String[2];
        players[0] = Main.emptyFallbackName(p1, "Player1");
        players[1] = Main.emptyFallbackName(p2, "Player2");
        Main.playMultiRound(players, 2, dictionary, 0, "Phase 1: Player vs Player", null);
    }
}
