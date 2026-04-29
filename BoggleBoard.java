private static final String[] DICE = {
    "AAAFRS", "AEEGMU", "CEIILT", "DHHNOT", "FIPRSY",
    "AAEEEE", "AEGMNN", "CEILPT", "DHLNOR", "GORRVW",
    "AAFIRS", "AFIRSY", "CEIPST", "EIIITT", "HIPRRY",
    "ADENNN", "BJKQXZ", "DDLNOR", "EMOTTT", "NOOTUW",
    "AEEEEM", "CCNSTW", "DHHLOR", "ENSSSU", "OOOTTU"
};

public void generateBoard() {
    // Roll each die into the grid (no shuffling, dice stay in fixed positions)
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            int index = row * SIZE + col;
            String die = DICE[index];
            board[row][col] = die.charAt(random.nextInt(die.length()));
        }
    }
}

// Rolls one face from each die.