package snake_game;

import java.awt.Color;

/**
 * Visual themes. Each theme defines the colors used for the board
 * background, grid accent, obstacles, text and power-ups. The snake
 * and apple sprites (icons) stay the same across themes to keep things
 * simple, but everything drawn with plain shapes changes.
 */
public enum Theme {

    CLASSIC(
            "Classic",
            new Color(0, 0, 0),
            new Color(20, 20, 20),
            new Color(120, 120, 120),
            Color.WHITE,
            new Color(255, 215, 0)
    ),
    DARK(
            "Dark Mode",
            new Color(18, 18, 24),
            new Color(30, 30, 38),
            new Color(90, 90, 110),
            new Color(220, 220, 230),
            new Color(0, 200, 255)
    ),
    NEON(
            "Neon",
            new Color(5, 5, 15),
            new Color(15, 15, 35),
            new Color(255, 0, 200),
            new Color(0, 255, 170),
            new Color(255, 255, 0)
    ),
    RETRO(
            "Retro",
            new Color(35, 40, 20),
            new Color(50, 58, 28),
            new Color(120, 110, 60),
            new Color(200, 220, 150),
            new Color(255, 140, 0)
    );

    public final String displayName;
    public final Color background;
    public final Color gridAccent;
    public final Color obstacleColor;
    public final Color textColor;
    public final Color powerUpColor;

    Theme(String displayName, Color background, Color gridAccent,
          Color obstacleColor, Color textColor, Color powerUpColor) {
        this.displayName = displayName;
        this.background = background;
        this.gridAccent = gridAccent;
        this.obstacleColor = obstacleColor;
        this.textColor = textColor;
        this.powerUpColor = powerUpColor;
    }

    @Override
    public String toString() {
        return displayName;
    }
}