package BoggleAssignment;

import java.util.ArrayList;

public class WordChecker {
    public static boolean isValidWord(String word, char[][] board, String[] dictionary, int dictionaryCount,
                                      ArrayList<String> usedWords, int minWordLength) {
        if (word == null || word.length() < minWordLength) {
            return false;
        }
        if (containsWord(usedWords, word)) {
            return false;
        }
        if (!containsWord(dictionary, dictionaryCount, word)) {
            return false;
        }
        return existsOnBoard(word, board);
    }

    public static boolean existsOnBoard(String word, char[][] board) {
        int n = board.length;
        boolean[][] visited = new boolean[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == word.charAt(0)) {
                    if (dfs(word, 0, r, c, board, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean dfs(String word, int index, int r, int c, char[][] board, boolean[][] visited) {
        if (index == word.length()) {
            return true;
        }
        int n = board.length;
        if (r < 0 || c < 0 || r >= n || c >= n) {
            return false;
        }
        if (visited[r][c]) {
            return false;
        }
        if (board[r][c] != word.charAt(index)) {
            return false;
        }

        visited[r][c] = true;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                if (dfs(word, index + 1, r + dr, c + dc, board, visited)) {
                    visited[r][c] = false;
                    return true;
                }
            }
        }
        visited[r][c] = false;
        return index + 1 == word.length();
    }

    public static boolean containsWord(String[] arr, int length, String target) {
        for (int i = 0; i < length; i++) {
            if (arr[i] != null && arr[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWord(ArrayList<String> arr, String target) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).equals(target)) {
                return true;
            }
        }
        return false;
    }
}
