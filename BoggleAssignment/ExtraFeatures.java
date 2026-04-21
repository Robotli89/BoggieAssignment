package BoggleAssignment;

public class ExtraFeatures {
    public static int readMinWordLength() {
        int value = Main.readInt("Minimum word length (at least 3): ");
        while (value < 3) {
            value = Main.readInt("Minimum word length (at least 3): ");
        }
        return value;
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String ans = Main.SCANNER.nextLine().trim().toUpperCase();
            if (ans.equals("Y") || ans.equals("YES")) {
                return true;
            }
            if (ans.equals("N") || ans.equals("NO")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }
}
