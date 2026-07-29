package snake_game;

import javax.swing.*;
import java.awt.*;

/**
 * The main menu shown when the game launches: New Game, Resume, Settings,
 * Leaderboard, Exit.
 */
public class Mainmenupanel extends JPanel {

    private final JButton resumeButton;

    public Mainmenupanel(Snake_Game parent) {

        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(300, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("SNAKE", SwingConstants.CENTER);
        title.setForeground(Color.GREEN);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(title, gbc);

        JButton newGameButton = new JButton("New Game");
        resumeButton = new JButton("Resume Game");
        JButton settingsButton = new JButton("Settings");
        JButton leaderboardButton = new JButton("Leaderboard");
        JButton exitButton = new JButton("Exit");

        gbc.insets = new Insets(6, 40, 6, 40);

        gbc.gridy = 1;
        add(newGameButton, gbc);

        gbc.gridy = 2;
        add(resumeButton, gbc);

        gbc.gridy = 3;
        add(settingsButton, gbc);

        gbc.gridy = 4;
        add(leaderboardButton, gbc);

        gbc.gridy = 5;
        add(exitButton, gbc);

        newGameButton.addActionListener(e -> parent.startNewGame());
        resumeButton.addActionListener(e -> parent.resumeGame());
        settingsButton.addActionListener(e -> parent.showSettings());
        leaderboardButton.addActionListener(e -> parent.showLeaderboard());
        exitButton.addActionListener(e -> System.exit(0));

        refreshResumeAvailability();
    }

    public void refreshResumeAvailability() {
        resumeButton.setEnabled(Savegamemanager.saveExists());
    }
}