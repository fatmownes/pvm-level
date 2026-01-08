package com.pvmlevel.panel;

import com.pvmlevel.PlayerManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TopThreePanelParent extends PluginPanel
{

    TopThreePanelParent(SpriteManager spriteManager, PlayerManager.PlayerStat playerStat) {

        HiscoreSkill hs1;
        HiscoreSkill hs2;
        HiscoreSkill hs3;
        int kc1;
        int kc2;
        int kc3;

        if (playerStat == null || !playerStat.hasFetchedKcs() || playerStat.getSorted().size() < 3) {
            hs1 = null;
            hs2 = null;
            hs3 = null;

            kc1 = 0;
            kc2 = 0;
            kc3 = 0;
        } else {
            hs1 = playerStat.getSorted().get(0).getKey();
            hs2 = playerStat.getSorted().get(1).getKey();
            hs3 = playerStat.getSorted().get(2).getKey();

             kc1 = playerStat.getSorted().get(0).getValue();
             kc2 = playerStat.getSorted().get(1).getValue();
             kc3 = playerStat.getSorted().get(2).getValue();
        }

        setBorder(new EmptyBorder(3, 3, 3, 3));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel topPanel = new CoolerBossPanel(spriteManager, hs1, kc1);
        JPanel middlePanel = new CoolerBossPanel(spriteManager, hs2, kc2);
        JPanel bottomPanel = new CoolerBossPanel(spriteManager, hs3, kc3);

        add(topPanel);
        add(Box.createVerticalStrut(6)); // spacing
        add(middlePanel);
        add(Box.createVerticalStrut(6)); // spacing
        add(bottomPanel);
        add(Box.createVerticalStrut(12)); // spacing

        setVisible(true);
    }

}
