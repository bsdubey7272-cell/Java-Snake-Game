package snake_game;

import javax.swing.*;
import java.awt.*;

public class Snake_Game extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardHolder;

    private Mainmenupanel mainMenuPanel;
    private Board board;

    private static final String CARD_MENU = "menu";
    private static final String CARD_SETTINGS = "settings";
    private static final String CARD_LEADERBOARD = "leaderboard";
    private static final String CARD_GAME = "game";

    Snake_Game() {
        super("Snake Game");

        cardLayout = new CardLayout();
        cardHolder = new JPanel(cardLayout);

        mainMenuPanel = new Mainmenupanel(this);
        cardHolder.add(mainMenuPanel, CARD_MENU);
        cardHolder.add(new Settingspanel(this), CARD_SETTINGS);
        cardHolder.add(new Leaderboardpanel(this), CARD_LEADERBOARD);

        add(cardHolder);
        pack();

        setLocation(700, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showMenu();
    }

    public void showMenu() {
        mainMenuPanel.refreshResumeAvailability();
        cardLayout.show(cardHolder, CARD_MENU);
        cardHolder.revalidate();
    }

    public void showSettings() {
        // Rebuild so it always reflects the latest saved settings
        for (Component c : cardHolder.getComponents()) {
            if (c instanceof Settingspanel) {
                cardHolder.remove(c);
                break;
            }
        }
        cardHolder.add(new Settingspanel(this), CARD_SETTINGS);
        cardLayout.show(cardHolder, CARD_SETTINGS);
    }

    public void showLeaderboard() {
        for (Component c : cardHolder.getComponents()) {
            if (c instanceof Leaderboardpanel) {
                cardHolder.remove(c);
                break;
            }
        }
        cardHolder.add(new Leaderboardpanel(this), CARD_LEADERBOARD);
        cardLayout.show(cardHolder, CARD_LEADERBOARD);
    }

    public void startNewGame() {
        Savegamemanager.deleteSave();
        board = new Board(this, null);
        cardHolder.add(board, CARD_GAME);
        cardLayout.show(cardHolder, CARD_GAME);
        board.requestFocusInWindow();
    }

    public void resumeGame() {
        Gamestate state = Savegamemanager.load();
        if (state == null) {
            showMenu();
            return;
        }
        board = new Board(this, state);
        cardHolder.add(board, CARD_GAME);
        cardLayout.show(cardHolder, CARD_GAME);
        board.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Snake_Game().setVisible(true));
    }
}