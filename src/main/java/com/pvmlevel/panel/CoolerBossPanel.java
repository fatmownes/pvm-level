package com.pvmlevel.panel;

import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.pvmlevel.BossPanel.getKcLabel;

public class CoolerBossPanel extends PluginPanel {

    CoolerBossPanel(HiscoreSkill hiscoreSkill, Integer kc)
    {
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        String bossName = (hiscoreSkill != null) ? StringUtils.capitalize(hiscoreSkill.getName().toLowerCase()) : "None";

        JLabel boss = new JLabel(bossName);
        boss.setFont(FontManager.getRunescapeBoldFont());
        boss.setForeground(Color.GRAY);
        JLabel kcLabel = getKcLabel(kc);

        boss.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        layout.setVerticalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(boss)
                        .addComponent(kcLabel)
                )

        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(boss)
                        .addComponent(kcLabel)
                )
        );

    }

}
