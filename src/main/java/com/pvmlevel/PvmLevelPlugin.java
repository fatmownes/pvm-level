package com.pvmlevel;

import com.google.inject.Provider;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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

import static net.runelite.api.GameState.LOADING;
import static net.runelite.api.GameState.LOGGED_IN;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class PvmLevelPlugin extends Plugin
{
	private static final String MENU_TITLE = "(PvM-level: ?)";
	private static final String MENU_TITLE_SUB = "(PvM-level";

	@Inject
	private Client client;

	@Inject
	private HiscoreClient hiscoreClient;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PvmLevelConfig config;

	Set<Player> previousPlayerSet;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Provider<MenuManager> menuManager;

	private PvMPluginPanel pvmPluginPanel;
	private NavigationButton navButton;

	private PlayerManager playerManager;

	private boolean firstTick = true;

	@Override
	protected void startUp() throws Exception
	{
		this.playerManager = new PlayerManager(client, hiscoreClient);
		menuManager.get().addPlayerMenuItem(MENU_TITLE);

		pvmPluginPanel = injector.getInstance(PvMPluginPanel.class);
		pvmPluginPanel.init(playerManager);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "game_icon_tzkalzuk.png");

		navButton = NavigationButton.builder()
				.tooltip("PvM-Level Panel")
				.icon(icon)
				.priority(5)
				.panel(pvmPluginPanel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		menuManager.get().removePlayerMenuItem(MENU_TITLE);
//		pvmPluginPanel.deinit();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOption().contains(MENU_TITLE_SUB))
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

			pvmPluginPanel.update(target.substring(0, target.indexOf("(level-")).trim());
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

						if (playerManager.getPlayer(playerName) != null) {
							menuEntry.setOption(
									String.format(
											"(PvM-level: %s)",
											playerManager.getPlayer(playerName).getLevel())
							);
						}
					}
				});
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		//if game log in set first tick to true;
	}

	@Subscribe
	public void onGameTick(final GameTick event) {

		// because LOGGED_IN Game state will have an incomplete local player.
		if (firstTick) {
			firstTick = false;
			playerManager.initLocalPlayer();
			pvmPluginPanel.update(Text.sanitize(playerManager.getLocalPlayer().getPlayer().getName()));
		}

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

	@Provides
	PvmLevelConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PvmLevelConfig.class);
	}

}
