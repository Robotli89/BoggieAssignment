package BoggleAssignment;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static final Scanner SCANNER = new Scanner(System.in);
    public static final Random RANDOM = new Random();
    public static final int BOARD_SIZE = 5;
    public static final int MAX_DICTIONARY_WORDS = 300000;
    public static final int TURN_TIME_SECONDS = 15;
    public static final int TARGET_SCORE = 50;
    public static final String[] DICE = {
            "AAAFRS", "AEEGMU", "CEIILT", "DHHNOT", "FIPRSY",
            "AAEEEE", "AEGMNN", "CEILPT", "DHLNOR", "GORRVW",
            "AAFIRS", "AFIRSY", "CEIPST", "EIIITT", "HIPRRY",
            "ADENNN", "BJKQXZ", "DDLNOR", "EMOTTT", "NOOTUW",
            "AEEEEM", "CCNSTW", "DHHLOR", "ENSSSU", "OOOTTU"
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
        printWelcomeAndRules();
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

    public static void printWelcomeAndRules() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("Welcome to MyBoggle!");
        System.out.println("========================================");
        System.out.println("Rules Intro:");
        System.out.println("1) Build words from touching letters (horizontal, vertical, diagonal).");
        System.out.println("2) A board letter cannot be reused within the same word.");
        System.out.println("3) Words must exist in the dictionary file and on the board.");
        System.out.println("4) Each valid word gives points equal to its length.");
        System.out.println("5) Turn time limit is " + TURN_TIME_SECONDS + " seconds.");
        System.out.println("6) A timeout counts as a wrong attempt.");
        System.out.println("7) Two wrong attempts in a row cause auto-concede.");
        System.out.println("8) Target score is " + TARGET_SCORE + " points.");
        System.out.println("9) You can type CONCEDE to pass/stop based on mode rules.");
        System.out.println("10) After each turn, you can choose to quit with NO RESULT.");
        System.out.println("========================================");
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
        PhasePlayerVsPlayer.run(dictionary);
    }

    public static void playPhase2(String[] dictionary) {
        PhasePlayerVsAI.run(dictionary);
    }

    public static void playPhase3(String[] dictionary) {
        PhaseMultiplayer.run(dictionary);
    }

    public static void playPhase4(String[] dictionary) {
        PhaseMultiplayerPlusAI.run(dictionary);
    }

    public static void playPhase5(String[] dictionary) {
        PhaseAIVsAI.run(dictionary);
    }

    public static void playMultiRound(String[] players, int playerCount, String[] dictionary, int aiEnabled,
                                      String phaseName, char[][] fixedBoard) {
        int[] aiLevels = new int[playerCount];
        playMultiRound(players, playerCount, dictionary, aiEnabled, phaseName, fixedBoard, aiLevels);
    }

    public static void playMultiRound(String[] players, int playerCount, String[] dictionary, int aiEnabled,
                                      String phaseName, char[][] fixedBoard, int[] aiLevels) {
        int minWordLength = readMinWordLength();
        int[] totalScores = new int[playerCount];
        int[] wrongStreak = new int[playerCount];
        String log = "";
        log += "=== " + phaseName + " ===\n";
        log += "Min word length: " + minWordLength + "\n";
        char[][] board = fixedBoard == null ? generateBoard() : copyBoard(fixedBoard);
        ArrayList<String> usedWords = new ArrayList<>();
        boolean[] conceded = new boolean[playerCount];
        int concededCount = 0;
        boolean shakeUsed = false;
        boolean noResult = false;

        printBoard(board);
        boolean gameOver = false;
        while (!gameOver) {
            for (int i = 0; i < playerCount; i++) {
                if (conceded[i]) {
                    continue;
                }

                String currentPlayer = players[i];
                String inputWord = "";
                boolean attemptedWord = false;
                boolean valid = false;

                if (aiEnabled == 1 && isAIName(currentPlayer)) {
                    inputWord = chooseAIWord(board, dictionary, usedWords, aiLevels[i], minWordLength);
                    if (inputWord == null) {
                        System.out.println(currentPlayer + " concedes (no new words found).");
                        conceded[i] = true;
                        concededCount++;
                        log += currentPlayer + " -> CONCEDE\n";
                    } else {
                        attemptedWord = true;
                        System.out.println(currentPlayer + " plays: " + inputWord);
                    }
                } else {
                    long startTime = System.currentTimeMillis();
                    System.out.print(currentPlayer + ", enter a word within " + TURN_TIME_SECONDS + "s (or CONCEDE): ");
                    inputWord = SCANNER.nextLine().trim().toUpperCase();
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    if (elapsed > TURN_TIME_SECONDS) {
                        inputWord = "";
                        attemptedWord = true;
                        System.out.println("Time up!.");
                    } else {
                        attemptedWord = !inputWord.equals("CONCEDE");
                    }
                    if (inputWord.equals("CONCEDE")) {
                        conceded[i] = true;
                        concededCount++;
                        log += currentPlayer + " -> CONCEDE\n";
                        if (!shakeUsed && (phaseName.equals("Phase 2: Player vs AI") || phaseName.equals("Phase 4: Multiplayer + AI"))) {
                            if (readYesNo("Use one-time Shake-up the board now? (Y/N): ")) {
                                board = generateBoard();
                                usedWords.clear();
                                conceded[i] = false;
                                concededCount--;
                                shakeUsed = true;
                                System.out.println("Board shaken up.");
                                printBoard(board);
                                log += "Shake-up used after concede.\n";
                            }
                        }
                    }
                }

                if (!conceded[i] && attemptedWord && isValidWord(inputWord, board, dictionary, usedWords, minWordLength)) {
                    int score = inputWord.length();
                    totalScores[i] += score;
                    usedWords.add(inputWord);
                    wrongStreak[i] = 0;
                    valid = true;
                    System.out.println("Accepted. +" + score + " points.");
                    log += currentPlayer + " -> " + inputWord + " (valid, +" + score + ")\n";
                } else if (!conceded[i] && attemptedWord) {
                    wrongStreak[i]++;
                    System.out.println("Rejected.");
                    log += currentPlayer + " -> " + inputWord + " (invalid)\n";

                    if (shouldGiveSecondAttempt(phaseName, players, conceded, playerCount, i)) {
                        String retry = "";
                        long retryStart = System.currentTimeMillis();
                        System.out.print(currentPlayer + ", second attempt within " + TURN_TIME_SECONDS + "s: ");
                        retry = SCANNER.nextLine().trim().toUpperCase();
                        long retryElapsed = (System.currentTimeMillis() - retryStart) / 1000;
                        if (retryElapsed <= TURN_TIME_SECONDS && isValidWord(retry, board, dictionary, usedWords, minWordLength)) {
                            int score = retry.length();
                            totalScores[i] += score;
                            usedWords.add(retry);
                            wrongStreak[i] = 0;
                            valid = true;
                            System.out.println("Accepted on second attempt. +" + score + " points.");
                            log += currentPlayer + " -> " + retry + " (second attempt valid, +" + score + ")\n";
                        } else {
                            conceded[i] = true;
                            concededCount++;
                            wrongStreak[i] = 0;
                            System.out.println(currentPlayer + " auto-concedes after second failed attempt.");
                            log += currentPlayer + " -> AUTO CONCEDE (second failed)\n";
                        }
                    } else if (wrongStreak[i] >= 2) {
                        conceded[i] = true;
                        concededCount++;
                        wrongStreak[i] = 0;
                        System.out.println(currentPlayer + " auto-concedes after two time-up in a row.");
                        log += currentPlayer + " -> AUTO CONCEDE (2 time-up streak)\n";
                    }
                }

                if (!gameOver && !phaseName.equals("Phase 5: AI vs AI Contest") && reachedTarget(totalScores, TARGET_SCORE)) {
                    gameOver = true;
                }
                if (!gameOver) {
                    gameOver = shouldEndGame(phaseName, players, totalScores, conceded, concededCount, playerCount);
                }
                if (!gameOver && !shakeUsed && (phaseName.equals("Phase 1: Player vs Player") || phaseName.equals("Phase 3: Multiplayer"))
                        && concededCount == playerCount) {
                    if (readYesNo("All players conceded. Use one-time Shake-up the board? (Y/N): ")) {
                        board = generateBoard();
                        usedWords.clear();
                        for (int j = 0; j < playerCount; j++) {
                            conceded[j] = false;
                            wrongStreak[j] = 0;
                        }
                        concededCount = 0;
                        shakeUsed = true;
                        System.out.println("Board shaken up.");
                        printBoard(board);
                        log += "Shake-up used after all conceded.\n";
                    } else {
                        gameOver = true;
                    }
                }
                if (!gameOver && (phaseName.equals("Phase 1: Player vs Player") || phaseName.equals("Phase 3: Multiplayer"))) {
                    int onlyActive = getOnlyActivePlayer(conceded);
                    if (onlyActive != -1) {
                        int maxOther = getMaxOtherScore(totalScores, onlyActive);
                        if (totalScores[onlyActive] > maxOther && valid) {
                            gameOver = true;
                        }
                    }
                }
                if (!gameOver) {
                    if (readYesNo("Quit game with NO RESULT now? (Y/N): ")) {
                        noResult = true;
                        gameOver = true;
                    }
                }
                if (gameOver) {
                    break;
                }
            }
        }

        if (noResult) {
            System.out.println("Game ended with NO RESULT.");
            appendResults(log + "Game ended with NO RESULT.\n");
            return;
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
        return WinChecker.shouldEndGame(phaseName, players, totalScores, conceded, concededCount, playerCount);
    }

    public static String chooseAIWord(char[][] board, String[] dictionary, ArrayList<String> usedWords, int level, int minWordLength) {
        String chosen = null;
        ArrayList<String> valid = new ArrayList<>();
        for (int i = 0; i < dictionaryCount; i++) {
            String word = dictionary[i];
            if (word != null && word.length() >= minWordLength && !containsWord(usedWords, word) && existsOnBoard(word, board)) {
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

    public static boolean isValidWord(String word, char[][] board, String[] dictionary, ArrayList<String> usedWords,
                                      int minWordLength) {
        return WordChecker.isValidWord(word, board, dictionary, dictionaryCount, usedWords, minWordLength);
    }

    public static int readMinWordLength() {
        return ExtraFeatures.readMinWordLength();
    }

    public static boolean readYesNo(String prompt) {
        return ExtraFeatures.readYesNo(prompt);
    }

    public static boolean reachedTarget(int[] scores, int target) {
        return WinChecker.reachedTarget(scores, target);
    }

    public static int getOnlyActivePlayer(boolean[] conceded) {
        return WinChecker.getOnlyActivePlayer(conceded);
    }

    public static int getMaxOtherScore(int[] scores, int myIndex) {
        return WinChecker.getMaxOtherScore(scores, myIndex);
    }

    public static boolean shouldGiveSecondAttempt(String phaseName, String[] players, boolean[] conceded, int playerCount, int currentIndex) {
        return WinChecker.shouldGiveSecondAttempt(phaseName, conceded, playerCount, currentIndex);
    }

    public static boolean existsOnBoard(String word, char[][] board) {
        return WordChecker.existsOnBoard(word, board);
    }

    public static boolean dfs(String word, int index, int r, int c, char[][] board, boolean[][] visited) {
        return WordChecker.dfs(word, index, r, c, board, visited);
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
        ResultFileWriter.appendResults(RESULTS_FILE, text);
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
        return WinChecker.isAIName(name);
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
        return WordChecker.containsWord(arr, length, target);
    }

    public static boolean containsWord(ArrayList<String> arr, String target) {
        return WordChecker.containsWord(arr, target);
    }

}
