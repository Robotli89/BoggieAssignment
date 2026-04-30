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

    /** Standard board dimension. */
    public static final int SIZE = 5;

    private char[][] board;
    private Random random;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** Creates a random board. */
    public BoggleBoard() {
        random = new Random();
        board  = new char[SIZE][SIZE];
        generateBoard();
    }

    /** Creates a random board from a fixed seed. */
    public BoggleBoard(long seed) {
        random = new Random(seed);
        board  = new char[SIZE][SIZE];
        generateBoard();
    }

    // ---------------------------------------------------------------
    // Board generation
    // ---------------------------------------------------------------

    /** Shuffles the dice and rolls one face from each die. */
    public void generateBoard() {
        // Copy dice before shuffling.
        String[] shuffledDice = new String[DICE.length];
        for (int i = 0; i < DICE.length; i++) {
            shuffledDice[i] = DICE[i];
        }

        // Shuffle dice positions.
        for (int i = 0; i < shuffledDice.length; i++) {
            int j = random.nextInt(shuffledDice.length);
            String temp      = shuffledDice[i];
            shuffledDice[i]  = shuffledDice[j];
            shuffledDice[j]  = temp;
        }

        // Roll each die into the grid.
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String die = shuffledDice[index++];
                board[row][col] = die.charAt(random.nextInt(die.length()));
            }
        }
    }

    /** Copies a supplied board into this board. */
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

    /** Current board array. */
    public char[][] getBoard() { return board; }

    /** Builds the text-mode board display. */
    public String toString() {
        String text = "";
        text = text + "+---+---+---+---+---+\n";
        for (int row = 0; row < SIZE; row++) {
            text = text + "| ";
            for (int col = 0; col < SIZE; col++) {
                text = text + board[row][col] + " | ";
            }
            text = text + "\n+---+---+---+---+---+\n";
        }
        return text;
    }
}
