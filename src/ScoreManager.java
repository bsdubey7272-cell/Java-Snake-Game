package snake_game;

import java.io.*;

public class ScoreManager {

    private static final String FILE_NAME = "highscore.dat";

    public static int getHighScore() {

        try {

            File file = new File(FILE_NAME);

            if (!file.exists()) {
                return 0;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            int score = Integer.parseInt(br.readLine());
            br.close();

            return score;

        } catch (Exception e) {
            return 0;
        }
    }

    public static void saveHighScore(int score) {

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME));
            bw.write(String.valueOf(score));
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}