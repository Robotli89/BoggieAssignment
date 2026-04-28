package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Swing panel that shows the 5x5 Boggle board.
 */
import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    // ---------------------------------------------------------------
    // Visual constants
    // ---------------------------------------------------------------

    private static final int   CELL_SIZE    = 72;
    private static final Font  CELL_FONT    = new Font("Dialog", Font.PLAIN, 24);
    private static final Color NORMAL_BG    = Color.WHITE;
    private static final Color HIGHLIGHT_BG = Color.YELLOW;
    private static final Color BORDER_COLOR = Color.GRAY;
    private static final Color TEXT_COLOR   = Color.BLACK;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private JLabel[][] cells;
    private int        size;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates an empty board panel. */
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

    /** Creates one label per board cell. */
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

    /** Updates cell text and clears highlights. */
    public void updateBoard(char[][] board) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setText(String.valueOf(board[row][col]));
                cells[row][col].setBackground(NORMAL_BG);
            }
        }
        repaint();
    }

    /** Highlights selected cells. */
    public void highlightCells(boolean[][] highlights) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (highlights[row][col]) {
                    cells[row][col].setBackground(HIGHLIGHT_BG);
                } else {
                    cells[row][col].setBackground(NORMAL_BG);
                }
            }
        }
        repaint();
    }

    /** Clears all cell highlights. */
    public void clearHighlights() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setBackground(NORMAL_BG);
            }
        }
        repaint();
    }
}
