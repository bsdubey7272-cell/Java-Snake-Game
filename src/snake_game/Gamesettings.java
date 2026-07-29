package snake_game;

import java.io.*;
import java.util.Properties;

/**
 * Holds all user-configurable game settings and persists them to
 * settings.properties so they survive between runs.
 */
public class Gamesettings {

    private static final String FILE_NAME = "settings.properties";

    public enum Difficulty {
        EASY(180), MEDIUM(140), HARD(100);

        public final int timerDelayMs;

        Difficulty(int timerDelayMs) {
            this.timerDelayMs = timerDelayMs;
        }
    }

    private static Theme theme = Theme.CLASSIC;
    private static boolean soundEnabled = true;
    private static boolean obstaclesEnabled = true;
    private static boolean powerUpsEnabled = true;
    private static Difficulty difficulty = Difficulty.MEDIUM;

    static {
        load();
    }

    public static Theme getTheme() {
        return theme;
    }

    public static void setTheme(Theme t) {
        theme = t;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isObstaclesEnabled() {
        return obstaclesEnabled;
    }

    public static void setObstaclesEnabled(boolean enabled) {
        obstaclesEnabled = enabled;
    }

    public static boolean isPowerUpsEnabled() {
        return powerUpsEnabled;
    }

    public static void setPowerUpsEnabled(boolean enabled) {
        powerUpsEnabled = enabled;
    }

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(Difficulty d) {
        difficulty = d;
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("theme", theme.name());
        p.setProperty("soundEnabled", String.valueOf(soundEnabled));
        p.setProperty("obstaclesEnabled", String.valueOf(obstaclesEnabled));
        p.setProperty("powerUpsEnabled", String.valueOf(powerUpsEnabled));
        p.setProperty("difficulty", difficulty.name());

        try (FileOutputStream out = new FileOutputStream(FILE_NAME)) {
            p.store(out, "Snake Game Settings");
        } catch (IOException e) {
            // Non-critical; settings just won't persist this run
        }
    }

    public static void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            p.load(in);

            theme = Theme.valueOf(p.getProperty("theme", Theme.CLASSIC.name()));
            soundEnabled = Boolean.parseBoolean(p.getProperty("soundEnabled", "true"));
            obstaclesEnabled = Boolean.parseBoolean(p.getProperty("obstaclesEnabled", "true"));
            powerUpsEnabled = Boolean.parseBoolean(p.getProperty("powerUpsEnabled", "true"));
            difficulty = Difficulty.valueOf(p.getProperty("difficulty", Difficulty.MEDIUM.name()));

        } catch (Exception e) {
            // Corrupt or unreadable settings file; fall back to defaults
        }
    }
}