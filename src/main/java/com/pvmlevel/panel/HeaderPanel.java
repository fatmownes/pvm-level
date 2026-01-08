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
    public JLabel levelLabel;
    public JLabel totalKcLabel;

    public HeaderPanel() {
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        nameLabel = new JLabel(NO_PLAYER_SELECTED);
        nameLabel.setForeground(Color.YELLOW);
        nameLabel.setFont(FontManager.getRunescapeFont());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new JLabel(NO_PLAYER_SELECTED_LEVEL);
        levelLabel.setForeground(Color.RED);
        levelLabel.setFont(FontManager.getRunescapeFont());
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalKcLabel = new JLabel(NO_PLAYER_SELECTED_KC);
        totalKcLabel.setForeground(Color.RED);
        totalKcLabel.setFont(FontManager.getRunescapeFont());
        totalKcLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(nameLabel);
        add(levelLabel);

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addComponent(levelLabel)
                        .addComponent(totalKcLabel)
                )

        );

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameLabel)
                        .addComponent(levelLabel)
                        .addComponent(totalKcLabel)
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
        );

    }

}
