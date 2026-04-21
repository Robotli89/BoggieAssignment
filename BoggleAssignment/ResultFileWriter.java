package BoggleAssignment;

import java.io.FileWriter;
import java.io.PrintWriter;

public class ResultFileWriter {
    public static void appendResults(String filename, String text) {
        try {
            FileWriter fileWriter = new FileWriter(filename, true);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.write(text);
            writer.write("\n");
            writer.flush();
            writer.close();
            System.out.println("Results saved to " + filename);
        } catch (Exception e) {
            System.out.println("Could not write results file.");
        }
    }
}
