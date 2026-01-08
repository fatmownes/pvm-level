package com.pvmscore;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PvMScorePluginTest {
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(PvmScorePlugin.class);
        RuneLite.main(args);
    }
}
