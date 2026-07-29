package snake_game;

import java.io.*;
import java.util.*;

/**
 * Manages a persisted top-10 leaderboard of named scores.
 * Stored as simple "name,score" lines in leaderboard.dat.
 */
public class Leaderboard {

    private static final String FILE_NAME = "leaderboard.dat";
    private static final int MAX_ENTRIES = 10;

    public static class Entry {
        public final String name;
        public final int score;

        public Entry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static List<Entry> getTopScores() {

        List<Entry> entries = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return entries;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                int comma = line.lastIndexOf(',');
                if (comma <= 0) continue;

                String name = line.substring(0, comma);
                int score = Integer.parseInt(line.substring(comma + 1).trim());
                entries.add(new Entry(name, score));
            }

        } catch (Exception e) {
            // Corrupt file; return whatever was parsed so far
        }

        entries.sort((a, b) -> b.score - a.score);
        return entries;
    }

    /**
     * Returns true if the given score would place on the leaderboard
     * (i.e. board isn't full yet, or the score beats the lowest entry).
     */
    public static boolean qualifies(int score) {
        List<Entry> current = getTopScores();
        if (current.size() < MAX_ENTRIES) {
            return score > 0;
        }
        return score > current.get(current.size() - 1).score;
    }

    public static void addScore(String name, int score) {

        if (name == null || name.isBlank()) {
            name = "Player";
        }
        name = name.replace(",", "").trim();

        List<Entry> entries = getTopScores();
        entries.add(new Entry(name, score));
        entries.sort((a, b) -> b.score - a.score);

        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Entry e : entries) {
                bw.write(e.name + "," + e.score);
                bw.newLine();
            }
        } catch (IOException e) {
            // Non-critical; leaderboard entry just won't persist
        }
    }
}