package snake_game;

/**
 * Types of power-ups that can spawn on the board.
 */
public enum Poweruptype {

    SCORE_BONUS("+50 pts", 50),
    SPEED_BOOST("Speed Boost", 0),
    SLOW_MOTION("Slow-Mo", 0),
    SHIELD("Shield", 0);

    public final String label;
    public final int bonusPoints;

    Poweruptype(String label, int bonusPoints) {
        this.label = label;
        this.bonusPoints = bonusPoints;
    }
}