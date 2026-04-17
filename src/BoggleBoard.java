/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Manages the 5x5 Boggle board using the standard 25-dice
 *              Boggle Deluxe configuration. Each die has 6 letter faces.
 *              Dice are shuffled and rolled to produce a random board.
 */
import java.util.Arrays;
import java.util.Random;

public class BoggleBoard {

    // ---------------------------------------------------------------
    // 25 Boggle Deluxe dice, each string = 6 faces of one die
    // ---------------------------------------------------------------
    private static final String[] DICE = {
        "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
        "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW",
        "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR",
        "DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
        "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"
    };

    /** Board dimension (5 for a standard 5x5 Boggle board) */
    public static final int SIZE = 5;

    private char[][] board;
    private Random random;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** Creates a board with a new random layout. */
    public BoggleBoard() {
        random = new Random();
        board  = new char[SIZE][SIZE];
        generateBoard();
    }

    /**
     * Creates a board with a fixed seed (useful for Phase 5 reproducibility).
     * @param seed random seed value
     */
    public BoggleBoard(long seed) {
        random = new Random(seed);
        board  = new char[SIZE][SIZE];
        generateBoard();
    }

    // ---------------------------------------------------------------
    // Board generation
    // ---------------------------------------------------------------

    /**
     * Generates a new random board.
     * Steps: randomly swap the 25 dice, then roll each die by picking
     * one random face. Simulates physically shaking and placing the tray.
     */
    public void generateBoard() {
        // Copy dice so we can shuffle without modifying the original array
        String[] shuffledDice = Arrays.copyOf(DICE, DICE.length);

        // Randomly swap dice positions.
        for (int i = 0; i < shuffledDice.length; i++) {
            int j = random.nextInt(shuffledDice.length);
            String temp      = shuffledDice[i];
            shuffledDice[i]  = shuffledDice[j];
            shuffledDice[j]  = temp;
        }

        // Roll each die and place it in the 5x5 grid
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String die = shuffledDice[index++];
                board[row][col] = die.charAt(random.nextInt(die.length()));
            }
        }
    }

    /**
     * Directly sets the board from a 2D char array.
     * Used in Phase 5 when the board is loaded from a contest file.
     * @param newBoard a SIZE x SIZE char array of uppercase letters
     */
    public void setBoard(char[][] newBoard) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = newBoard[r][c];
            }
        }
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    /** @return the current 5x5 board as a char array */
    public char[][] getBoard() { return board; }

    /** @return the board dimension (always 5) */
    public int getSize() { return SIZE; }

    /**
     * Returns a formatted string representation of the board.
     * @return multi-line board display string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+---+---+---+---+---+\n");
        for (int row = 0; row < SIZE; row++) {
            sb.append("| ");
            for (int col = 0; col < SIZE; col++) {
                sb.append(board[row][col]).append(" | ");
            }
            sb.append("\n+---+---+---+---+---+\n");
        }
        return sb.toString();
    }
}
