package snake_game;

import java.io.Serializable;

/**
 * A serializable snapshot of everything needed to resume a game exactly
 * where it was left off.
 */
public class Gamestate implements Serializable {

    private static final long serialVersionUID = 1L;

    public int dots;
    public int score;
    public int[] x;
    public int[] y;

    public int apple_x;
    public int apple_y;

    public boolean leftDirection;
    public boolean rightDirection;
    public boolean upDirection;
    public boolean downDirection;

    public boolean obstaclesEnabled;
    public int[] obstacleX;
    public int[] obstacleY;

    public boolean powerUpActive;
    public Poweruptype powerUpType;
    public int powerUp_x;
    public int powerUp_y;
}