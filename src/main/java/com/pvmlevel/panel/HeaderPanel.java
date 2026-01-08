package com.pvmlevel.panel;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.pvmlevel.panel.PvMPluginPanel.*;

public class HeaderPanel extends PluginPanel {

    public JLabel nameLabel;
    public JLabel scoreLabel;
    public JLabel totalKcLabel;

    public HeaderPanel() {
        BoxLayout groupLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(groupLayout);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));


        nameLabel = new JLabel(NO_PLAYER_SELECTED);
        nameLabel.setForeground(Color.YELLOW);
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scoreLabel = new JLabel(NO_PLAYER_SELECTED_LEVEL);
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setFont(FontManager.getRunescapeFont());
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalKcLabel = new JLabel(NO_PLAYER_SELECTED_KC);
        totalKcLabel.setForeground(Color.YELLOW);
        totalKcLabel.setFont(FontManager.getRunescapeFont());
        totalKcLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(nameLabel);
        add(scoreLabel);
        add(totalKcLabel);

    }

}
