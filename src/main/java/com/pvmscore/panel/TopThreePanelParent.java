package com.pvmscore.panel;

import com.pvmscore.PlayerManager;
import com.pvmscore.PvmScore;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pvmscore.panel.RaidsPanelParent.translateName;

public class TopThreePanelParent extends PluginPanel
{

    TopThreePanelParent(SpriteManager spriteManager, PlayerManager.PlayerStat playerStat, List<Map.Entry<HiscoreSkill, Integer>> sorted,  boolean sortByKc) {

        setBorder(new EmptyBorder(3, 3, 3, 3));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ArrayList<JPanel> panels = new ArrayList<>();
        for(int i = 0; i < 3; i++) {

            HiscoreSkill hiscoreSkill;
            int kc;

            if (playerStat == null || !playerStat.hasFetchedKcs() || sorted.size() <= i) {
                hiscoreSkill = null;
                kc = 0;
            } else {
                hiscoreSkill = sorted.get(i).getKey();
                kc = sorted.get(i).getValue();
            }

            JPanel panel = new TopThreePanel(spriteManager, hiscoreSkill, kc, sortByKc);
            panels.add(panel);
        }

        panels.forEach(p -> {
            add(p);
            add(Box.createVerticalStrut(6)); // spacing
        });

        setVisible(true);
    }

    private class TopThreePanel extends PluginPanel {

        TopThreePanel(SpriteManager spriteManager, HiscoreSkill hiscoreSkill, Integer val, boolean sortByKc)
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

            String bossName = (hiscoreSkill != null) ? translateName(hiscoreSkill) : "None";
            bossName += (hiscoreSkill != null) ? " (" + PvmScore.FULL_POINT_MAPPINGS.get(hiscoreSkill)
                    + (PvmScore.FULL_POINT_MAPPINGS.get(hiscoreSkill) > 1 ? "pts" : "pt") + ")" : "";
            JLabel boss = new JLabel(bossName);
            boss.setFont(FontManager.getRunescapeBoldFont());
            boss.setForeground(Color.GRAY);

            String kcFormatted = QuantityFormatter.quantityToStackSize(val);
            JLabel valLabel = new JLabel(StringUtils.capitalize(kcFormatted) + (sortByKc ? " kc" : " pts"));

            valLabel.setForeground(Color.YELLOW);

            valLabel.setFont(FontManager.getRunescapeFont());
            valLabel.setToolTipText(NumberFormat.getNumberInstance(Locale.US).format(val));
            valLabel.setHorizontalAlignment(SwingConstants.CENTER);


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
                            .addComponent(valLabel)
                    )
            );

            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(spriteLabel)
                            .addComponent(boss)
                            .addComponent(valLabel)
                    )
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // flexible left gap
            );
        }

    }

}
