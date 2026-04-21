/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Phase 5 — AI vs AI Contest Mode.
 *              Two AI players compete against each other.
 *              For the official contest, the board is loaded from a text file
 *              ("board.txt") so both competing teams use the identical board.
 *              A coin toss determines who goes first.
 *
 *              Board file format (5 lines, 5 space-separated uppercase letters):
 *                A B C D E
 *                F G H I J
 *                K L M N O
 *                P Q R S T
 *                U V W X Y
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Phase5AIvsAI extends GameSession {

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a Phase 5 (AI vs AI) game session.
     *
     * @param ai1Difficulty  difficulty of the first AI
     * @param ai2Difficulty  difficulty of the second AI
     * @param ai1GoesFirst   result of the coin toss (true = AI-1 goes first)
     * @param totalRounds    how many rounds to play
     * @param pointTarget    first to reach this score wins (0 = off)
     * @param minWordLength  minimum word length
     * @param dictionaryPath path to wordlist.txt
     * @param boardFilePath  path to the contest board file, or null for random
     */
    public Phase5AIvsAI(AIPlayer.Difficulty ai1Difficulty,
                        AIPlayer.Difficulty ai2Difficulty,
                        boolean ai1GoesFirst,
                        int totalRounds, int pointTarget,
                        int minWordLength, String dictionaryPath,
                        String boardFilePath) {
        super(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Load contest board from file if provided
        if (boardFilePath != null && boardFilePath.isEmpty() == false) {
            loadBoardFromFile(boardFilePath);
        }

        // Create two AI players
        AIPlayer ai1 = new AIPlayer("AI-1 (" + ai1Difficulty + ")", ai1Difficulty, dictionary);
        AIPlayer ai2 = new AIPlayer("AI-2 (" + ai2Difficulty + ")", ai2Difficulty, dictionary);

        players    = new Player[2];
        players[0] = ai1GoesFirst ? ai1 : ai2;
        players[1] = ai1GoesFirst ? ai2 : ai1;
    }

    // ---------------------------------------------------------------
    // Board file loading
    // ---------------------------------------------------------------

    /**
     * Loads a 5x5 board from a text file.
     * Each row is one line; letters are separated by whitespace.
     * Falls back to the randomly generated board if the file cannot be read.
     *
     * @param filePath path to the board file
     */
    private void loadBoardFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            char[][] newBoard = new char[BoggleBoard.SIZE][BoggleBoard.SIZE];

            for (int row = 0; row < BoggleBoard.SIZE; row++) {
                String   line    = br.readLine();
                if (line == null) throw new IOException("Board file has fewer than 5 rows.");
                String[] letters = line.trim().toUpperCase().split("\\s+");

                for (int col = 0; col < BoggleBoard.SIZE; col++) {
                    newBoard[row][col] = letters[col].charAt(0);
                }
            }

            boggleBoard.setBoard(newBoard);
            wordValidator.setBoard(newBoard);
            System.out.println("Phase 5 board loaded from: " + filePath);

        } catch (IOException e) {
            System.err.println("Warning: Could not load board file \"" + filePath
                    + "\": " + e.getMessage() + " — using random board instead.");
        }
    }
}
