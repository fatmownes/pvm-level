package com.pvmscore.panel;

import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class BossPanel extends PluginPanel {

    BossPanel(HiscoreSkill hiscoreSkill, Integer kc)
    {
        setBorder(new EmptyBorder(3, 3, 3, 3));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JLabel boss = new JLabel(StringUtils.capitalize(hiscoreSkill.getName().toLowerCase()));
        boss.setFont(FontManager.getRunescapeSmallFont());
        boss.setForeground(Color.GRAY);
        String kcFormatted = QuantityFormatter.quantityToStackSize(kc);
        JLabel kcLabel = new JLabel(StringUtils.capitalize(kcFormatted));

        kcLabel.setForeground(Color.YELLOW);

        kcLabel.setFont(FontManager.getRunescapeFont());
        kcLabel.setToolTipText(NumberFormat.getNumberInstance(Locale.US).format(kc));

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
