/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Entry point for the Boggle application.
 *              Lets the user choose between the Swing GUI and text mode.
 */
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Choose how to play Boggle:");
        System.out.println("1. GUI version");
        System.out.println("2. Text version");
        System.out.print("Enter 1 or 2: ");

        String choice = input.nextLine().trim();
        if (choice.equals("1")) {
            launchGUI();
        } else {
            runTextVersion();
        }
    }

    private static void launchGUI() {
        BoggleGUI gui = new BoggleGUI();
        gui.setVisible(true);
    }

    private static void runTextVersion() {
        System.out.println("\nText version");
        System.out.println("1. Player vs Player");
        System.out.println("2. Player vs AI");
        System.out.println("3. Multi-player");
        System.out.println("4. Multi-player + AI");
        System.out.println("5. AI vs AI");

        int phase = askInt("Choose a phase: ", 1, 5);
        int rounds = askInt("Rounds: ", 1, 20);
        int target = askInt("Point target (0 = off): ", 0, 500);
        int minLength = askInt("Minimum word length: ", 2, 8);
        String dictionaryPath = askString("Dictionary file (press Enter for wordlist.txt): ");
        if (dictionaryPath.equals("")) {
            dictionaryPath = "wordlist.txt";
        }

        GameSession game;

        if (phase == 1) {
            String p1 = askName("Player 1 name: ", "Player 1");
            String p2 = askName("Player 2 name: ", "Player 2");
            game = new Phase1PlayerVsPlayer(p1, p2, rounds, target, minLength, dictionaryPath);

        } else if (phase == 2) {
            String player = askName("Your name: ", "Player 1");
            int difficulty = askDifficulty("AI difficulty");
            boolean playerFirst = askYesNo("Should the player go first? (y/n): ");
            game = new Phase2PlayerVsAI(player, difficulty, playerFirst, rounds, target, minLength, dictionaryPath);

        } else if (phase == 3) {
            int count = askInt("Number of players: ", 2, 6);
            String[] names = new String[count];
            for (int i = 0; i < count; i++) {
                names[i] = askName("Player " + (i + 1) + " name: ", "Player " + (i + 1));

            }
            game = new Phase3MultiPlayer(names, rounds, target, minLength, dictionaryPath);

        } else if (phase == 4) {
            int count = askInt("Number of human players: ", 1, 5);
            String[] names = new String[count];
            for (int i = 0; i < count; i++) {
                names[i] = askName("Player " + (i + 1) + " name: ", "Player " + (i + 1));
            }
            int difficulty = askDifficulty("AI difficulty");
            int aiPosition = askInt("AI position (1 = first): ", 1, count + 1) - 1;
            game = new Phase4MultiPlayerAI(names, difficulty, aiPosition, rounds, target, minLength, dictionaryPath);
        } else {
            int ai1 = askDifficulty("AI 1 difficulty");
            int ai2 = askDifficulty("AI 2 difficulty");
            boolean ai1First = new Random().nextBoolean();
            String boardFile = askString("Board file (press Enter for random): ");
            if (boardFile.equals("")) {
                boardFile = null;
            }
            game = new Phase5AIvsAI(ai1, ai2, ai1First, rounds, target, minLength, dictionaryPath, boardFile);
        }

        playTextGame(game);
    }

    private static void playTextGame(GameSession game) {
        while (game.isGameOver() == false && game.shouldGameEndNow() == false) {
            System.out.println("\nRound " + game.getCurrentRound() + " of " + game.getTotalRounds());
            System.out.println(game.getBoggleBoard());

            while (game.isRoundOver() == false && game.shouldGameEndNow() == false) {
                Player current = game.getCurrentPlayer();
                printScores(game);
                System.out.println("Current player: " + current.getName());

                if (current.isAI()) {
                    String move = game.getAIMove();
                    if (move == null) {
                        game.playerQuitsGame();
                        System.out.println(current.getName() + " quits because no words were found.");
                    } else {
                        game.submitWord(move);
                        System.out.println(current.getName() + " plays " + move + ".");
                    }
                } else {
                    System.out.print("Enter word, PASS, QUIT, or SHAKE: ");
                    String move = input.nextLine().trim();
                    handleHumanMove(game, move);
                }
                System.out.println();
            }

            System.out.println("Round over. Winner: " + game.getRoundWinner().getName());
            if (game.getCurrentRound() < game.getTotalRounds() && game.shouldGameEndNow() == false) {
                game.nextRound();
            } else {
                break;
            }
        }

        printScores(game);
        System.out.println("Game over. Winner: " + game.getGameWinner().getName());
    }

    private static void handleHumanMove(GameSession game, String move) {
        String moveUpper = move.toUpperCase();

        if (moveUpper.equals("PASS")) {
            game.pass();
            System.out.println("Passed.");
        } else if (moveUpper.equals("QUIT")) {
            game.playerQuitsGame();
            System.out.println("You quit the game.");
        } else if (moveUpper.equals("SHAKE")) {
            game.shakeUpBoard();
            System.out.println(game.getBoggleBoard());
        } else {
            String result = game.submitWord(move);
            if (result.equals(GameSession.RESULT_VALID)) {
                System.out.println("Accepted.");
            } else if (result.equals(GameSession.RESULT_TOO_SHORT)) {
                System.out.println("Too short.");
            } else if (result.equals(GameSession.RESULT_ALREADY_USED)) {
                System.out.println("Already used.");
            } else if (result.equals(GameSession.RESULT_NOT_ON_BOARD)) {
                System.out.println("Not on board.");
            } else {
                System.out.println("Not in dictionary.");
            }
        }
    }

    private static void printScores(GameSession game) {
        System.out.println("Scores:");
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; i++) {
            System.out.println(players[i].getName() + ": " + players[i].getTotalScore());
        }
    }

    private static String askString(String question) {
        System.out.print(question);
        return input.nextLine().trim();
    }

    private static String askName(String question, String defaultName) {
        String name = askString(question);
        if (name.equals("")) {
            return defaultName;
        } else {
            return name;
        }
    }

    private static int askInt(String question, int min, int max) {
        int number;

        System.out.print(question);
        number = input.nextInt();
        input.nextLine();

        while (number < min || number > max) {
            System.out.println("Enter a number from " + min + " to " + max + ".");
            System.out.print(question);
            number = input.nextInt();
            input.nextLine();
        }

        return number;
    }

    private static boolean askYesNo(String question) {
        String answer = askString(question);
        answer = answer.toUpperCase();

        if (answer.equals("Y")) {
            return true;
        } else if (answer.equals("YES")) {
            return true;
        } else {
            return false;
        }
    }

    private static int askDifficulty(String question) {
        System.out.println(question + ":");
        System.out.println("1. Beginner");
        System.out.println("2. Medium");
        System.out.println("3. Smart");
        int choice = askInt("Choose difficulty: ", 1, 3);

        if (choice == 1) {
            return AIPlayer.BEGINNER;
        } else if (choice == 2) {
            return AIPlayer.MEDIUM;
        } else {
            return AIPlayer.SMART;
        }
    }
}
