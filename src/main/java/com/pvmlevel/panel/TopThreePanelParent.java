package com.pvmlevel.panel;

import com.pvmlevel.PlayerManager;
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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;

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

        JPanel topPanel = new TopThreePanel(spriteManager, hs1, kc1);
        JPanel middlePanel = new TopThreePanel(spriteManager, hs2, kc2);
        JPanel bottomPanel = new TopThreePanel(spriteManager, hs3, kc3);

        add(topPanel);
        add(Box.createVerticalStrut(6)); // spacing
        add(middlePanel);
        add(Box.createVerticalStrut(6)); // spacing
        add(bottomPanel);
        add(Box.createVerticalStrut(12)); // spacing

        setVisible(true);
    }

    private class TopThreePanel extends PluginPanel {

        TopThreePanel(SpriteManager spriteManager, HiscoreSkill hiscoreSkill, Integer kc)
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

}
