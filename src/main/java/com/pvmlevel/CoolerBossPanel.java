package com.pvmlevel;

import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;


public class CoolerBossPanel extends PluginPanel {

    CoolerBossPanel(SpriteManager spriteManager, HiscoreSkill hiscoreSkill, Integer kc)
    {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Move border to the panel
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Make panel fill its parent's width
        setAlignmentX(Component.CENTER_ALIGNMENT);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        String bossName = (hiscoreSkill != null) ? StringUtils.capitalize(hiscoreSkill.getName().toLowerCase()) : "None";

        JLabel boss = new JLabel(bossName);
        boss.setFont(FontManager.getRunescapeBoldFont());
        boss.setForeground(Color.GRAY);

        String kcFormatted = QuantityFormatter.quantityToStackSize(kc);
        JLabel kcLabel = new JLabel(StringUtils.capitalize(kcFormatted));

        kcLabel.setForeground(Color.YELLOW);

        kcLabel.setFont(FontManager.getRunescapeFont());
        kcLabel.setToolTipText(NumberFormat.getNumberInstance(Locale.US).format(kc));
        kcLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel spriteLabel = new JLabel();

        spriteManager.getSpriteAsync(hiscoreSkill == null ? SpriteID.SideIcons.COMBAT : hiscoreSkill.getSpriteId(), 0,
                (sprite) ->
                SwingUtilities.invokeLater(() ->
                {
                    final BufferedImage scaledSprite = ImageUtil.
                            resizeImage(ImageUtil.resizeCanvas(sprite, 25, 25), 20, 20);
                    spriteLabel.setIcon(new ImageIcon(scaledSprite));
                }));

        layout.setVerticalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(spriteLabel)
                        .addComponent(boss)
                        .addComponent(kcLabel)
                )

        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(spriteLabel)
                        .addComponent(boss)
                        .addComponent(kcLabel)
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
        );
    }

}
