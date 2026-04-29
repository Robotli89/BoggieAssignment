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
 *              Uses sequential search to find possible starting cells, then
 *              uses recursive DFS (depth-first search) with backtracking.
 */
public class WordValidator {

    private char[][] board;
    private int      size;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Creates a validator for the current board. */
    public WordValidator(char[][] newBoard) {
        board = newBoard;
        size  = newBoard.length;
    }

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /** Updates the board used for validation. */
    public void setBoard(char[][] newBoard) {
        board = newBoard;
        size  = newBoard.length;
    }

    /** Checks if a word can be built from adjacent board cells. */
    public boolean isWordOnBoard(String word) {
        if (word == null || word.length() == 0) {
            return false;
        }
        String upper = word.toUpperCase();

        boolean[][] visited = new boolean[size][size];

        // Sequentially search the grid for matching first-letter cells.
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

    /** Recursively matches the remaining word from one board cell. */
    private boolean dfs(String word, int index, int row, int col, boolean[][] visited) {
        // Full word matched.
        if (index == word.length()) {
            return true;
        }

        // Reject invalid cells and wrong letters.
        if (row < 0 || row >= size) {
            return false;
        }
        if (col < 0 || col >= size) {
            return false;
        }
        if (visited[row][col]) {
            return false;
        }
        if (board[row][col] != word.charAt(index)) {
            return false;
        }

        // Mark the cell for this path.
        visited[row][col] = true;

        // Check all 8 directions.
        int[] dr = {-1, -1, -1,  0,  0,  1,  1,  1};
        int[] dc = {-1,  0,  1, -1,  1, -1,  0,  1};

        for (int d = 0; d < 8; d++) {
            if (dfs(word, index + 1, row + dr[d], col + dc[d], visited)) {
                visited[row][col] = false; // backtrack
                return true;
            }
        }

        // Backtrack for other paths.
        visited[row][col] = false;
        return false;
    }
}
