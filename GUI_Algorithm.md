# Algorithm for Boggle GUI

## Global Variables

- cardLayout stores the screen changer for Menu, Setup, and Game screens
- mainContainer stores all GUI screens
- currentSession stores the active Boggle game session
- selectedPhase stores the selected game mode from Phase 1 to Phase 5
- dictionaryPath stores the dictionary file location
- countdownTimer controls the 15 second human turn timer
- aiMoveTimer controls the delay before an AI move
- timeLeft stores the remaining seconds for the current human turn
- setup input fields store player names, AI difficulty, number of rounds, point target, minimum word length, and optional board file
- game screen components store the board, current player label, timer label, score table, word history, word input, buttons, and status message

## Start Main

- Create the Boggle GUI window
- Set the title, close operation, size behaviour, and background
- Create a CardLayout
- Create the main container that uses the CardLayout
- Call buildMainMenuPanel()
- Call buildSetupPanel()
- Call buildGamePanel()
- Add all three panels to the main container
- Add the main container to the window
- Pack the window
- Center the window on the screen
- Display the window

## Pseudocode for buildMainMenuPanel()

**Purpose:** Create the first screen where the user chooses which phase to play.

- Create a main menu panel using BorderLayout
- Add a large title that says BOGGLE at the top
- Create a button panel with six rows
- Add buttons for:
  - Phase 1 Player vs Player
  - Phase 2 Player vs AI
  - Phase 3 Multi-player
  - Phase 4 Multi-player + AI
  - Phase 5 AI vs AI Contest
  - Quit
- For each phase button:
  - Store the phase number
  - When clicked, call showSetup(phase number)
- For the Quit button:
  - When clicked, close the program
- Add a footer label
- Return the completed menu panel

## Pseudocode for showSetup(phase)

**Parameter:** phase is the game mode selected by the user.

**Purpose:** Display the correct setup fields for the selected phase.

- Store phase in selectedPhase
- Clear all old setup fields
- Set the setup title based on the selected phase
- If phase = 1:
  - Call buildSetupPhase1()
- Else if phase = 2:
  - Call buildSetupPhase2()
- Else if phase = 3:
  - Call buildSetupPhase3()
- Else if phase = 4:
  - Call buildSetupPhase4()
- Else if phase = 5:
  - Call buildSetupPhase5()
- Add shared game options:
  - Number of rounds
  - Point target
  - Minimum word length
- Add dictionary file field and Browse button
- If Browse is clicked:
  - Open a file chooser
  - Store the selected file path in dictionaryPath
  - Display the selected path in the text field
- Refresh the setup panel
- Show the Setup screen

## Pseudocode for buildSetupPhase1()

**Purpose:** Build setup fields for Player vs Player.

- Add section label: Player Names
- Create name field for Player 1
- Create name field for Player 2

## Pseudocode for buildSetupPhase2()

**Purpose:** Build setup fields for Player vs AI.

- Add section label: Player Setup
- Create name field for the human player
- Add section label: AI Settings
- Create dropdown for AI difficulty
- Create dropdown for who goes first: Player or AI

## Pseudocode for buildSetupPhase3()

**Purpose:** Build setup fields for multi-player mode.

- Add section label: Players
- Create spinner for number of players from 2 to 6
- Create six possible name fields
- When the number of players changes:
  - Call refreshPhase3Names()
- Call refreshPhase3Names()

## Pseudocode for refreshPhase3Names()

**Purpose:** Only show the number of name fields needed.

- Get the number of players from the spinner
- For each possible player field from 1 to 6:
  - If the field number is less than or equal to the number of players:
    - Show the field
  - Else:
    - Hide the field
- Refresh the setup panel

## Pseudocode for buildSetupPhase4()

**Purpose:** Build setup fields for multi-player plus AI mode.

- Add section label: Human Players
- Create spinner for number of human players from 1 to 5
- Create five possible human player name fields
- Add section label: AI Settings
- Create dropdown for AI difficulty
- Create spinner for AI turn position

## Pseudocode for buildSetupPhase5()

**Purpose:** Build setup fields for AI vs AI contest.

- Add section label: AI Competitors
- Create dropdown for AI 1 difficulty
- Create dropdown for AI 2 difficulty
- Create dropdown for who goes first: AI 1, AI 2, or Coin Toss
- Add section label: Contest Board
- Create optional board file field
- Add Browse button
- If Browse is clicked:
  - Open a file chooser
  - Store the selected board file path in the board file field

## Pseudocode for startGame()

**Purpose:** Read setup values, create the correct game session, and begin the game.

