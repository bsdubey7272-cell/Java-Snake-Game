package snake_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board extends JPanel implements ActionListener {

    private Image apple;
    private Image dot;
    private Image head;

    private final int ALL_DOTS = 900;
    private final int DOT_SIZE = 10;
    private final int RANDOM_POSITION = 29;
    private final int OBSTACLE_COUNT = 8;

    private int apple_x;
    private int apple_y;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;
    private boolean paused = false;

    private int dots;
    private int score;
    private int highScore;

    private Timer timer;
    private int baseDelay;

    private final Snake_Game parent;

    // Obstacles
    private int[] obstacleX = new int[0];
    private int[] obstacleY = new int[0];

    // Power-ups
    private boolean powerUpActive = false;
    private Poweruptype powerUpType;
    private int powerUp_x;
    private int powerUp_y;

    // Shield effect
    private boolean shieldActive = false;
    private Timer shieldTimer;
    private Timer speedRevertTimer;

    // Particle animation on eat / power-up
    private final List<Particle> particles = new ArrayList<>();

    // Game over blink animation
    private Timer blinkTimer;
    private boolean blinkOn = false;

    private boolean leaderboardPrompted = false;

    private static class Particle {
        double x, y, dx, dy;
        int life;
        Color color;

        Particle(double x, double y, double dx, double dy, int life, Color color) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.life = life;
            this.color = color;
        }
    }

    Board(Snake_Game parent, Gamestate resumeState) {

        this.parent = parent;

        addKeyListener(new TAdapter());

        setPreferredSize(new Dimension(300, 300));
        setFocusable(true);

        loadImages();

        blinkTimer = new Timer(500, e -> {
            blinkOn = !blinkOn;
            repaint();
        });

        if (resumeState != null) {
            loadFromState(resumeState);
        } else {
            initGame();
        }
    }

    public void loadImages() {

        ImageIcon i1 = new ImageIcon(getClass().getResource("/snake_game/icons/apple.png"));
        apple = i1.getImage();

        ImageIcon i2 = new ImageIcon(getClass().getResource("/snake_game/icons/dot.png"));
        dot = i2.getImage();

        ImageIcon i3 = new ImageIcon(getClass().getResource("/snake_game/icons/head.png"));
        head = i3.getImage();
    }

    public void initGame() {

        dots = 3;
        score = 0;

        highScore = ScoreManager.getHighScore();

        for (int i = 0; i < dots; i++) {
            y[i] = 50;
            x[i] = 50 - i * DOT_SIZE;
        }

        generateObstacles();
        locateApple();

        powerUpActive = false;
        shieldActive = false;
        particles.clear();
        leaderboardPrompted = false;

        setBackground(Gamesettings.getTheme().background);

        baseDelay = Gamesettings.getDifficulty().timerDelayMs;
        timer = new Timer(baseDelay, this);
        timer.start();

        SoundManager.startBackgroundMusic();
    }

    /**
     * Restores an in-progress game exactly as it was saved.
     */
    private void loadFromState(Gamestate s) {

        dots = s.dots;
        score = s.score;
        highScore = ScoreManager.getHighScore();

        for (int i = 0; i < dots; i++) {
            x[i] = s.x[i];
            y[i] = s.y[i];
        }

        apple_x = s.apple_x;
        apple_y = s.apple_y;

        leftDirection = s.leftDirection;
        rightDirection = s.rightDirection;
        upDirection = s.upDirection;
        downDirection = s.downDirection;

        obstacleX = s.obstacleX != null ? s.obstacleX : new int[0];
        obstacleY = s.obstacleY != null ? s.obstacleY : new int[0];

        powerUpActive = s.powerUpActive;
        powerUpType = s.powerUpType;
        powerUp_x = s.powerUp_x;
        powerUp_y = s.powerUp_y;

        shieldActive = false;
        particles.clear();
        leaderboardPrompted = false;
        inGame = true;
        paused = false;

        setBackground(Gamesettings.getTheme().background);

        baseDelay = Gamesettings.getDifficulty().timerDelayMs;
        timer = new Timer(baseDelay, this);
        timer.start();

        SoundManager.startBackgroundMusic();
    }

    private Gamestate buildGameState() {

        Gamestate s = new Gamestate();
        s.dots = dots;
        s.score = score;
        s.x = Arrays.copyOf(x, dots);
        s.y = Arrays.copyOf(y, dots);
        s.apple_x = apple_x;
        s.apple_y = apple_y;
        s.leftDirection = leftDirection;
        s.rightDirection = rightDirection;
        s.upDirection = upDirection;
        s.downDirection = downDirection;
        s.obstaclesEnabled = Gamesettings.isObstaclesEnabled();
        s.obstacleX = obstacleX;
        s.obstacleY = obstacleY;
        s.powerUpActive = powerUpActive;
        s.powerUpType = powerUpType;
        s.powerUp_x = powerUp_x;
        s.powerUp_y = powerUp_y;
        return s;
    }

    private void saveAndExitToMenu() {

        Savegamemanager.save(buildGameState());

        if (timer != null) timer.stop();
        if (shieldTimer != null) shieldTimer.stop();
        if (speedRevertTimer != null) speedRevertTimer.stop();

        SoundManager.stopBackgroundMusic();
        parent.showMenu();
    }

    private void exitToMenuWithoutSaving() {

        if (timer != null) timer.stop();
        if (shieldTimer != null) shieldTimer.stop();
        if (speedRevertTimer != null) speedRevertTimer.stop();

        SoundManager.stopBackgroundMusic();
        parent.showMenu();
    }

    /**
     * Resets all game state and starts a brand new round.
     * Called when the player presses ENTER on the Game Over screen.
     */
    public void restartGame() {

        inGame = true;
        paused = false;

        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;

        if (blinkTimer != null) blinkTimer.stop();
        blinkOn = false;

        initGame();
        repaint();
        requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Obstacles
    // ------------------------------------------------------------------

    private void generateObstacles() {

        if (!Gamesettings.isObstaclesEnabled()) {
            obstacleX = new int[0];
            obstacleY = new int[0];
            return;
        }

        obstacleX = new int[OBSTACLE_COUNT];
        obstacleY = new int[OBSTACLE_COUNT];

        int placed = 0;
        int attempts = 0;

        while (placed < OBSTACLE_COUNT && attempts < 500) {
            attempts++;

            int ox = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;
            int oy = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;

            // Keep the snake's starting row/area clear
            if (oy == 50 && ox <= 120) continue;

            boolean duplicate = false;
            for (int k = 0; k < placed; k++) {
                if (obstacleX[k] == ox && obstacleY[k] == oy) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) continue;

            obstacleX[placed] = ox;
            obstacleY[placed] = oy;
            placed++;
        }

        if (placed < OBSTACLE_COUNT) {
            obstacleX = Arrays.copyOf(obstacleX, placed);
            obstacleY = Arrays.copyOf(obstacleY, placed);
        }
    }

    private boolean isObstacleAt(int px, int py) {
        for (int k = 0; k < obstacleX.length; k++) {
            if (obstacleX[k] == px && obstacleY[k] == py) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Apple / power-up placement
    // ------------------------------------------------------------------

    public void locateApple() {

        int r, px, py;
        int attempts = 0;

        do {
            r = (int) (Math.random() * RANDOM_POSITION);
            px = r * DOT_SIZE;
            r = (int) (Math.random() * RANDOM_POSITION);
            py = r * DOT_SIZE;
            attempts++;
        } while (isObstacleAt(px, py) && attempts < 200);

        apple_x = px;
        apple_y = py;
    }

    private void maybeSpawnPowerUp() {

        if (!Gamesettings.isPowerUpsEnabled() || powerUpActive) {
            return;
        }

        if (Math.random() >= 0.35) {
            return;
        }

        int px, py;
        int attempts = 0;
        boolean valid;

        do {
            px = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;
            py = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;
            attempts++;

            valid = !isObstacleAt(px, py) && !(px == apple_x && py == apple_y);
            for (int i = 0; i < dots && valid; i++) {
                if (x[i] == px && y[i] == py) valid = false;
            }
        } while (!valid && attempts < 200);

        powerUp_x = px;
        powerUp_y = py;

        Poweruptype[] types = Poweruptype.values();
        powerUpType = types[(int) (Math.random() * types.length)];
        powerUpActive = true;
    }

    // ------------------------------------------------------------------
    // Particles (simple burst animation)
    // ------------------------------------------------------------------

    private void spawnParticles(int cx, int cy, Color color) {
        for (int i = 0; i < 8; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double speed = 1 + Math.random() * 2;
            particles.add(new Particle(cx, cy, Math.cos(angle) * speed, Math.sin(angle) * speed, 14, color));
        }
    }

    private void updateParticles() {
        List<Particle> toRemove = new ArrayList<>();
        for (Particle p : particles) {
            p.x += p.dx;
            p.y += p.dy;
            p.life--;
            if (p.life <= 0) toRemove.add(p);
        }
        particles.removeAll(toRemove);
    }

    private void drawParticles(Graphics g) {
        for (Particle p : particles) {
            int alpha = Math.max(0, Math.min(255, p.life * 18));
            g.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), alpha));
            g.fillOval((int) p.x, (int) p.y, 4, 4);
        }
    }

    // ------------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        Theme theme = Gamesettings.getTheme();

        if (inGame) {

            // Obstacles
            g.setColor(theme.obstacleColor);
            for (int k = 0; k < obstacleX.length; k++) {
                g.fillRect(obstacleX[k], obstacleY[k], DOT_SIZE, DOT_SIZE);
            }

            // Apple
            g.drawImage(apple, apple_x, apple_y, this);

            // Power-up
            if (powerUpActive) {
                drawPowerUp(g, theme);
            }

            // Snake
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], this);
                } else {
                    g.drawImage(dot, x[i], y[i], this);
                }
            }

            // Particles
            drawParticles(g);

            // Score
            g.setColor(theme.textColor);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score : " + score, 10, 20);

            if (shieldActive) {
                g.setColor(theme.powerUpColor);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("SHIELD", 230, 20);
            }

            // Control hint
            g.setColor(theme.textColor);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            g.drawString("P: Pause   F5: Save & Exit   ESC: Menu", 5, 296);

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g, theme);
        }
    }

    private void drawPowerUp(Graphics g, Theme theme) {

        g.setColor(theme.powerUpColor);
        g.fillOval(powerUp_x, powerUp_y, DOT_SIZE, DOT_SIZE);

        String label;
        switch (powerUpType) {
            case SCORE_BONUS: label = "$"; break;
            case SPEED_BOOST: label = ">"; break;
            case SLOW_MOTION: label = "~"; break;
            case SHIELD: label = "S"; break;
            default: label = "?";
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString(label, powerUp_x + 2, powerUp_y + 9);
    }

    public void gameOver(Graphics g, Theme theme) {

        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font scoreFont = new Font("Arial", Font.PLAIN, 18);
        Font promptFont = new Font("Arial", Font.PLAIN, 13);

        String title = "GAME OVER";
        String scoreText = "Final Score : " + score;
        String highText = "High Score : " + highScore;
        String promptText = "Press ENTER to Play Again";
        String menuText = "Press M for Menu";

        FontMetrics titleMetrics = getFontMetrics(titleFont);
        FontMetrics scoreMetrics = getFontMetrics(scoreFont);
        FontMetrics promptMetrics = getFontMetrics(promptFont);

        g.setColor(Color.RED);
        g.setFont(titleFont);
        g.drawString(title,
                (getWidth() - titleMetrics.stringWidth(title)) / 2,
                getHeight() / 2 - 30);

        g.setColor(theme.textColor);
        g.setFont(scoreFont);

        g.drawString(scoreText,
                (getWidth() - scoreMetrics.stringWidth(scoreText)) / 2,
                getHeight() / 2 + 10);

        g.drawString(highText,
                (getWidth() - scoreMetrics.stringWidth(highText)) / 2,
                getHeight() / 2 + 35);

        g.setFont(promptFont);

        if (blinkOn) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(promptText,
                    (getWidth() - promptMetrics.stringWidth(promptText)) / 2,
                    getHeight() / 2 + 65);
        }

        g.setColor(Color.GRAY);
        g.drawString(menuText,
                (getWidth() - promptMetrics.stringWidth(menuText)) / 2,
                getHeight() / 2 + 85);
    }

    // ------------------------------------------------------------------
    // Movement / game logic
    // ------------------------------------------------------------------

    public void move() {

        for (int i = dots - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection) y[0] -= DOT_SIZE;
        if (downDirection) y[0] += DOT_SIZE;
    }

    public void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            score += 10;

            SoundManager.playEatSound();
            spawnParticles(apple_x + 5, apple_y + 5, Color.ORANGE);

            if (score > highScore) {
                highScore = score;
                ScoreManager.saveHighScore(highScore);
            }

            locateApple();
            maybeSpawnPowerUp();
        }
    }

    public void checkPowerUp() {

        if (!powerUpActive) return;

        if (x[0] == powerUp_x && y[0] == powerUp_y) {

            switch (powerUpType) {
                case SCORE_BONUS:
                    score += powerUpType.bonusPoints;
                    if (score > highScore) {
                        highScore = score;
                        ScoreManager.saveHighScore(highScore);
                    }
                    break;
                case SPEED_BOOST:
                    applyTemporarySpeed(-40);
                    break;
                case SLOW_MOTION:
                    applyTemporarySpeed(40);
                    break;
                case SHIELD:
                    activateShield();
                    break;
            }

            SoundManager.playPowerUpSound();
            spawnParticles(powerUp_x + 5, powerUp_y + 5, Gamesettings.getTheme().powerUpColor);
            powerUpActive = false;
        }
    }

    private void applyTemporarySpeed(int deltaMs) {

        int newDelay = Math.max(60, timer.getDelay() + deltaMs);
        timer.setDelay(newDelay);

        if (speedRevertTimer != null) speedRevertTimer.stop();

        speedRevertTimer = new Timer(5000, e -> {
            timer.setDelay(baseDelay);
            ((Timer) e.getSource()).stop();
        });
        speedRevertTimer.setRepeats(false);
        speedRevertTimer.start();
    }

    private void activateShield() {

        shieldActive = true;

        if (shieldTimer != null) shieldTimer.stop();

        shieldTimer = new Timer(5000, e -> {
            shieldActive = false;
            ((Timer) e.getSource()).stop();
            repaint();
        });
        shieldTimer.setRepeats(false);
        shieldTimer.start();
    }

    public void checkCollision() {

        for (int i = dots - 1; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                if (!shieldActive) {
                    inGame = false;
                }
            }
        }

        if (!shieldActive && isObstacleAt(x[0], y[0])) {
            inGame = false;
        }

        if (x[0] >= 300) inGame = false;
        if (y[0] >= 300) inGame = false;
        if (x[0] < 0) inGame = false;
        if (y[0] < 0) inGame = false;

        if (!inGame) {

            timer.stop();
            if (shieldTimer != null) shieldTimer.stop();
            if (speedRevertTimer != null) speedRevertTimer.stop();

            SoundManager.stopBackgroundMusic();
            SoundManager.playGameOverSound();

            blinkTimer.start();

            promptLeaderboardIfQualifies();

            // A completed game clears any in-progress save
            Savegamemanager.deleteSave();
        }
    }

    private void promptLeaderboardIfQualifies() {

        if (leaderboardPrompted) return;
        leaderboardPrompted = true;

        if (Leaderboard.qualifies(score)) {
            String name = JOptionPane.showInputDialog(this,
                    "New high score! Enter your name:", "Leaderboard", JOptionPane.PLAIN_MESSAGE);
            if (name != null) {
                Leaderboard.addScore(name, score);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            checkApple();
            checkPowerUp();
            checkCollision();
            move();
            updateParticles();
        }

        repaint();
    }

    public class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            // Game Over screen controls
            if (!inGame) {
                if (key == KeyEvent.VK_ENTER) {
                    restartGame();
                } else if (key == KeyEvent.VK_M) {
                    exitToMenuWithoutSaving();
                }
                return;
            }

            // Return to menu without saving
            if (key == KeyEvent.VK_ESCAPE) {
                exitToMenuWithoutSaving();
                return;
            }

            // Save current game and return to menu
            if (key == KeyEvent.VK_F5) {
                saveAndExitToMenu();
                return;
            }

            // Pause / Resume
            if (key == KeyEvent.VK_P) {

                paused = !paused;

                if (paused) {
                    timer.stop();
                } else {
                    timer.start();
                }

                repaint();
                return;
            }

            // Ignore movement while paused
            if (paused) {
                return;
            }

            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true;
                rightDirection = false;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true;
                leftDirection = false;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
                upDirection = false;
            }
        }
    }
}