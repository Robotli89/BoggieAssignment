package BoggleAssignment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MyBoggle {
    public static final Scanner SCANNER = new Scanner(System.in);
    public static final Random RANDOM = new Random();
    public static final int BOARD_SIZE = 5;
    public static final int MAX_DICTIONARY_WORDS = 300000;
    public static final int MAX_USED_WORDS = 50000;
    public static final String[] DICE = {
            "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
            "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW",
            "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR",
            "DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
            "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"
    };
    public static final String DEFAULT_DICTIONARY_PRIMARY = "wordllist.txt";
    public static final String DEFAULT_DICTIONARY_SECONDARY = "wordlist.txt";
    public static final String RESULTS_FILE = "boggle_results.txt";
    public static int dictionaryCount = 0;

    public static void main(String[] args) {
        String[] dictionary = loadDictionary();
        if (dictionaryCount == 0) {
            System.out.println("No dictionary words loaded. Add wordllist.txt or wordlist.txt and restart.");
            return;
        }

        System.out.println("Dictionary loaded with " + dictionaryCount + " words.");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose a mode: ");
            switch (choice) {
                case 1 -> playPhase1(dictionary);
                case 2 -> playPhase2(dictionary);
                case 3 -> playPhase3(dictionary);
                case 4 -> playPhase4(dictionary);
                case 5 -> playPhase5(dictionary);
                case 0 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Thanks for playing MyBoggle.");
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("====== MyBoggle ======");
        System.out.println("1) Phase 1: Player vs Player");
        System.out.println("2) Phase 2: Player vs AI");
        System.out.println("3) Phase 3: Multiplayer");
        System.out.println("4) Phase 4: Multiplayer + AI");
        System.out.println("5) Phase 5: AI vs AI Contest");
        System.out.println("0) Exit");
    }

    public static void playPhase1(String[] dictionary) {
        System.out.print("Player 1 name: ");
        String p1 = SCANNER.nextLine().trim();
        System.out.print("Player 2 name: ");
        String p2 = SCANNER.nextLine().trim();
        String[] players = new String[2];
        players[0] = emptyFallbackName(p1, "Player1");
        players[1] = emptyFallbackName(p2, "Player2");
        playMultiRound(players, 2, dictionary, 0, "Phase 1: Player vs Player", null);
    }

    public static void playPhase2(String[] dictionary) {
        System.out.print("Human player name: ");
        String human = emptyFallbackName(SCANNER.nextLine().trim(), "Human");
        System.out.println("AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int aiLevel = readInt("Choose 1-3: ");
        while (aiLevel < 1 || aiLevel > 3) {
            aiLevel = readInt("Choose 1-3: ");
        }
        String[] players = new String[2];
        if (RANDOM.nextBoolean()) {
            players[0] = "AI";
            players[1] = human;
        } else {
            players[0] = human;
            players[1] = "AI";
        }
        System.out.println("Random toss: " + players[0] + " goes first.");
        int[] aiLevels = new int[2];
        aiLevels[0] = players[0].equals("AI") ? aiLevel : 0;
        aiLevels[1] = players[1].equals("AI") ? aiLevel : 0;
        playMultiRound(players, 2, dictionary, 1, "Phase 2: Player vs AI", null, aiLevels);
    }

    public static void playPhase3(String[] dictionary) {
        int count = readInt("How many human players? ");
        while (count < 3 || count > 5) {
            System.out.println("For multiplayer, choose 3 to 5 players.");
            count = readInt("How many human players? ");
        }
        String[] players = new String[count];
        for (int i = 1; i <= count; i++) {
            System.out.print("Name for player " + i + ": ");
            players[i - 1] = emptyFallbackName(SCANNER.nextLine().trim(), "Player" + i);
        }
        playMultiRound(players, count, dictionary, 0, "Phase 3: Multiplayer", null);
    }

    public static void playPhase4(String[] dictionary) {
        int humans = readInt("How many human players before adding AI? ");
        while (humans < 2 || humans > 4) {
            System.out.println("Pick 2 to 4 humans. With AI, total players will be 3 to 5.");
            humans = readInt("How many human players? ");
        }
        String[] players = new String[humans + 1];
        for (int i = 1; i <= humans; i++) {
            System.out.print("Name for human player " + i + ": ");
            players[i - 1] = emptyFallbackName(SCANNER.nextLine().trim(), "Player" + i);
        }
        players[humans] = "AI";
        System.out.println("AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int aiLevel = readInt("Choose 1-3: ");
        while (aiLevel < 1 || aiLevel > 3) {
            aiLevel = readInt("Choose 1-3: ");
        }
        int[] aiLevels = new int[players.length];
        aiLevels[humans] = aiLevel;
        playMultiRound(players, players.length, dictionary, 1, "Phase 4: Multiplayer + AI", null, aiLevels);
    }

    public static void playPhase5(String[] dictionary) {
        System.out.println("AI vs AI contest mode.");
        System.out.print("Enter teacher board file path (.txt): ");
        String path = SCANNER.nextLine().trim();
        while (path.length() == 0) {
            path = SCANNER.nextLine().trim();
        }

        char[][] board = loadBoardFromFile(path);
        if (board == null) {
            System.out.println("Invalid board file. This mode requires teacher provided board file.");
            return;
        }

        System.out.println("My AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int myLevel = readInt("Choose 1-3: ");
        while (myLevel < 1 || myLevel > 3) {
            myLevel = readInt("Choose 1-3: ");
        }

        System.out.println("Other student AI difficulty?");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        int otherLevel = readInt("Choose 1-3: ");
        while (otherLevel < 1 || otherLevel > 3) {
            otherLevel = readInt("Choose 1-3: ");
        }

        String[] players = new String[2];
        int[] aiLevels = new int[2];
        if (RANDOM.nextBoolean()) {
            players[0] = "MyAI";
            players[1] = "OtherStudentAI";
            aiLevels[0] = myLevel;
            aiLevels[1] = otherLevel;
        } else {
            players[0] = "OtherStudentAI";
            players[1] = "MyAI";
            aiLevels[0] = otherLevel;
            aiLevels[1] = myLevel;
        }

        System.out.println("Coin toss complete. First player: " + players[0]);
        playMultiRound(players, 2, dictionary, 1, "Phase 5: AI vs AI Contest", board, aiLevels);
    }

    public static void playMultiRound(String[] players, int playerCount, String[] dictionary, int aiEnabled,
                                      String phaseName, char[][] fixedBoard) {
        int[] aiLevels = new int[playerCount];
        playMultiRound(players, playerCount, dictionary, aiEnabled, phaseName, fixedBoard, aiLevels);
    }

    public static void playMultiRound(String[] players, int playerCount, String[] dictionary, int aiEnabled,
                                      String phaseName, char[][] fixedBoard, int[] aiLevels) {
        int[] totalScores = new int[playerCount];
        String log = "";
        log += "=== " + phaseName + " ===\n";
        char[][] board = fixedBoard == null ? generateBoard() : copyBoard(fixedBoard);
        ArrayList<String> usedWords = new ArrayList<>();
        boolean[] conceded = new boolean[playerCount];
        int concededCount = 0;

        printBoard(board);
        boolean gameOver = false;
        while (!gameOver) {
            for (int i = 0; i < playerCount; i++) {
                if (conceded[i]) {
                    continue;
                }

                String currentPlayer = players[i];
                String inputWord = "";

                if (aiEnabled == 1 && isAIName(currentPlayer)) {
                    inputWord = chooseAIWord(board, dictionary, usedWords, aiLevels[i]);
                    if (inputWord == null) {
                        System.out.println(currentPlayer + " concedes (no new words found).");
                        conceded[i] = true;
                        concededCount++;
                        log += currentPlayer + " -> CONCEDE\n";
                    } else {
                        System.out.println(currentPlayer + " plays: " + inputWord);
                    }
                } else {
                    System.out.print(currentPlayer + ", enter a word (or CONCEDE): ");
                    inputWord = SCANNER.nextLine().trim().toUpperCase();
                    if (inputWord.equals("CONCEDE")) {
                        conceded[i] = true;
                        concededCount++;
                        log += currentPlayer + " -> CONCEDE\n";
                    }
                }

                if (!conceded[i] && isValidWord(inputWord, board, dictionary, usedWords)) {
                    int score = inputWord.length();
                    totalScores[i] += score;
                    usedWords.add(inputWord);
                    System.out.println("Accepted. +" + score + " points.");
                    log += currentPlayer + " -> " + inputWord + " (valid, +" + score + ")\n";
                } else if (!conceded[i]) {
                    System.out.println("Rejected.");
                    log += currentPlayer + " -> " + inputWord + " (invalid)\n";
                }

                gameOver = shouldEndGame(phaseName, players, totalScores, conceded, concededCount, playerCount);
                if (gameOver) {
                    break;
                }
            }
        }

        int bestTotal = Integer.MIN_VALUE;
        String gameWinner = "Tie";
        for (int i = 0; i < playerCount; i++) {
            System.out.println(players[i] + " final score: " + totalScores[i]);
            log += players[i] + " final=" + totalScores[i] + "\n";
            if (totalScores[i] > bestTotal) {
                bestTotal = totalScores[i];
                gameWinner = players[i];
            } else if (totalScores[i] == bestTotal) {
                gameWinner = "Tie";
            }
        }
        System.out.println("Game winner: " + gameWinner);
        log += "Game winner: " + gameWinner + "\n";
        appendResults(log);
    }

    public static boolean shouldEndGame(String phaseName, String[] players, int[] totalScores, boolean[] conceded,
                                        int concededCount, int playerCount) {
        if (phaseName.equals("Phase 1: Player vs Player") || phaseName.equals("Phase 3: Multiplayer")) {
            return concededCount == playerCount;
        }

        if (phaseName.equals("Phase 2: Player vs AI")) {
            int aiIndex = -1;
            int humanIndex = -1;
            for (int i = 0; i < playerCount; i++) {
                if (isAIName(players[i])) {
                    aiIndex = i;
                } else {
                    humanIndex = i;
                }
            }
            if (aiIndex != -1 && conceded[aiIndex]) {
                return true;
            }
            if (humanIndex != -1 && conceded[humanIndex] && aiIndex != -1 && totalScores[humanIndex] < totalScores[aiIndex]) {
                return true;
            }
            return concededCount == playerCount;
        }

        if (phaseName.equals("Phase 4: Multiplayer + AI")) {
            int aiIndex = -1;
            for (int i = 0; i < playerCount; i++) {
                if (isAIName(players[i])) {
                    aiIndex = i;
                    break;
                }
            }
            if (aiIndex != -1 && conceded[aiIndex]) {
                return true;
            }

            boolean allHumansConceded = true;
            for (int i = 0; i < playerCount; i++) {
                if (i != aiIndex && !conceded[i]) {
                    allHumansConceded = false;
                    break;
                }
            }
            if (aiIndex != -1 && allHumansConceded) {
                boolean aiHighest = true;
                for (int i = 0; i < playerCount; i++) {
                    if (i != aiIndex && totalScores[i] > totalScores[aiIndex]) {
                        aiHighest = false;
                        break;
                    }
                }
                if (aiHighest) {
                    return true;
                }
            }
            return concededCount == playerCount;
        }

        if (phaseName.equals("Phase 5: AI vs AI Contest")) {
            return concededCount == playerCount;
        }

        return concededCount == playerCount;
    }

    public static String chooseAIWord(char[][] board, String[] dictionary, ArrayList<String> usedWords, int level) {
        String chosen = null;
        ArrayList<String> valid = new ArrayList<>();
        for (int i = 0; i < dictionaryCount; i++) {
            String word = dictionary[i];
            if (word != null && word.length() >= 3 && !containsWord(usedWords, word) && existsOnBoard(word, board)) {
                if (level == 1) {
                    if (chosen == null || word.length() < chosen.length()) {
                        chosen = word;
                    }
                } else if (level == 3) {
                    if (chosen == null || word.length() > chosen.length()) {
                        chosen = word;
                    }
                } else {
                    valid.add(word);
                }
            }
        }
        if (level == 2) {
            if (valid.size() == 0) {
                return null;
            }
            return valid.get(RANDOM.nextInt(valid.size()));
        }
        return chosen;
    }

    public static boolean isValidWord(String word, char[][] board, String[] dictionary, ArrayList<String> usedWords) {
        if (word == null || word.length() < 3) {
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

    public static char[][] generateBoard() {
        char[] letters = new char[BOARD_SIZE * BOARD_SIZE];
        for (int i = 0; i < DICE.length; i++) {
            int face = RANDOM.nextInt(DICE[i].length());
            letters[i] = DICE[i].charAt(face);
        }
        shuffleCharArray(letters);
        char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
        int k = 0;
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = letters[k++];
            }
        }
        return board;
    }

    public static void printBoard(char[][] board) {
        System.out.println("\nBoggle Board:");
        for (char[] row : board) {
            for (char ch : row) {
                System.out.print(ch + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static String[] loadDictionary() {
        String[] words = new String[MAX_DICTIONARY_WORDS];
        boolean loaded = loadDictionaryFile(DEFAULT_DICTIONARY_PRIMARY, words);
        if (!loaded) {
            loadDictionaryFile(DEFAULT_DICTIONARY_SECONDARY, words);
        }
        if (dictionaryCount == 0) {
            boolean loadedFromFolder = loadDictionaryFile("BoggleAssignment\\" + DEFAULT_DICTIONARY_PRIMARY, words);
            if (!loadedFromFolder) {
                loadDictionaryFile("BoggleAssignment\\" + DEFAULT_DICTIONARY_SECONDARY, words);
            }
        }
        return words;
    }

    public static boolean loadDictionaryFile(String filename, String[] words) {
        File file = new File(filename);
        if (!file.exists()) {
            return false;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            dictionaryCount = 0;
            while (fileScanner.hasNextLine() && dictionaryCount < words.length) {
                String cleaned = fileScanner.nextLine().trim().toUpperCase();
                if (cleaned.length() >= 3 && !containsWord(words, dictionaryCount, cleaned)) {
                    words[dictionaryCount] = cleaned;
                    dictionaryCount++;
                }
            }
            fileScanner.close();
            return true;
        } catch (Exception e) {
            System.out.println("Could not read dictionary file " + filename);
            return false;
        }
    }

    public static char[][] loadBoardFromFile(String filename) {
        char[] chars = new char[BOARD_SIZE * BOARD_SIZE];
        int charCount = 0;
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            while (fileScanner.hasNextLine() && charCount < chars.length) {
                String line = fileScanner.nextLine().toUpperCase();
                for (int i = 0; i < line.length() && charCount < chars.length; i++) {
                    char ch = line.charAt(i);
                    if (Character.isLetter(ch)) {
                        chars[charCount] = ch;
                        charCount++;
                    }
                }
            }
            fileScanner.close();
        } catch (Exception e) {
            return null;
        }

        if (charCount < BOARD_SIZE * BOARD_SIZE) {
            return null;
        }

        char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
        int index = 0;
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = chars[index++];
            }
        }
        return board;
    }

    public static void appendResults(String text) {
        try {
            FileWriter fileWriter = new FileWriter(RESULTS_FILE, true);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.write(text);
            writer.write("\n");
            writer.flush();
            writer.close();
            System.out.println("Results saved to " + RESULTS_FILE);
        } catch (Exception e) {
            System.out.println("Could not write results file.");
        }
    }

    public static String emptyFallbackName(String name, String fallback) {
        return name == null || name.isEmpty() ? fallback : name;
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public static boolean isAIName(String name) {
        return name.equalsIgnoreCase("AI")
                || name.equalsIgnoreCase("MYAI")
                || name.equalsIgnoreCase("OTHERSTUDENTAI");
    }

    public static char[][] copyBoard(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int r = 0; r < original.length; r++) {
            System.arraycopy(original[r], 0, copy[r], 0, original[r].length);
        }
        return copy;
    }

    public static void shuffleCharArray(char[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
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
