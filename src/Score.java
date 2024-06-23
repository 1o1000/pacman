import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Score implements Serializable {
    public String name = "";
    public int score = 0;

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    static void Write(Score s) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Scores.ser", true)) {};
        oos.writeObject(s);
        oos.close();
    }

    static List<Score> Load() throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(
                    "Scores.ser");
            List<Score> scores = new ArrayList<>();
            Score s;
            while(fis.available() != 0){
                ObjectInputStream ois = new ObjectInputStream(fis);
                s = (Score)ois.readObject();
                scores.add(s);
            }
            return scores;
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
