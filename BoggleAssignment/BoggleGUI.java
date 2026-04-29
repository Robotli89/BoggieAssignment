package BoggleAssignment;

/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Main GUI window for the Boggle game.
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class BoggleGUI extends JFrame {

    private static final String CARD_MENU  = "MENU";
    private static final String CARD_SETUP = "SETUP";
    private static final String CARD_GAME  = "GAME";

    private CardLayout cardLayout;
    private JPanel     mainContainer;

    private GameSession currentSession;
    private int         selectedPhase;
    private String      dictionaryPath = "wordlist.txt";

    private javax.swing.Timer countdownTimer;
    private javax.swing.Timer aiMoveTimer;
    private int               timeLeft;

    private JLabel     setupTitleLabel;
    private JPanel     setupFieldsPanel;
    private JTextField[] nameFields;
    private JSpinner   numPlayersSpinner;
    private JComboBox<String>  ai1DiffCombo, ai2DiffCombo;
    private JComboBox<String>  whoFirstCombo;
    private JSpinner   aiPositionSpinner;
    private JTextField boardFileField;
    private JSpinner   roundsSpinner, pointTargetSpinner, minLenSpinner;

    private JLabel      phaseTitleLabel;
    private JLabel      roundLabel;
    private BoardPanel  boardPanel;
    private JLabel      currentPlayerLabel;
    private JLabel      timerLabel;
    private DefaultTableModel scoreTableModel;
    private JTextArea   wordHistoryArea;
    private JTextField  wordInput;
    private JButton     submitButton, passButton, shakeButton, quitButton;
    private JLabel      statusLabel;

    public BoggleGUI() {
        setTitle("Boggle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));

        cardLayout     = new CardLayout();
        mainContainer  = new JPanel(cardLayout);

        mainContainer.add(buildMainMenuPanel(), CARD_MENU);
        mainContainer.add(buildSetupPanel(),    CARD_SETUP);
        mainContainer.add(buildGamePanel(),     CARD_GAME);

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
    }

    // =================================================================
    //  MAIN MENU PANEL
    // =================================================================
    private JPanel buildMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("BOGGLE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));

        String[] labels = {
            "Phase 1 - Player vs Player",
            "Phase 2 - Player vs AI",
            "Phase 3 - Multi-player",
            "Phase 4 - Multi-player + AI",
            "Phase 5 - AI vs AI Contest",
            "Quit"
        };

        for (int i = 0; i < labels.length; i++) {
            final int phase = i + 1;
            JButton btn = new JButton(labels[i]);
            if (i < 5) {
                btn.addActionListener(e -> showSetup(phase));
            } else {
                btn.addActionListener(e -> System.exit(0));
            }
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    // =================================================================
    //  SETUP PANEL
    // =================================================================
    private JPanel buildSetupPanel() {
        JPanel outer = new JPanel(new BorderLayout());

        setupTitleLabel = new JLabel("", SwingConstants.CENTER);
        setupTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        outer.add(setupTitleLabel, BorderLayout.NORTH);

        setupFieldsPanel = new JPanel();
        setupFieldsPanel.setLayout(new BoxLayout(setupFieldsPanel, BoxLayout.Y_AXIS));
        outer.add(new JScrollPane(setupFieldsPanel), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout());

        JButton backBtn  = new JButton("Back");
        JButton startBtn = new JButton("Start Game");
        backBtn.addActionListener (e -> cardLayout.show(mainContainer, CARD_MENU));
        startBtn.addActionListener(e -> startGame());
        btnRow.add(backBtn);
        btnRow.add(startBtn);
        outer.add(btnRow, BorderLayout.SOUTH);

        return outer;
    }

    private void showSetup(int phase) {
        selectedPhase = phase;
        setupFieldsPanel.removeAll();

        String[] phaseNames = {
            "", "Phase 1 - Player vs Player",
                "Phase 2 - Player vs AI",
                "Phase 3 - Multi-player",
                "Phase 4 - Multi-player + AI",
                "Phase 5 - AI vs AI Contest"
        };
        setupTitleLabel.setText(phaseNames[phase]);

        switch (phase) {
            case 1: buildSetupPhase1(); break;
            case 2: buildSetupPhase2(); break;
            case 3: buildSetupPhase3(); break;
            case 4: buildSetupPhase4(); break;
            case 5: buildSetupPhase5(); break;
        }

        setupFieldsPanel.add(new JLabel("-- Game Options --"));
        roundsSpinner      = addSpinnerRow("Number of Rounds:", 3, 1, 20);
        pointTargetSpinner = addSpinnerRow("Point Target (0 = off):", 50, 0, 500);
        minLenSpinner      = addSpinnerRow("Minimum Word Length:", 3, 2, 8);

        JPanel dictRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dictRow.add(new JLabel("Dictionary File:"));
        JTextField dictField = new JTextField(dictionaryPath, 20);
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                dictionaryPath = fc.getSelectedFile().getAbsolutePath();
                dictField.setText(dictionaryPath);
            }
        });
        dictRow.add(dictField);
        dictRow.add(browseBtn);
        setupFieldsPanel.add(dictRow);

        setupFieldsPanel.revalidate();
        setupFieldsPanel.repaint();
        cardLayout.show(mainContainer, CARD_SETUP);
    }

    private void buildSetupPhase1() {
        setupFieldsPanel.add(new JLabel("-- Player Names --"));
        nameFields    = new JTextField[2];
        nameFields[0] = addTextFieldRow("Player 1 Name:", "Player 1");
        nameFields[1] = addTextFieldRow("Player 2 Name:", "Player 2");
    }

    private void buildSetupPhase2() {
        setupFieldsPanel.add(new JLabel("-- Player Setup --"));
        nameFields    = new JTextField[1];
        nameFields[0] = addTextFieldRow("Your Name:", "Player 1");

        setupFieldsPanel.add(new JLabel("-- AI Settings --"));
        ai1DiffCombo = addComboRow("AI Difficulty:",
                new String[]{"BEGINNER", "MEDIUM", "SMART"});
        whoFirstCombo = addComboRow("Who Goes First:",
                new String[]{"Player", "AI"});
    }

    private void buildSetupPhase3() {
        setupFieldsPanel.add(new JLabel("-- Players --"));
        numPlayersSpinner = addSpinnerRow("Number of Players:", 3, 2, 6);
        numPlayersSpinner.addChangeListener(e -> refreshPhase3Names());

        nameFields = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            nameFields[i] = addTextFieldRow("Player " + (i + 1) + " Name:", "Player " + (i + 1));
        }
        refreshPhase3Names();
    }

    private void refreshPhase3Names() {
        int count = (Integer) numPlayersSpinner.getValue();
        for (int i = 0; i < 6; i++) {
            if (nameFields[i] != null) {
                nameFields[i].getParent().setVisible(i < count);
            }
        }
        setupFieldsPanel.revalidate();
    }

    private void buildSetupPhase4() {
        setupFieldsPanel.add(new JLabel("-- Human Players --"));
        numPlayersSpinner = addSpinnerRow("Number of Human Players:", 2, 1, 5);

        nameFields = new JTextField[5];
        for (int i = 0; i < 5; i++) {
            nameFields[i] = addTextFieldRow("Player " + (i + 1) + " Name:", "Player " + (i + 1));
        }

        setupFieldsPanel.add(new JLabel("-- AI Settings --"));
        ai1DiffCombo    = addComboRow("AI Difficulty:", new String[]{"BEGINNER", "MEDIUM", "SMART"});
        aiPositionSpinner = addSpinnerRow("AI Turn Position (1 = first):", 1, 1, 6);
    }

    private void buildSetupPhase5() {
        setupFieldsPanel.add(new JLabel("-- AI Competitors --"));
        ai1DiffCombo = addComboRow("AI-1 Difficulty:", new String[]{"BEGINNER", "MEDIUM", "SMART"});
        ai2DiffCombo = addComboRow("AI-2 Difficulty:", new String[]{"BEGINNER", "MEDIUM", "SMART"});
        whoFirstCombo = addComboRow("Who Goes First:", new String[]{"AI-1", "AI-2", "Coin Toss"});

        setupFieldsPanel.add(new JLabel("-- Contest Board (optional) --"));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel("Board File:"));
        boardFileField = new JTextField("board.txt", 18);
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                boardFileField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        row.add(boardFileField);
        row.add(browse);
        setupFieldsPanel.add(row);
    }

    // =================================================================
    //  Start game
    // =================================================================
    private void startGame() {
        int rounds      = (Integer) roundsSpinner.getValue();
        int pointTarget = (Integer) pointTargetSpinner.getValue();
        int minLen      = (Integer) minLenSpinner.getValue();

        try {
            switch (selectedPhase) {
                case 1:
                    currentSession = new Phase1PlayerVsPlayer(
                        emptyFallbackName(nameFields[0].getText().trim(), "Player 1"),
                        emptyFallbackName(nameFields[1].getText().trim(), "Player 2"),
                        rounds, pointTarget, minLen, dictionaryPath);
                    break;

                case 2: {
                    int diff = getSelectedDifficulty(ai1DiffCombo);
                    boolean playerFirst = whoFirstCombo.getSelectedIndex() == 0;
                    currentSession = new Phase2PlayerVsAI(
                        emptyFallbackName(nameFields[0].getText().trim(), "Player 1"),
                        diff, playerFirst,
                        rounds, pointTarget, minLen, dictionaryPath);
                    break;
                }

                case 3: {
                    int n = (Integer) numPlayersSpinner.getValue();
                    String[] names = new String[n];
                    for (int i = 0; i < n; i++)
                        names[i] = emptyFallbackName(nameFields[i].getText().trim(), "Player " + (i + 1));
                    currentSession = new Phase3MultiPlayer(
                        names, rounds, pointTarget, minLen, dictionaryPath);
                    break;
                }

                case 4: {
                    int n = (Integer) numPlayersSpinner.getValue();
                    String[] names = new String[n];
                    for (int i = 0; i < n; i++)
                        names[i] = emptyFallbackName(nameFields[i].getText().trim(), "Player " + (i + 1));
                    int diff = getSelectedDifficulty(ai1DiffCombo);
                    int aiPos = (Integer) aiPositionSpinner.getValue() - 1;
                    currentSession = new Phase4MultiPlayerAI(
                        names, diff, aiPos,
                        rounds, pointTarget, minLen, dictionaryPath);
                    break;
                }

                case 5: {
                    int d1 = getSelectedDifficulty(ai1DiffCombo);
                    int d2 = getSelectedDifficulty(ai2DiffCombo);
                    int firstIdx = whoFirstCombo.getSelectedIndex();
                    boolean ai1First = firstIdx == 0
                            || (firstIdx == 2 && new Random().nextBoolean());
                    String boardFile = boardFileField.getText().trim();
                    currentSession = new Phase5AIvsAI(
                        d1, d2, ai1First,
                        rounds, pointTarget, minLen, dictionaryPath,
                        getBlankAsNull(boardFile));
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error starting game: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        initGamePanel();
        cardLayout.show(mainContainer, CARD_GAME);
        nextTurn();
    }

    // =================================================================
    //  GAME PANEL
    // =================================================================
    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // top
        JPanel north = new JPanel(new BorderLayout());
        phaseTitleLabel = new JLabel("Boggle");
        phaseTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        roundLabel = new JLabel("Round 1 of ?");
        north.add(phaseTitleLabel, BorderLayout.WEST);
        north.add(roundLabel,      BorderLayout.EAST);
        panel.add(north, BorderLayout.NORTH);

        // center
        JPanel center = new JPanel(new BorderLayout());

        boardPanel = new BoardPanel(BoggleBoard.SIZE);
        center.add(boardPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());

        // current player and timer
        JPanel playerInfo = new JPanel(new GridLayout(2, 2));
        playerInfo.setBorder(BorderFactory.createTitledBorder("Turn Info"));
        playerInfo.add(new JLabel("Current Player:"));
        playerInfo.add(new JLabel("Time Left:"));
        currentPlayerLabel = new JLabel("---");
        timerLabel = new JLabel("15");
        playerInfo.add(currentPlayerLabel);
        playerInfo.add(timerLabel);
        rightPanel.add(playerInfo, BorderLayout.NORTH);

        // score table
        String[] cols = {"Player", "Round", "Total"};
        scoreTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable scoreTable = new JTable(scoreTableModel);
        JScrollPane scorePane = new JScrollPane(scoreTable);
        scorePane.setPreferredSize(new Dimension(260, 120));
        rightPanel.add(scorePane, BorderLayout.CENTER);

        // word history
        JPanel historyWrapper = new JPanel(new BorderLayout());
        historyWrapper.add(new JLabel("Words This Round:"), BorderLayout.NORTH);
        wordHistoryArea = new JTextArea(6, 22);
        wordHistoryArea.setEditable(false);
        JScrollPane histPane = new JScrollPane(wordHistoryArea);
        historyWrapper.add(histPane, BorderLayout.CENTER);
        rightPanel.add(historyWrapper, BorderLayout.SOUTH);

        center.add(rightPanel, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        // bottom
        JPanel south = new JPanel(new BorderLayout());

        JPanel inputRow = new JPanel(new FlowLayout());
        wordInput = new JTextField(16);
        wordInput.addActionListener(e -> onSubmit());

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> onSubmit());

        passButton = new JButton("Pass");
        passButton.addActionListener(e -> onPass());

        shakeButton = new JButton("Shake Board");
        shakeButton.addActionListener(e -> onShakeBoard());

        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> onQuitGame());

        inputRow.add(new JLabel("Word:"));
        inputRow.add(wordInput);
        inputRow.add(submitButton);
        inputRow.add(passButton);
        inputRow.add(shakeButton);
        inputRow.add(quitButton);
        south.add(inputRow, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(760, 580));
        return panel;
    }

    private void initGamePanel() {
        String[] phaseNames = {
            "", "Phase 1: Player vs Player",
                "Phase 2: Player vs AI",
                "Phase 3: Multi-player",
                "Phase 4: Multi-player + AI",
                "Phase 5: AI vs AI"
        };
        phaseTitleLabel.setText(phaseNames[selectedPhase]);
        updateRoundLabel();
        boardPanel.updateBoard(currentSession.getBoggleBoard().getBoard());
        rebuildScoreTable();
        wordHistoryArea.setText("");
        statusLabel.setText("Game started! Good luck.");
    }

    // =================================================================
    //  GAME LOOP
    // =================================================================
    private void nextTurn() {
        if (currentSession.isRoundOver()) { endRound(); return; }

        Player current = currentSession.getCurrentPlayer();
        if (current.isAI()) {
            currentPlayerLabel.setText(current.getName() + " (AI)");
        } else {
            currentPlayerLabel.setText(current.getName());
        }

        stopTimers();

        if (current.isAI()) {
            setInputEnabled(false);
            statusLabel.setText(current.getName() + " is thinking...");
            timerLabel.setText("---");
            aiMoveTimer = new javax.swing.Timer(1500, e -> executeAIMove());
            aiMoveTimer.setRepeats(false);
            aiMoveTimer.start();
        } else {
            setInputEnabled(true);
            wordInput.setText("");
            wordInput.requestFocus();
            statusLabel.setText("Your turn, " + current.getName() + "! Enter a word.");
            timeLeft = 15;
            timerLabel.setText(String.valueOf(timeLeft));
            countdownTimer = new javax.swing.Timer(1000, e -> tickTimer());
            countdownTimer.start();
        }
    }

    private void tickTimer() {
        timeLeft--;
        timerLabel.setText(String.valueOf(timeLeft));
        if (timeLeft <= 5) {
            timerLabel.setForeground(Color.RED);
        } else {
            timerLabel.setForeground(Color.BLACK);
        }
        if (timeLeft <= 0) {
            countdownTimer.stop();
            statusLabel.setText("Time's up! Auto-passing for " +
                    currentSession.getCurrentPlayer().getName() + ".");
            currentSession.pass();
            afterTurn();
        }
    }

    private void executeAIMove() {
        String move = currentSession.getAIMove();
        String playerName = currentSession.getCurrentPlayer().getName();

        if (move != null) {
            String result = currentSession.submitWord(move);
            if (GameSession.RESULT_VALID.equals(result)) {
                statusLabel.setText(playerName + " played \"" + move
                        + "\" for " + move.length() + " points!");
                appendWordHistory(playerName, move);
            } else {
                currentSession.pass();
                statusLabel.setText(playerName + " passed.");
            }
        } else {
            currentSession.playerQuitsGame();
            statusLabel.setText(playerName + " quits because no words were found.");
        }

        rebuildScoreTable();
        afterTurn();
    }

    private void onSubmit() {
        String word = wordInput.getText().trim().toUpperCase();
        if (word.length() == 0) {
            return;
        }

        stopTimers();
        String playerName = currentSession.getCurrentPlayer().getName();
        String result     = currentSession.submitWord(word);

        switch (result) {
            case GameSession.RESULT_VALID:
                statusLabel.setText("\"" + word + "\" accepted! +"
                        + word.length() + " points for " + playerName + ".");
                appendWordHistory(playerName, word);
                break;
            case GameSession.RESULT_TOO_SHORT:
                statusLabel.setText("\"" + word + "\" is too short (min "
                        + currentSession.getMinWordLength() + " letters).");
                restartTimerForSameTurn();
                return;
            case GameSession.RESULT_ALREADY_USED:
                statusLabel.setText("\"" + word + "\" was already played this round.");
                restartTimerForSameTurn();
                return;
            case GameSession.RESULT_NOT_ON_BOARD:
                statusLabel.setText("\"" + word + "\" cannot be formed on the board.");
                restartTimerForSameTurn();
                return;
            case GameSession.RESULT_NOT_IN_DICT:
                statusLabel.setText("\"" + word + "\" is not a valid English word.");
                restartTimerForSameTurn();
                return;
        }

        wordInput.setText("");
        rebuildScoreTable();
        afterTurn();
    }

    private void onPass() {
        stopTimers();
        String playerName = currentSession.getCurrentPlayer().getName();
        currentSession.pass();
        statusLabel.setText(playerName + " passed.");
        rebuildScoreTable();
        afterTurn();
    }

    private void onQuitGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Quit the game? You will be removed from play.",
                "Quit Game", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            stopTimers();
            String playerName = currentSession.getCurrentPlayer().getName();
            currentSession.playerQuitsGame();
            statusLabel.setText(playerName + " quit the game.");
            rebuildScoreTable();
            afterTurn();
        }
    }

    private void onShakeBoard() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Shake up the board? All words played this round will be cleared.",
                "Shake Board", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            stopTimers();
            currentSession.shakeUpBoard();
            boardPanel.updateBoard(currentSession.getBoggleBoard().getBoard());
            wordHistoryArea.setText("");
            statusLabel.setText("Board shaken! New board generated.");
            nextTurn();
        }
    }

    private void afterTurn() {
        updateRoundLabel();
        boardPanel.clearHighlights();

        if (currentSession.shouldGameEndNow()) {
            endGame();
            return;
        }
        if (currentSession.isRoundOver()) {
            endRound();
        } else {
            nextTurn();
        }
    }

    private void restartTimerForSameTurn() {
        timeLeft = 15;
        timerLabel.setText(String.valueOf(timeLeft));
        timerLabel.setForeground(Color.BLACK);
        countdownTimer = new javax.swing.Timer(1000, e -> tickTimer());
        countdownTimer.start();
        wordInput.selectAll();
        wordInput.requestFocus();
    }

    // =================================================================
    //  ROUND / GAME END
    // =================================================================
    private void endRound() {
        stopTimers();
        setInputEnabled(false);

        Player winner = currentSession.getRoundWinner();
        String message = "Round " + currentSession.getCurrentRound() + " over!\n\nScores this round:\n";
        Player[] players = currentSession.getPlayers();
        for (int i = 0; i < players.length; i++) {
            message = message + "  " + players[i].getName() + ": "
                    + players[i].getRoundScore() + " pts\n";
        }
        message = message + "\nRound winner: " + winner.getName() + "!";

        if (currentSession.isGameOver()) {
            endGame();
        } else {
            int choice = JOptionPane.showOptionDialog(this,
                    message, "Round Over",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new String[]{"Next Round", "End Game"}, "Next Round");

            if (choice == JOptionPane.YES_OPTION) {
                currentSession.nextRound();
                boardPanel.updateBoard(currentSession.getBoggleBoard().getBoard());
                wordHistoryArea.setText("");
                updateRoundLabel();
                rebuildScoreTable();
                statusLabel.setText("Round " + currentSession.getCurrentRound() + " started!");
                nextTurn();
            } else {
                endGame();
            }
        }
    }

    private void endGame() {
        stopTimers();
        setInputEnabled(false);

        Player winner = currentSession.getGameWinner();
        String message = "Game Over!\n\nFinal Scores:\n";
        Player[] players = currentSession.getPlayers();
        for (int i = 0; i < players.length; i++) {
            message = message + "  " + players[i].getName() + ": "
                    + players[i].getTotalScore() + " pts\n";
        }
        message = message + "\nWinner: " + winner.getName() + "!";

        int choice = JOptionPane.showOptionDialog(this,
                message, "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Play Again", "Main Menu"}, "Main Menu");

        if (choice == JOptionPane.YES_OPTION) {
            showSetup(selectedPhase);
        } else {
            cardLayout.show(mainContainer, CARD_MENU);
        }
    }

    // =================================================================
    //  UI HELPERS
    // =================================================================

    private void updateRoundLabel() {
        roundLabel.setText("Round " + currentSession.getCurrentRound()
                + " of " + currentSession.getTotalRounds());
    }

    private void rebuildScoreTable() {
        scoreTableModel.setRowCount(0);
        Player[] players = currentSession.getPlayers();
        for (int i = 0; i < players.length; i++) {
            scoreTableModel.addRow(new Object[]{
                players[i].getName(), players[i].getRoundScore(), players[i].getTotalScore()
            });
        }
    }

    private void appendWordHistory(String player, String word) {
        wordHistoryArea.append(player + ": " + word
                + " (+" + word.length() + ")\n");
        wordHistoryArea.setCaretPosition(wordHistoryArea.getDocument().getLength());
    }

    private void setInputEnabled(boolean enabled) {
        wordInput.setEnabled(enabled);
        submitButton.setEnabled(enabled);
        passButton.setEnabled(enabled);
        quitButton.setEnabled(enabled);
    }

    private void stopTimers() {
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        if (aiMoveTimer    != null && aiMoveTimer.isRunning())    aiMoveTimer.stop();
    }

    // setup helpers
    private JTextField addTextFieldRow(String labelText, String defaultValue) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(labelText));
        JTextField field = new JTextField(defaultValue, 16);
        row.add(field);
        setupFieldsPanel.add(row);
        return field;
    }

    private JSpinner addSpinnerRow(String labelText, int value, int min, int max) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(labelText));
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        row.add(spinner);
        setupFieldsPanel.add(row);
        return spinner;
    }

    private JComboBox<String> addComboRow(String labelText, String[] items) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(labelText));
        JComboBox<String> combo = new JComboBox<>(items);
        row.add(combo);
        setupFieldsPanel.add(row);
        return combo;
    }

    private static String emptyFallbackName(String name, String fallback) {
        if (name == null) {
            return fallback;
        } else if (name.length() == 0) {
            return fallback;
        } else {
            return name;
        }
    }

    private String getBlankAsNull(String text) {
        if (text.length() == 0) {
            return null;
        } else {
            return text;
        }
    }

    private int getSelectedDifficulty(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if ("BEGINNER".equals(selected)) {
            return AIPlayer.BEGINNER;
        } else if ("MEDIUM".equals(selected)) {
            return AIPlayer.MEDIUM;
        } else {
            return AIPlayer.SMART;
        }
    }

}