- Get number of rounds from the rounds spinner
- Get point target from the point target spinner
- Get minimum word length from the minimum length spinner
- Try to create the correct session:
  - If selectedPhase = 1:
    - Get Player 1 name
    - Get Player 2 name
    - If a name is blank, use a default name
    - Create Phase1PlayerVsPlayer session
  - Else if selectedPhase = 2:
    - Get human player name
    - Get AI difficulty
    - Get who goes first
    - Create Phase2PlayerVsAI session
  - Else if selectedPhase = 3:
    - Get number of players
    - Store each player name in an array
    - Create Phase3MultiPlayer session
  - Else if selectedPhase = 4:
    - Get number of human players
    - Store each human player name in an array
    - Get AI difficulty
    - Get AI turn position
    - Create Phase4MultiPlayerAI session
  - Else if selectedPhase = 5:
    - Get both AI difficulties
    - Get who goes first
    - If Coin Toss is selected, randomly choose first AI
    - Get optional board file
    - Create Phase5AIvsAI session
- If an error happens:
  - Show an error message
  - Stop the method
- Call initGamePanel()
- Show the Game screen
- Call nextTurn()

## Pseudocode for buildGamePanel()

**Purpose:** Create the main game screen.

- Create the outer game panel using BorderLayout
- Create top row:
  - Add phase title on the left
  - Add round label on the right
- Create center row:
  - Add the 5 by 5 Boggle board on the left
  - Add right side panel for player information, score table, and word history
- Create player information area:
  - Show current player
  - Show time left
- Create score table:
  - Columns are Player, Round, and Total
  - Prevent editing table cells
- Create word history area:
  - Show words played during the current round
- Create bottom input row:
  - Add word input field
  - Add Submit button
  - Add Pass button
  - Add Shake Board button
  - Add Quit button
- If Enter is pressed in the word input:
  - Call onSubmit()
- If Submit is clicked:
  - Call onSubmit()
- If Pass is clicked:
  - Call onPass()
- If Shake Board is clicked:
  - Call onShakeBoard()
- If Quit is clicked:
  - Call onQuitGame()
- Add status label at the bottom
- Return the completed game panel

## Pseudocode for initGamePanel()

**Purpose:** Reset the game screen for a new session.

- Set the phase title
- Call updateRoundLabel()
- Show the current board on the board panel
- Call rebuildScoreTable()
- Clear word history
- Set status message to show the game has started

## Pseudocode for nextTurn()

**Purpose:** Prepare the GUI for the next player or AI turn.

- If the round is over:
  - Call endRound()
  - Stop the method
- Get the current player from currentSession
- Display the current player name
- If the current player is an AI:
  - Add AI beside the player name
- Stop all active timers
- If the current player is an AI:
  - Disable word input and human buttons
  - Display that the AI is thinking
  - Set timer label to a dash
  - Start aiMoveTimer for a short delay
  - When the delay ends, call executeAIMove()
- Else:
  - Enable word input and human buttons
  - Clear the word input field
  - Focus the word input field
  - Display a message telling the player to enter a word
  - Set timeLeft to 15
  - Display timeLeft
  - Start countdownTimer
  - Every second, call tickTimer()

## Pseudocode for tickTimer()

**Purpose:** Count down the human player's turn.

- Decrease timeLeft by 1
- Display timeLeft
- If timeLeft is 5 or less:
  - Make timer text red
- Else:
  - Make timer text black
- If timeLeft is 0 or less:
  - Stop countdownTimer
  - Display time up message
  - Pass the current player's turn
  - Call afterTurn()

## Pseudocode for executeAIMove()

**Purpose:** Let the AI choose and play a move.

- Get the AI move from currentSession
- Get the current AI player name
- If the AI returns a word:
  - Submit the word to currentSession
  - If the result is valid:
    - Display accepted word message
    - Add the word to word history
  - Else:
    - Pass the AI turn
    - Display pass message
- Else:
  - Mark the AI as quit
  - Display that no words were found
- Rebuild the score table
- Call afterTurn()

## Pseudocode for onSubmit()

**Purpose:** Handle a human player's word submission.

- Get the word from the input field
- Trim spaces
- Convert the word to uppercase
- If the word is empty:
  - Stop the method
- Stop all timers
- Get the current player name
- Submit the word to currentSession
- If result = VALID:
  - Display accepted message
  - Add points equal to word length
  - Add word to word history
  - Clear the input field
  - Rebuild the score table
  - Call afterTurn()
- Else if result = TOO_SHORT:
  - Display too short message
  - Call restartTimerForSameTurn()
- Else if result = ALREADY_USED:
  - Display already used message
  - Call restartTimerForSameTurn()
- Else if result = NOT_ON_BOARD:
  - Display cannot be formed message
  - Call restartTimerForSameTurn()
