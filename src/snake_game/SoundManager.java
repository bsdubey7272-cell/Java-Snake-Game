package snake_game;

import javax.sound.sampled.*;

/**
 * Handles all game audio.
 *
 * Eat / Game Over / Power-up sounds are generated on the fly as simple
 * sine-wave beeps, so the game has working sound effects with zero extra
 * asset files.
 *
 * Background music is optional: if you drop a file at
 *   src/snake_game/sounds/background.wav
 * it will be looped automatically while playing. If the file isn't there,
 * background music is silently skipped (the game still works fine).
 *
 * All playback respects GameSettings.isSoundEnabled() so the Settings menu
 * mute toggle works without callers needing to check it themselves.
 */
public class SoundManager {

    private static Clip backgroundClip;

    // ---------------------------------------------------------------
    // Generated sound effects (no external files required)
    // ---------------------------------------------------------------

    public static void playEatSound() {
        if (!Gamesettings.isSoundEnabled()) return;
        playTone(880, 90, 0.25);
    }

    public static void playPowerUpSound() {
        if (!Gamesettings.isSoundEnabled()) return;
        new Thread(() -> {
            playToneBlocking(523, 80, 0.25);
            playToneBlocking(784, 120, 0.25);
        }).start();
    }

    public static void playGameOverSound() {
        if (!Gamesettings.isSoundEnabled()) return;
        // Two-tone descending "game over" cue
        new Thread(() -> {
            playToneBlocking(440, 150, 0.3);
            playToneBlocking(220, 300, 0.3);
        }).start();
    }

    private static void playTone(int freqHz, int durationMs, double volume) {
        new Thread(() -> playToneBlocking(freqHz, durationMs, volume)).start();
    }

    private static void playToneBlocking(int freqHz, int durationMs, double volume) {
        try {
            float sampleRate = 44100f;
            int numSamples = (int) (sampleRate * durationMs / 1000.0);
            byte[] buffer = new byte[numSamples * 2]; // 16-bit mono PCM

            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i * freqHz / sampleRate;
                short sample = (short) (Math.sin(angle) * Short.MAX_VALUE * volume);
                buffer[2 * i] = (byte) (sample & 0xFF);
                buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();

        } catch (LineUnavailableException e) {
            // Sound is not critical to gameplay; fail silently
        }
    }

    // ---------------------------------------------------------------
    // Optional looping background music (needs a .wav file supplied by you)
    // ---------------------------------------------------------------

    public static void startBackgroundMusic() {

        if (!Gamesettings.isSoundEnabled()) {
            return;
        }

        try {
            if (backgroundClip != null && backgroundClip.isRunning()) {
                return;
            }

            java.net.URL url = SoundManager.class.getResource("/snake_game/sounds/background.wav");
            if (url == null) {
                return; // no music file provided; skip quietly
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();

        } catch (Exception e) {
            // A missing/broken music file should never crash the game
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }
}