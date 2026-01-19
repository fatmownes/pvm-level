package com.pvmscore.panel;

import com.pvmscore.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
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
    private JButton sort;
    private boolean sortByKc = false;
    private static final String SORT_BY_KC = "Sort by KC";
    private static final String SORT_BY_PTS = "Sort by Points";

    private String currentSort = SORT_BY_PTS;

    private PlayerManager.PlayerStat currentPlayer = null;
    private IconTextField searchBar;
    private boolean loading = false;

    public void init(PlayerManager playerManager, SpriteManager spriteManager) {
        this.playerManager = playerManager;
        this.spriteManager = spriteManager;

        layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        this.raidsPanel = new RaidsPanelParent(spriteManager, null, false, sortByKc);

        this.gmPanel = new GMPanelParent(spriteManager, null);

        this.hardModeRaidsPanel = new RaidsPanelParent(spriteManager, null, true, sortByKc);

        this.topThreePanel = new TopThreePanelParent(spriteManager, null, Collections.EMPTY_LIST, sortByKc);

        this.bossPanels = new JPanel(new GridBagLayout());

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        header = new HeaderPanel();

        resetToSelf = new JButton();
        resetToSelf.setBackground(ColorScheme.DARK_GRAY_COLOR);
        resetToSelf.setFont(FontManager.getRunescapeFont());
        resetToSelf.setText("Reset to Me");
        resetToSelf.addActionListener(e -> {
            String localPlayer = playerManager.getLocalPlayer().getPlayer();

            if (localPlayer != null) {
                update(localPlayer);
            }
        });

        sort = new JButton();
        sort.setBackground(ColorScheme.DARK_GRAY_COLOR);
        sort.setFont(FontManager.getRunescapeFont());
        sort.setText(currentSort);
        sort.addActionListener(e -> {
            sortByKc = !sortByKc;

            if (currentPlayer != null) {
                update(currentPlayer.getPlayer());
            }
        });

        header.add(resetToSelf);
        header.add(sort);

        initSearchBar();
        header.add(searchBar);

        setLayout();
    }

    public PvMPluginPanel()
    {

    }

    private void initSearchBar() {
        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setEditable(true);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.setMinimumSize(new Dimension(0, 30));
        searchBar.addActionListener(e -> {
            playerManager.addPlayer(searchBar.getText()).fetchPlayerKC()
                    .whenComplete((r, throwable) ->
                            update(searchBar.getText())
            );
        });
        searchBar.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() != 2)
                {
                    return;
                }

                playerManager.addPlayer(searchBar.getText()).fetchPlayerKC()
                        .whenComplete((r, throwable) -> {
                    update(searchBar.getText());
                });
            }
        });
        searchBar.addClearListener(() ->
        {
            searchBar.setIcon(IconTextField.Icon.SEARCH);
            searchBar.setEditable(true);
            loading = false;
        });

//        add(searchBar, c);
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
        loading = true;
        header.nameLabel.setText("Player: " + playerName);
        header.scoreLabel.setText("Score: " + LOADING_TEXT);
        header.totalKcLabel.setText("Total kills: " + LOADING_TEXT);
    }

    public void clearSearchBar() {
        SwingUtilities.invokeLater(() -> {
            this.searchBar.setText("");
        });
    }

    public void update(String playerName)
    {
        loading = false;
        PlayerManager.PlayerStat playerStat;

        if (playerName.isEmpty() || playerName == null)
        {
            this.header.nameLabel.setText(NO_PLAYER_SELECTED);
            this.header.scoreLabel.setText(NO_PLAYER_SELECTED_LEVEL);
            this.header.totalKcLabel.setText(NO_PLAYER_SELECTED_KC);
            currentPlayer = null;
            return;
        }
        else
        {
            playerStat = playerManager.getPlayer(playerName);

            if (currentPlayer == null || !currentPlayer.equals(playerStat)) {
                currentPlayer = playerStat;
            }

            this.header.nameLabel.setText("Player: " + playerName);
            this.header.scoreLabel.setText("Score: " + currentPlayer.getLevel());
            this.header.totalKcLabel.setText("Total kills: " + currentPlayer.getTotalKc());
            currentPlayer.getSortedByKC(); //init
            currentPlayer.getSortedByScore(); //init
        }

        SwingUtilities.invokeLater(() ->
                {
                    removeAll();
                    bossPanels.removeAll();

                    raidsPanel = new RaidsPanelParent(spriteManager, currentPlayer, false, sortByKc);
                    hardModeRaidsPanel = new RaidsPanelParent(spriteManager, currentPlayer, true, sortByKc);
                    gmPanel = new GMPanelParent(spriteManager, currentPlayer);

                    List<Map.Entry<HiscoreSkill, Integer>> sorted;


                    if (sortByKc) {
                        sort.setText(SORT_BY_PTS);

                        sorted = currentPlayer.getSortedByKC();
                        topThreePanel = new TopThreePanelParent(spriteManager, currentPlayer, sorted, sortByKc);
                    } else {
                        sort.setText(SORT_BY_KC);

                        sorted = currentPlayer.getSortedByScore();
                        topThreePanel = new TopThreePanelParent(spriteManager, currentPlayer, sorted, sortByKc);
                    }

                    if (currentPlayer.hasFetchedKcs()){
                        for (int i = 3; i < sorted.size(); i++) {
                            int currVal = sorted.get(i).getValue();
                            bossPanels.add(new BossPanel(sorted.get(i).getKey(), currVal, sortByKc), c);
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
