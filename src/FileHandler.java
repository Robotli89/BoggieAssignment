/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Handles all file I/O for the Boggle game:
 *              - Writing a timestamped game log to logs/boggle_<timestamp>.log
 *              - Saving a game snapshot that can be reloaded later
 *              - (Future) loading a saved game snapshot
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------

    private static final String LOG_DIR  = "logs" + File.separator;
    private static final String SAVE_DIR = "saves" + File.separator;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    private PrintWriter logWriter;
    private String      logFilename;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Opens a new timestamped log file in the logs/ directory.
     */
    public FileHandler() {
        new File(LOG_DIR).mkdirs();
        new File(SAVE_DIR).mkdirs();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        logFilename = LOG_DIR + "boggle_" + timestamp + ".log";

        try {
            logWriter = new PrintWriter(new FileWriter(logFilename, true));
        } catch (IOException e) {
            System.err.println("Warning: Could not create log file — " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Logging helpers
    // ---------------------------------------------------------------

    /**
     * Logs the start of a new game.
     * @param phase       phase name
     * @param playerNames all player names
     * @param rounds      number of rounds
     */
    public void logGameStart(String phase, String[] playerNames, int rounds) {
        log("============================================================");
        log("GAME START: " + phase);
        log("Players  : " + String.join(", ", playerNames));
        log("Rounds   : " + rounds);
        log("Date/Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        log("============================================================");
    }

    /**
     * Logs the start of a new round.
     * @param round round number
     */
    public void logNewRound(int round) {
        log("");
        log("--- Round " + round + " ---");
    }

    /**
     * Logs a successfully played word.
     * @param round  round number
     * @param player player's name
     * @param word   word played
     * @param points points awarded
     */
    public void logWordPlayed(int round, String player, String word, int points) {
        log(String.format("  %-15s played  %-15s  +%d pts", player, word, points));
    }

    /**
     * Logs a player pass.
     * @param round  round number
     * @param player player's name
     */
    public void logPass(int round, String player) {
        log(String.format("  %-15s PASSED", player));
    }

    /**
     * Logs a free-form event (e.g., board shake-up).
     * @param event description of the event
     */
    public void logEvent(String event) {
        log("[EVENT] " + event);
    }

    // ---------------------------------------------------------------
    // Save / Load
    // ---------------------------------------------------------------

    /**
     * Saves a snapshot of the current game state to a file in saves/.
     * Format is human-readable key:value lines.
     *
     * @param session  the session to save
     * @param filename file name (without path); e.g., "mysave.bog"
     */
    public void saveGame(GameSession session, String filename) {
        String path = SAVE_DIR + filename;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("ROUND:"         + session.getCurrentRound());
            pw.println("TOTAL_ROUNDS:"  + session.getTotalRounds());
            pw.println("POINT_TARGET:"  + session.getPointTarget());
            pw.println("MIN_WORD_LEN:"  + session.getMinWordLength());

            // Board state (row by row)
            char[][] board = session.getBoggleBoard().getBoard();
            for (int r = 0; r < board.length; r++) {
                StringBuilder row = new StringBuilder("BOARD_ROW:");
                for (int c = 0; c < board[r].length; c++) {
                    if (c > 0) row.append(' ');
                    row.append(board[r][c]);
                }
                pw.println(row);
            }

            // Player scores
            for (Player p : session.getPlayers()) {
                pw.println("PLAYER:" + p.getName() + ":" + p.getTotalScore()
                        + ":" + p.getRoundScore());
            }

            // Words used this round
            for (String word : session.getUsedWordsThisRound()) {
                pw.println("USED_WORD:" + word);
            }

            pw.flush();
            log("Game saved to: " + path);
            System.out.println("Game saved to: " + path);

        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Returns true if the saves/ directory contains at least one .bog file.
     * @return true if there are saved games
     */
    public boolean hasSavedGames() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) return false;
        File[] saves = dir.listFiles((d, name) -> name.endsWith(".bog"));
        return saves != null && saves.length > 0;
    }

    /**
     * Returns the names of all saved game files.
     * @return array of filenames (without directory path), possibly empty
     */
    public String[] getSavedGameFiles() {
        File   dir   = new File(SAVE_DIR);
        File[] saves = dir.listFiles((d, name) -> name.endsWith(".bog"));
        if (saves == null) return new String[0];
        String[] names = new String[saves.length];
        for (int i = 0; i < saves.length; i++) names[i] = saves[i].getName();
        return names;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    /** @return the path to the current log file */
    public String getLogFilename() { return logFilename; }

    /** Flushes and closes the log writer. */
    public void close() {
        if (logWriter != null) {
            logWriter.flush();
            logWriter.close();
        }
    }

    // ---------------------------------------------------------------
    // Private helper
    // ---------------------------------------------------------------

    private void log(String message) {
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
}
