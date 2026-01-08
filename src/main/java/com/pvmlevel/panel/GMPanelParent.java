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

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;

public class GMPanelParent extends PluginPanel {

    GMPanelParent(SpriteManager spriteManager, PlayerManager.PlayerStat playerStat) {

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel solPanel = new GMPanel(spriteManager, HiscoreSkill.SOL_HEREDIT, playerStat == null ? 0 : playerStat.getKillCounts().getOrDefault(HiscoreSkill.SOL_HEREDIT, 0));
        JPanel zukPanel = new GMPanel(spriteManager, HiscoreSkill.TZKAL_ZUK, playerStat == null ? 0 : playerStat.getKillCounts().getOrDefault(HiscoreSkill.SOL_HEREDIT, 0));

        add(solPanel);
        add(Box.createHorizontalStrut(6)); // spacing
        add(zukPanel);

        setVisible(true);
    }

    private class GMPanel extends JPanel {

        public GMPanel(SpriteManager spriteManager, HiscoreSkill hiscoreSkill, int kc)
        {

            setBackground(ColorScheme.DARKER_GRAY_COLOR);
            Color borderColor = new Color(58, 58, 58);

            // Move border to the panel
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            // Make panel fill its parent's width
            setAlignmentX(Component.CENTER_ALIGNMENT);

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);

            JLabel boss = getBossLabelName(hiscoreSkill);

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

        @Nonnull
        private JLabel getBossLabelName(HiscoreSkill hiscoreSkill) {
            String bossName = "";

            if (hiscoreSkill.equals(HiscoreSkill.TZKAL_ZUK)) {
                bossName = "Zuk";
            }

            if (hiscoreSkill.equals(HiscoreSkill.SOL_HEREDIT)) {
                bossName = "Sol";
            }

            JLabel boss = new JLabel(bossName);
            boss.setFont(FontManager.getRunescapeBoldFont());
            boss.setForeground(Color.GRAY);
            return boss;
        }

    }

}
