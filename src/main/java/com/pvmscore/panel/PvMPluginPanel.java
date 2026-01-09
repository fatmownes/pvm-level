package com.pvmscore.panel;

import com.pvmscore.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class PvMPluginPanel extends PluginPanel {

    public final static String NO_PLAYER_SELECTED = "No player selected";
    public final static String NO_PLAYER_SELECTED_LEVEL = "Score: ?";
    public final static String NO_PLAYER_SELECTED_KC = "Total kills: ?";

    private static final String LOADING_TEXT = "loading...";

    private GridBagConstraints c;
    private JPanel bossPanels;
    private JPanel topThreePanel;
    private HeaderPanel header;
    private JPanel raidsPanel;
    private JPanel hardModeRaidsPanel;

    private JPanel gmPanel;

    private PlayerManager playerManager;
    private SpriteManager spriteManager;

    private GroupLayout layout;

    private JButton resetToSelf;

    public void init(PlayerManager playerManager, SpriteManager spriteManager) {
        this.playerManager = playerManager;
        this.spriteManager = spriteManager;

        layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        this.raidsPanel = new RaidsPanelParent(spriteManager, null, false);

        this.gmPanel = new GMPanelParent(spriteManager, null);

        this.hardModeRaidsPanel = new RaidsPanelParent(spriteManager, null, true);

        this.topThreePanel = new TopThreePanelParent(spriteManager, null);

        this.bossPanels = new JPanel(new GridBagLayout());

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        header = new HeaderPanel();

        resetToSelf = new JButton();
        resetToSelf.setBackground(ColorScheme.DARK_GRAY_COLOR);
        resetToSelf.setFont(FontManager.getRunescapeSmallFont());
        resetToSelf.add(new JLabel("Reset to Me"));
        resetToSelf.addActionListener(e -> {
            Player localPlayer = playerManager.getLocalPlayer().getPlayer();

            if (localPlayer != null) {
                update(localPlayer.getName());
            }
        });

        header.add(resetToSelf);

        setLayout();
    }

    public PvMPluginPanel()
    {

    }

    private void setLayout() {
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(header)
                        .addComponent(hardModeRaidsPanel)
                        .addComponent(gmPanel)
                        .addComponent(raidsPanel)
                        .addComponent(topThreePanel)
                        .addComponent(bossPanels)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(header)
                .addGap(10)
                .addComponent(gmPanel)
                .addGap(10)
                .addComponent(hardModeRaidsPanel)
                .addGap(10)
                .addComponent(raidsPanel)
                .addGap(10)
                .addComponent(topThreePanel)
                .addGap(10)
                .addComponent(bossPanels)
        );
    }

    public void loading(String playerName) {
        header.nameLabel.setText("Player: " + playerName);
        header.scoreLabel.setText("Score: " + LOADING_TEXT);
        header.totalKcLabel.setText("Total kills: " + LOADING_TEXT);
    }

    public void update(String playerName)
    {
        PlayerManager.PlayerStat playerStat;
        if (playerName.isEmpty() || playerName == null)
        {
            this.header.nameLabel.setText(NO_PLAYER_SELECTED);
            this.header.scoreLabel.setText(NO_PLAYER_SELECTED_LEVEL);
            this.header.totalKcLabel.setText(NO_PLAYER_SELECTED_KC);
            return;
        }
        else
        {
            playerStat = playerManager.getPlayer(playerName);
            this.header.nameLabel.setText("Player: " + playerName);
            this.header.scoreLabel.setText("Score: " + playerStat.getLevel());
            this.header.totalKcLabel.setText("Total kills: " + playerStat.getTotalKc());
        }

        SwingUtilities.invokeLater(() ->
                {
                    removeAll();
                    bossPanels.removeAll();

                    topThreePanel = new TopThreePanelParent(spriteManager, playerStat);
                    raidsPanel = new RaidsPanelParent(spriteManager, playerStat, false);
                    hardModeRaidsPanel = new RaidsPanelParent(spriteManager, playerStat, true);
                    gmPanel = new GMPanelParent(spriteManager, playerStat);

                    List<Map.Entry<HiscoreSkill, Integer>> sorted = playerStat.getSorted();
                    if (playerStat.hasFetchedKcs()){
                        for (int i = 3; i < sorted.size(); i++) {
                            int currKc = sorted.get(i).getValue();
                            bossPanels.add(new BossPanel(sorted.get(i).getKey(), currKc), c);
                            c.gridy++;
                        }
                        header.revalidate();
                        header.repaint();
                    }

                    setLayout();

                }
        );
    }
}
