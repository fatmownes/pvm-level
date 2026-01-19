package com.pvmscore.panel;

import com.pvmscore.PlayerManager;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RaidsPanelParent extends PluginPanel {

    RaidsPanelParent(SpriteManager spriteManager, PlayerManager.PlayerStat playerStat, boolean hardMode, boolean sortByKc) {

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel topPanel;
        JPanel middlePanel;
        JPanel bottomPanel;
        if (hardMode) {
            topPanel = new RaidsPanel(spriteManager, HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE, getHardModeKcs(playerStat, sortByKc).get(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE), true, sortByKc);
            middlePanel = new RaidsPanel(spriteManager, HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE, getHardModeKcs(playerStat, sortByKc).get(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE), true, sortByKc);
            bottomPanel = new RaidsPanel(spriteManager, HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT, getHardModeKcs(playerStat, sortByKc).get(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT), true, sortByKc);
        } else {
            topPanel = new RaidsPanel(spriteManager, HiscoreSkill.CHAMBERS_OF_XERIC, getRaidsKc(playerStat, sortByKc).get(HiscoreSkill.CHAMBERS_OF_XERIC), false, sortByKc);
            middlePanel = new RaidsPanel(spriteManager, HiscoreSkill.THEATRE_OF_BLOOD, getRaidsKc(playerStat, sortByKc).get(HiscoreSkill.THEATRE_OF_BLOOD), false, sortByKc);
            bottomPanel = new RaidsPanel(spriteManager, HiscoreSkill.TOMBS_OF_AMASCUT, getRaidsKc(playerStat, sortByKc).get(HiscoreSkill.TOMBS_OF_AMASCUT), false, sortByKc);
        }

        add(topPanel);
        add(Box.createHorizontalStrut(6)); // spacing
        add(middlePanel);
        add(Box.createHorizontalStrut(6)); // spacing
        add(bottomPanel);

        setVisible(true);
    }

    private Map<HiscoreSkill, Integer> getHardModeKcs(PlayerManager.PlayerStat playerStat, boolean sortByKc) {
        Map<HiscoreSkill, Integer> result = new HashMap<>();
        result.put(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE,
                playerStat == null ? 0 : (sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE, 0)
        );
        result.put(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE,
                playerStat == null ? 0 : (sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE, 0)
        );
        result.put(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT,
                playerStat == null ? 0 : (sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT, 0)
        );

        return result;
    }

    private Map<HiscoreSkill, Integer> getRaidsKc(PlayerManager.PlayerStat playerStat, boolean sortByKc) {
        Map<HiscoreSkill, Integer> result = new HashMap<>();
        result.put(HiscoreSkill.CHAMBERS_OF_XERIC,
                playerStat == null ? 0 :(sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.CHAMBERS_OF_XERIC, 0));
        result.put(HiscoreSkill.THEATRE_OF_BLOOD,
                playerStat == null ? 0 : (sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.THEATRE_OF_BLOOD, 0));
        result.put(HiscoreSkill.TOMBS_OF_AMASCUT,
                playerStat == null ? 0 : (sortByKc ? playerStat.getKillCounts() : playerStat.getPointCounts()).getOrDefault(HiscoreSkill.TOMBS_OF_AMASCUT, 0));

        return result;
    }

    private class RaidsPanel extends JPanel {

        public RaidsPanel(SpriteManager spriteManager, HiscoreSkill hiscoreSkill, int val, boolean hardMode, boolean sortByKc)
        {

            Color borderColor;
            if (hardMode) {
                setBackground(new Color(97, 57, 52));
                borderColor = new Color(30, 30, 30);
            } else {
                setBackground(ColorScheme.DARKER_GRAY_COLOR);
                borderColor = new Color(58, 58, 58);
            }

            // Move border to the panel
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            // Make panel fill its parent's width
            setAlignmentX(Component.CENTER_ALIGNMENT);

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);

            JLabel boss = getBossLabelName(hiscoreSkill);

            String kcFormatted = QuantityFormatter.quantityToStackSize(val) + (sortByKc ? " kc" : " pts");
            JLabel valLabel = new JLabel(StringUtils.capitalize(kcFormatted));

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

        @Nonnull
        private JLabel getBossLabelName(HiscoreSkill hiscoreSkill) {
            String bossName = translateName(hiscoreSkill);

            JLabel boss = new JLabel(bossName);
            boss.setFont(FontManager.getRunescapeBoldFont());
            boss.setForeground(Color.GRAY);
            return boss;
        }

    }

    public static String translateName(HiscoreSkill hiscoreSkill) {
        if (hiscoreSkill.equals(HiscoreSkill.CHAMBERS_OF_XERIC)) {
            return  "CoX";
        }

        if (hiscoreSkill.equals(HiscoreSkill.THEATRE_OF_BLOOD)) {
            return "ToB";
        }

        if (hiscoreSkill.equals(HiscoreSkill.TOMBS_OF_AMASCUT)) {
            return "ToA";
        }

        if (hiscoreSkill.equals(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE)) {
            return "CoX CM";
        }

        if (hiscoreSkill.equals(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE)) {
            return "ToB HM";
        }

        if (hiscoreSkill.equals(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT)) {
            return "ToA Ex";
        }

        return StringUtils.capitalize(hiscoreSkill.getName().toLowerCase());
    }

}
