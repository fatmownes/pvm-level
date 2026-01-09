package com.pvmscore;

import com.google.inject.Provider;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.swing.*;

import com.pvmscore.overlay.BossPointsOverlay;
import com.pvmscore.panel.PvMPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@PluginDescriptor(
	name = "PvM Score",
	configName = "PvmScoreConfig"
)
public class PvmScorePlugin extends Plugin
{
	private static final String MENU_TITLE = "PvM Score";

	@Inject
	private Client client;

	@Inject
	private HiscoreClient hiscoreClient;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PvmScoreConfig config;

	Set<Player> previousPlayerSet;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Provider<MenuManager> menuManager;

	@Inject
	SpriteManager spriteManager;

	@Inject
	PvmScoreConfig pvmScoreConfig;

	BossPointsOverlay bossPointsOverlay;

	private PvMPluginPanel pvmPluginPanel;
	private NavigationButton navButton;

	private PlayerManager playerManager;

	private boolean firstTick = true;

	public static final String YELLOW = "ffff00";
	public static final String GREEN = "00ff00";
	public static final String RED = "ff0000";
	public static final String ORANGE = "ff9040";

	private int tickCount = -1;

	private final Set<Integer> processedNpcIndices = new HashSet<>();

	@Override
	protected void startUp() throws Exception
	{
		this.playerManager = new PlayerManager(client, hiscoreClient);
		menuManager.get().addPlayerMenuItem(MENU_TITLE);

		pvmPluginPanel = injector.getInstance(PvMPluginPanel.class);
		pvmPluginPanel.init(playerManager, spriteManager);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "game_icon_tzkalzuk.png");

		navButton = NavigationButton.builder()
				.tooltip("PvM-Level Panel")
				.icon(icon)
				.priority(5)
				.panel(pvmPluginPanel)
				.build();

		bossPointsOverlay = new BossPointsOverlay(client);

		overlayManager.add(bossPointsOverlay);
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		menuManager.get().removePlayerMenuItem(MENU_TITLE);
		overlayManager.add(bossPointsOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOption().equals(MENU_TITLE))
		{
			try
			{
				SwingUtilities.invokeAndWait(() -> clientToolbar.openPanel(navButton));
			}
			catch (InterruptedException | InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}

			String target = Text.sanitize(Text.removeTags(event.getMenuEntry().getTarget()));

			// TODO, how do we make it more compelling for users to
			// want to increase THEIR score? Can we add it somewhere?
			// maybe this is in the form of a second panel that breaks down the score.

			String playerName = target.substring(0, target.indexOf("(score-")).trim();

			if (!playerManager.getPlayer(playerName).hasFetchedKcs()) {

				pvmPluginPanel.loading(playerName);
				playerManager.getPlayer(playerName).fetchPlayerKC().whenComplete((result, error) -> {
					pvmPluginPanel.update(playerName);
				});
			} else {
				pvmPluginPanel.update(playerName);
			}
		}

	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		Stream.of(event.getMenuEntries())
				.filter(e -> e.getOption().equals(MENU_TITLE))
				.forEach(menuEntry -> {
					Actor actor = menuEntry.getActor();

					if (actor instanceof Player) {
						Player player = (Player) actor;
						String playerName = player.getName();
						PlayerManager.PlayerStat playerStat = playerManager.getPlayer(playerName);
						if (playerStat != null) {
							menuEntry.setTarget(updateTarget(menuEntry, playerStat));
						}
					}
				});
	}

	private String updateTarget(MenuEntry menuEntry, PlayerManager.PlayerStat playerStat) {
		String s = menuEntry.getTarget();

		String newLevel = String.format("(score-%s)", playerStat.getLevel());

		// Replace the level text
		s = s.replaceAll("\\(level-\\d+\\)", newLevel);

		String color = colorLevelCompare(playerStat);

		if (!color.isBlank()) {
			// Replace the second <col=> tag
			s = s.replaceAll("(<col=[^>]+>[^<]+)<col=[^>]+>", "$1<col="
					+ colorLevelCompare(playerStat) + ">");
		}

		return s;
	}

	// TODO move me to the player manager probably.
	private String colorLevelCompare(PlayerManager.PlayerStat playerStat) {
		String color = "";

		if (playerStat.getLevel().equals("?")) {
			return "";
		}

		if (Integer.parseInt(playerStat.getLevel()) >
				Integer.parseInt(this.playerManager.getLocalPlayer().getLevel())) {
			color = RED;
		} else if (Integer.parseInt(playerStat.getLevel()) ==
				Integer.parseInt(this.playerManager.getLocalPlayer().getLevel())) {
			color = YELLOW;
		} else if (Integer.parseInt(playerStat.getLevel()) <
				Integer.parseInt(this.playerManager.getLocalPlayer().getLevel())) {
			color = GREEN;
		}

		return color;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		//if game log in set first tick to true;
		if (gameStateChanged.getGameState().equals(GameState.LOGGED_IN)) {
			firstTick = true;
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event) {

		// because LOGGED_IN Game state will have an incomplete local player.
		if (firstTick) {
			firstTick = false;
			playerManager.initLocalPlayer().whenComplete((result, ignore) -> {
				pvmPluginPanel.update(Text.sanitize(playerManager.getLocalPlayer().getPlayer().getName()));
			});
		}

		updateWorldPlayersState();

		if (pvmScoreConfig.enablePointDrop()) {
			handleNpc();
		}
	}

	private void updateWorldPlayersState() {
		IndexedObjectSet<? extends Player> players = client.getTopLevelWorldView().players();

		players.forEach(player -> {
			playerManager.addPlayer(player);
		});

		Set<Player> curr = StreamSupport.stream(players.stream().spliterator(), false).collect(Collectors.toSet());

		// this deals with players going away. I'm not super confident this is the best way to do this.
		if (previousPlayerSet != null) {
			Set<Player> removed = new HashSet<>(previousPlayerSet);
			removed.removeAll(curr);

			removed.forEach(p -> {
				log.debug("removing player {}", p.getName());
				playerManager.removePlayer(p);
			});
		}
		previousPlayerSet = curr;
		playerManager.processLookups();
	}

	private void handleNpc() {
		if (tickCount >= 0) {
			tickCount++;

			if (tickCount >= 3) {
				bossPointsOverlay.notifyNotKill();
				tickCount = -1;
				processedNpcIndices.clear();
			}
		}

		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			if (npc.isDead())
			{
				int npcIndex = npc.getIndex();

				// Only process this NPC once
				if (!processedNpcIndices.contains(npcIndex))
				{
					processedNpcIndices.add(npcIndex);
					bossPointsOverlay.notifyKill(npc);
					tickCount = 0;
				}
			}
		}
	}

	@Provides
	PvmScoreConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PvmScoreConfig.class);
	}

}
