package BoggleAssignment;

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

    /** Creates an AI-vs-AI contest game. */
    public Phase5AIvsAI(int ai1Difficulty,
                        int ai2Difficulty,
                        boolean ai1GoesFirst,
                        int totalRounds, int pointTarget,
                        int minWordLength, String dictionaryPath,
                        String boardFilePath) {
        setUpGameSession(totalRounds, pointTarget, minWordLength, dictionaryPath);

        // Load a fixed board when provided.
        if (boardFilePath != null && boardFilePath.length() > 0) {
            loadBoardFromFile(boardFilePath);
        }

        // Build the two AI players.
        AIPlayer ai1 = new AIPlayer("AI-1 (" + AIPlayer.getDifficultyName(ai1Difficulty) + ")", ai1Difficulty, dictionary);
        AIPlayer ai2 = new AIPlayer("AI-2 (" + AIPlayer.getDifficultyName(ai2Difficulty) + ")", ai2Difficulty, dictionary);

        players    = new Player[2];
        if (ai1GoesFirst) {
            players[0] = ai1;
            players[1] = ai2;
        } else {
            players[0] = ai2;
            players[1] = ai1;
        }
    }

    // ---------------------------------------------------------------
    // Board file loading
    // ---------------------------------------------------------------

    /** Loads a 5x5 board from a text file. */
    private void loadBoardFromFile(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            char[][] newBoard = new char[BoggleBoard.SIZE][BoggleBoard.SIZE];
            boolean validBoard = true;

            for (int row = 0; row < BoggleBoard.SIZE; row++) {
                String   line    = br.readLine();
                if (line == null) {
                    validBoard = false;
                } else {
                    String[] letters = line.trim().toUpperCase().split(" ");

                    if (letters.length < BoggleBoard.SIZE) {
                        validBoard = false;
                    } else {
                        for (int col = 0; col < BoggleBoard.SIZE; col++) {
                            newBoard[row][col] = letters[col].charAt(0);
                        }
                    }
                }
            }

            br.close();

            if (validBoard) {
                boggleBoard.setBoard(newBoard);
                wordChecker.setBoard(newBoard);
                System.out.println("Phase 5 board loaded from: " + filePath);
            } else {
                System.out.println("Board file format was wrong. Using random board instead.");
            }

        } catch (IOException e) {
            System.err.println("Warning: Could not load board file \"" + filePath
                    + "\": " + e.getMessage() + " - using random board instead.");
        }
    }
}
