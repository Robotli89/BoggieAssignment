/**
 * Author: Kevin Li and Ethan Chuang
 * Date:   [TBD]
 * Course: ICS4U
 * Project: Boggle Game
 *
 * Description: Entry point for the Boggle application.
 *              Launches the Swing GUI on the Event Dispatch Thread.
 */
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> {
            BoggleGUI gui = new BoggleGUI();
            gui.setVisible(true);
        });
    }
}
