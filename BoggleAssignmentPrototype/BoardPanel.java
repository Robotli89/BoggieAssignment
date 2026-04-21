/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Swing panel that renders the 5x5 Boggle board.
 *              Each cell is a styled JLabel.
 *              Supports highlighting cells to show a found word's path.
 */
import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    // ---------------------------------------------------------------
    // Visual constants
    // ---------------------------------------------------------------

    private static final int   CELL_SIZE    = 72;
    private static final Font  CELL_FONT    = new Font("Arial", Font.BOLD, 26);
    private static final Color NORMAL_BG    = new Color(173, 216, 230);  // light blue
    private static final Color HIGHLIGHT_BG = new Color(255, 215,   0);  // gold
    private static final Color BORDER_COLOR = new Color( 70, 130, 180);  // steel blue
    private static final Color TEXT_COLOR   = Color.DARK_GRAY;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private JLabel[][] cells;
    private int        size;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates an empty board panel for a board of the given dimension.
     * @param size board dimension (5 for the standard Boggle board)
     */
    public BoardPanel(int boardSize) {
        size  = boardSize;
        cells = new JLabel[boardSize][boardSize];

        setLayout(new GridLayout(boardSize, boardSize, 4, 4));
        setBackground(BORDER_COLOR);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        initializeCells();
    }

    // ---------------------------------------------------------------
    // Initialisation
    // ---------------------------------------------------------------

    /**
     * Creates and adds a styled JLabel for each board position.
     */
    private void initializeCells() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JLabel cell = new JLabel("?", SwingConstants.CENTER);
                cell.setFont(CELL_FONT);
                cell.setOpaque(true);
                cell.setBackground(NORMAL_BG);
                cell.setForeground(TEXT_COLOR);
                cell.setBorder(BorderFactory.createRaisedBevelBorder());
                cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cells[row][col] = cell;
                add(cell);
            }
        }
    }

    // ---------------------------------------------------------------
    // Public update methods
    // ---------------------------------------------------------------

    /**
     * Refreshes all cells to display the letters in the given board array.
     * Clears any existing highlights.
     * @param board the board to display (size x size uppercase chars)
     */
    public void updateBoard(char[][] board) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setText(String.valueOf(board[row][col]));
                cells[row][col].setBackground(NORMAL_BG);
            }
        }
        repaint();
    }

    /**
     * Highlights the specified cells in gold (to show a word's path).
     * All other cells are reset to the normal background.
     * @param highlights size x size boolean array; true = highlight this cell
     */
    public void highlightCells(boolean[][] highlights) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setBackground(
                        highlights[row][col] ? HIGHLIGHT_BG : NORMAL_BG);
            }
        }
        repaint();
    }

    /**
     * Resets every cell back to the normal (un-highlighted) background.
     */
    public void clearHighlights() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setBackground(NORMAL_BG);
            }
        }
        repaint();
    }
}
