package snake_game;

import java.io.*;

/**
 * Handles saving and loading a single in-progress game to disk,
 * powering the "Resume Game" menu option.
 */
public class Savegamemanager {

    private static final String FILE_NAME = "savegame.dat";

    public static boolean saveExists() {
        return new File(FILE_NAME).exists();
    }

    public static void save(Gamestate state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(state);
        } catch (IOException e) {
            // Non-critical; save just fails silently
        }
    }

    public static Gamestate load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Gamestate) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteSave() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }
}