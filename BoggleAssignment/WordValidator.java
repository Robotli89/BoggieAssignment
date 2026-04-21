package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Checks whether a given word can be formed on the Boggle board
 *              by tracing a valid path through adjacent (horizontal, vertical,
 *              or diagonal) letters without reusing any cell.
 *              Uses recursive DFS (depth-first search) with backtracking.
 */
public class WordValidator {

    private char[][] board;
    private int      size;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a validator for the given board.
     * @param board the 5x5 board of uppercase letters
     */
    public WordValidator(char[][] newBoard) {
        board = newBoard;
        size  = newBoard.length;
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /**
     * Updates the board reference when the board changes (e.g., shaken up).
     * @param board the new board to validate against
     */
    public void setBoard(char[][] newBoard) {
        board = newBoard;
        size  = newBoard.length;
    }

    /**
     * Returns true if the given word can be formed on the current board.
     * A valid path requires:
     *   - Letters are horizontally, vertically, or diagonally adjacent.
     *   - No single board cell is used more than once in the path.
     *
     * @param word the word to search for (case-insensitive)
     * @return true if the word exists on the board
     */
    public boolean isWordOnBoard(String word) {
        if (word == null || word.isEmpty()) return false;
        String upper = word.toUpperCase();

        boolean[][] visited = new boolean[size][size];

        // Try to start a DFS path from every cell on the board
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == upper.charAt(0)) {
                    if (dfs(upper, 0, row, col, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Private DFS helper
    // ---------------------------------------------------------------

    /**
     * Recursive DFS that attempts to match word[index..] starting at (row, col).
     *
     * @param word    the target word (uppercase)
     * @param index   index of the next character to match
     * @param row     current row on the board
     * @param col     current column on the board
     * @param visited marks cells already used in the current path
     * @return true if the full word is found from this position
     */
    private boolean dfs(String word, int index, int row, int col, boolean[][] visited) {
        // Base case: all characters matched successfully
        if (index == word.length()) return true;

        // Bounds / already-visited / character mismatch checks
        if (row < 0 || row >= size)   return false;
        if (col < 0 || col >= size)   return false;
        if (visited[row][col])        return false;
        if (board[row][col] != word.charAt(index)) return false;

        // Mark this cell as part of the current path
        visited[row][col] = true;

        // All 8 neighbor directions (horizontal, vertical, diagonal)
        int[] dr = {-1, -1, -1,  0,  0,  1,  1,  1};
        int[] dc = {-1,  0,  1, -1,  1, -1,  0,  1};

        for (int d = 0; d < 8; d++) {
            if (dfs(word, index + 1, row + dr[d], col + dc[d], visited)) {
                visited[row][col] = false; // backtrack before returning
                return true;
            }
        }

        // Backtrack: unmark this cell so other paths can use it
        visited[row][col] = false;
        return false;
    }
}
