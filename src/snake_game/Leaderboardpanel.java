package snake_game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Displays the persisted top-10 leaderboard.
 */
public class Leaderboardpanel extends JPanel {

    public Leaderboardpanel(Snake_Game parent) {

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(300, 300));

        JLabel title = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        title.setForeground(Color.GREEN);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        List<Leaderboard.Entry> entries = Leaderboard.getTopScores();

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No scores yet — go set one!");
            empty.setForeground(Color.LIGHT_GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(empty);
        } else {
            int rank = 1;
            for (Leaderboard.Entry e : entries) {
                JLabel row = new JLabel(rank + ".  " + e.name + "  —  " + e.score);
                row.setForeground(Color.WHITE);
                row.setFont(new Font("Arial", Font.PLAIN, 14));
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                listPanel.add(row);
                rank++;
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.BLACK);
        add(scroll, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setOpaque(false);
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);

        backButton.addActionListener(e -> parent.showMenu());
    }
}