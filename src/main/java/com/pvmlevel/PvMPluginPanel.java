package com.pvmlevel;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.game.ItemManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class PvMPluginPanel extends PluginPanel {

    private final static String NO_PLAYER_SELECTED = "No player selected";

    private GridBagConstraints c;
    private JPanel bossPanels;
    private JPanel header;
    public JLabel nameLabel;

    PlayerManager playerManager;

    void init(PlayerManager playerManager) {
        this.playerManager = playerManager;
//        update(playerManager.getLocalPlayer().getPlayer(), playerManager.getLocalPlayer().getPlayer().getName());
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
        header.setLayout(new BorderLayout());
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        nameLabel = new JLabel(NO_PLAYER_SELECTED);
        nameLabel.setForeground(Color.YELLOW);
        nameLabel.setFont(FontManager.getRunescapeSmallFont());

        header.setBackground(ColorScheme.DARK_GRAY_COLOR);
        header.add(nameLabel, BorderLayout.CENTER);


        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(bossPanels)
                .addComponent(header)
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

        if (playerName.isEmpty() || playerName == null)
        {
            nameLabel.setText(NO_PLAYER_SELECTED);
            return;
        }
        else
        {
            nameLabel.setText("Player: " + playerName);
        }

        PlayerManager.PlayerStat playerStat = playerManager.getPlayer(playerName);

        SwingUtilities.invokeLater(() ->
                {
                    bossPanels.removeAll();
                    bossPanels.add(new TopThreePanelParent(playerStat));

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