- Else if result = NOT_IN_DICT:
  - Display invalid English word message
  - Call restartTimerForSameTurn()

## Pseudocode for onPass()

**Purpose:** Let the current human player pass.

- Stop all timers
- Get current player name
- Pass the current player in currentSession
- Display pass message
- Rebuild the score table
- Call afterTurn()

## Pseudocode for onQuitGame()

**Purpose:** Remove the current human player from the game after confirmation.

- Ask the user to confirm quitting
- If the user chooses Yes:
  - Stop all timers
  - Get current player name
  - Mark the current player as quit in currentSession
  - Display quit message
  - Rebuild the score table
  - Call afterTurn()

## Pseudocode for onShakeBoard()

**Purpose:** Generate a new board during the round after confirmation.

- Ask the user to confirm shaking the board
- If the user chooses Yes:
  - Stop all timers
  - Call currentSession.shakeUpBoard()
  - Display the new board
  - Clear word history
  - Display board shaken message
  - Call nextTurn()

## Pseudocode for afterTurn()

**Purpose:** Decide what happens after a turn ends.

- Update the round label
- Clear any highlighted board cells
- If currentSession says the game should end now:
  - Call endGame()
  - Stop the method
- If currentSession says the round is over:
  - Call endRound()
- Else:
  - Call nextTurn()

## Pseudocode for restartTimerForSameTurn()

**Purpose:** Give the same player another chance after an invalid word.

- Set timeLeft back to 15
- Display timeLeft
- Set timer text to black
- Start countdownTimer again
- Select the word input text
- Focus the word input field

## Pseudocode for endRound()

**Purpose:** Show round results and either continue or end the game.

- Stop all timers
- Disable input
- Get the round winner
- Build a message showing:
  - Current round number
  - Each player's round score
  - Round winner
- If the game is over:
  - Call endGame()
- Else:
  - Ask the user to choose Next Round or End Game
  - If the user chooses Next Round:
    - Call currentSession.nextRound()
    - Display the new board
    - Clear word history
    - Update round label
    - Rebuild score table
    - Display new round message
    - Call nextTurn()
  - Else:
    - Call endGame()

## Pseudocode for endGame()

**Purpose:** Show final results and return to setup or menu.

- Stop all timers
- Disable input
- Get the game winner
- Build a message showing:
  - Final score for each player
  - Winner
- Ask the user to choose Play Again or Main Menu
- If the user chooses Play Again:
  - Call showSetup(selectedPhase)
- Else:
  - Show the Main Menu screen

## Pseudocode for updateRoundLabel()

**Purpose:** Show the current round number.

- Get current round from currentSession
- Get total rounds from currentSession
- Display: Round currentRound of totalRounds

## Pseudocode for rebuildScoreTable()

**Purpose:** Refresh the score table after a turn or round change.

- Clear all rows from the score table
- Get all players from currentSession
- For each player:
  - Add a row containing:
    - Player name
    - Round score
    - Total score

## Pseudocode for appendWordHistory(player, word)

**Parameters:** player is the player name, word is the accepted word.

**Purpose:** Show accepted words for the current round.

- Add a new line to the word history area
- Display player name, word, and points earned
- Move the text area view to the newest line

## Pseudocode for setInputEnabled(enabled)

**Parameter:** enabled is true or false.

**Purpose:** Turn human controls on or off.

- Set word input enabled status to enabled
- Set Submit button enabled status to enabled
- Set Pass button enabled status to enabled
- Set Quit button enabled status to enabled

## Pseudocode for stopTimers()

**Purpose:** Stop any active GUI timers.

- If countdownTimer exists and is running:
  - Stop countdownTimer
- If aiMoveTimer exists and is running:
  - Stop aiMoveTimer

## Pseudocode for BoardPanel

**Purpose:** Display and update the 5 by 5 Boggle letter grid.

### Constructor BoardPanel(boardSize)

- Store boardSize
- Create a 2D array of labels
- Set the layout to a grid with boardSize rows and boardSize columns
- Add spacing and border
- Call initializeCells()

### initializeCells()

- For each row in the board:
  - For each column in the board:
    - Create a label with ?
    - Center the text
    - Set font, colour, background, border, and size
    - Store the label in cells[row][col]
    - Add the label to the panel

### updateBoard(board)

- For each row in the board:
  - For each column in the board:
    - Set the matching label text to the board letter
    - Reset the cell background colour
- Repaint the board panel

### highlightCells(highlights)

- For each row in the board:
  - For each column in the board:
    - If highlights[row][col] is true:
      - Set the cell background to yellow
    - Else:
      - Set the cell background to white
- Repaint the board panel

### clearHighlights()

- For each row in the board:
  - For each column in the board:
    - Set the cell background to white
- Repaint the board panel
