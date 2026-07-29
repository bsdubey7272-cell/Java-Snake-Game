package snake_game;

import javax.swing.*;
import java.awt.*;

/**
 * Settings screen: theme, sound, obstacles, power-ups, difficulty.
 * Changes are applied to GameSettings and persisted on "Save".
 */
public class Settingspanel extends JPanel {

    public Settingspanel(Snake_Game parent) {

        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(300, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("SETTINGS", SwingConstants.CENTER);
        title.setForeground(Color.GREEN);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridy = 0;
        add(title, gbc);

        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(Color.WHITE);
        JComboBox<Theme> themeBox = new JComboBox<>(Theme.values());
        themeBox.setSelectedItem(Gamesettings.getTheme());

        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setForeground(Color.WHITE);
        JComboBox<Gamesettings.Difficulty> difficultyBox = new JComboBox<>(Gamesettings.Difficulty.values());
        difficultyBox.setSelectedItem(Gamesettings.getDifficulty());

        JCheckBox soundCheck = new JCheckBox("Sound Enabled", Gamesettings.isSoundEnabled());
        JCheckBox obstaclesCheck = new JCheckBox("Obstacles Enabled", Gamesettings.isObstaclesEnabled());
        JCheckBox powerUpsCheck = new JCheckBox("Power-ups Enabled", Gamesettings.isPowerUpsEnabled());

        styleCheck(soundCheck);
        styleCheck(obstaclesCheck);
        styleCheck(powerUpsCheck);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        add(themeLabel, gbc);
        gbc.gridx = 1;
        add(themeBox, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(difficultyLabel, gbc);
        gbc.gridx = 1;
        add(difficultyBox, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;

        gbc.gridy = 3;
        add(soundCheck, gbc);

        gbc.gridy = 4;
        add(obstaclesCheck, gbc);

        gbc.gridy = 5;
        add(powerUpsCheck, gbc);

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.setOpaque(false);
        buttonRow.add(saveButton);
        buttonRow.add(backButton);

        gbc.gridy = 6;
        gbc.insets = new Insets(16, 10, 6, 10);
        add(buttonRow, gbc);

        saveButton.addActionListener(e -> {
            Gamesettings.setTheme((Theme) themeBox.getSelectedItem());
            Gamesettings.setDifficulty((Gamesettings.Difficulty) difficultyBox.getSelectedItem());
            Gamesettings.setSoundEnabled(soundCheck.isSelected());
            Gamesettings.setObstaclesEnabled(obstaclesCheck.isSelected());
            Gamesettings.setPowerUpsEnabled(powerUpsCheck.isSelected());
            Gamesettings.save();
            parent.showMenu();
        });

        backButton.addActionListener(e -> parent.showMenu());
    }

    private void styleCheck(JCheckBox box) {
        box.setForeground(Color.WHITE);
        box.setOpaque(false);
    }
}