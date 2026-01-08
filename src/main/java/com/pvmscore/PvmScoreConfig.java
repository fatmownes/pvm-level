package com.pvmscore;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pvmscore")
public interface PvmScoreConfig extends Config
{
	@ConfigItem(
		keyName = "enablePointDrop",
		name = "Enable Point Drops",
		description = "Enables point drops when bosses are killed.",
		position = 0
	)
	default boolean enablePointDrop()
	{
		return true;
	}
}
