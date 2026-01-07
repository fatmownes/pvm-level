package com.pvmlevel.panel;

import com.pvmlevel.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class PvMPluginPanel extends PluginPanel {

    private final static String NO_PLAYER_SELECTED = "No player selected";
    private final static String NO_PLAYER_SELECTED_LEVEL = "?";

    private GridBagConstraints c;
    private JPanel bossPanels;
    private JPanel header;
    public JLabel nameLabel;
    public JLabel levelLabel;

    private PlayerManager playerManager;
    private SpriteManager spriteManager;

    public void init(PlayerManager playerManager, SpriteManager spriteManager) {
        this.playerManager = playerManager;
        this.spriteManager = spriteManager;
    }

    public PvMPluginPanel()
    {

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        bossPanels = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new CompoundBorder(
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

        header.setBackground(ColorScheme.DARK_GRAY_COLOR);
        header.add(nameLabel);
        header.add(levelLabel);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(header)
                        .addComponent(bossPanels)
                ).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(header)
                .addGap(10)
                .addComponent(bossPanels)
        );
    }

    // inferno / sol: 25
    // hard mode:     15
    //
    // raid :         10
    // something:      3
    // zulrah esq:     2
    // early-md l:     1

    // divide 1000 round down

    public void update(String playerName)
    {

        PlayerManager.PlayerStat playerStat;
        if (playerName.isEmpty() || playerName == null)
        {
            nameLabel.setText(NO_PLAYER_SELECTED);
            nameLabel.setText(NO_PLAYER_SELECTED_LEVEL);
            return;
        }
        else
        {
            playerStat = playerManager.getPlayer(playerName);
            nameLabel.setText("Player: " + playerName);
            levelLabel.setText("PvM Level: " + playerStat.getLevel());

        }


        SwingUtilities.invokeLater(() ->
                {
                    bossPanels.removeAll();
                    bossPanels.add(new TopThreePanelParent(spriteManager, playerStat));

                    List<Map.Entry<HiscoreSkill, Integer>> sorted = playerStat.getSorted();

                    if (playerStat.hasFetchedKcs()){
                        for (int i = 2; i < sorted.size(); i++) {
                            int currKc = sorted.get(i).getValue();
                            bossPanels.add(new BossPanel(sorted.get(i).getKey(), currKc), c);
                            c.gridy++;
                        }
                        header.revalidate();
                        header.repaint();
                    }
                }
        );
    }
}
