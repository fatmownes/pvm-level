package com.pvmscore;

import net.runelite.client.hiscore.HiscoreSkill;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.runelite.client.hiscore.HiscoreSkill.*;
import static org.junit.Assert.*;

public class PvmScoreTest {

    private Map<HiscoreSkill, Integer> killCounts;

    @Before
    public void setUp() {
        killCounts = new HashMap<>();
    }

    @Test
    public void testEmptyKillCounts() {
        int score = PvmScore.getScore(killCounts, 1);
        assertEquals(0, score);
    }

    @Test
    public void testEachBossCategoryPoints() {
        // Grand Master
        killCounts.put(TZKAL_ZUK, 1);
        assertEquals(50, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Hard Mode Raids
        killCounts.put(CHAMBERS_OF_XERIC_CHALLENGE_MODE, 1);
        assertEquals(25, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Raids
        killCounts.put(CHAMBERS_OF_XERIC, 1);
        assertEquals(10, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Master
        killCounts.put(PHOSANIS_NIGHTMARE, 1);
        assertEquals(5, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Elite
        killCounts.put(ZULRAH, 1);
        assertEquals(3, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Hard
        killCounts.put(ARTIO, 1);
        assertEquals(2, PvmScore.getScore(killCounts, 1));
        killCounts.clear();

        // Default
        killCounts.put(KING_BLACK_DRAGON, 1);
        assertEquals(1, PvmScore.getScore(killCounts, 1));
    }

    @Test
    public void testMultipleKillsCalculation() {
        killCounts.put(ZULRAH, 100);
        assertEquals(300, PvmScore.getScore(killCounts, 1));
    }

    @Test
    public void testMixedBossCategories() {
        killCounts.put(TZKAL_ZUK, 2);                    // 100 pts
        killCounts.put(CHAMBERS_OF_XERIC, 10);           // 100 pts
        killCounts.put(ZULRAH, 50);                      // 150 pts
        killCounts.put(KING_BLACK_DRAGON, 20);           // 20 pts

        assertEquals(370, PvmScore.getScore(killCounts, 1));
    }

    @Test
    public void testDivisionByDivisor() {
        killCounts.put(TZKAL_ZUK, 10);

        assertEquals(500, PvmScore.getScore(killCounts, 1));
        assertEquals(50, PvmScore.getScore(killCounts, 10));
        assertEquals(5, PvmScore.getScore(killCounts, 100));
    }

    @Test
    public void testUnknownBossIgnored() {
        killCounts.put(ATTACK, 99);
        assertEquals(0, PvmScore.getScore(killCounts, 1));
    }

    @Test
    public void testNoDuplicateBossesInLists() {
        Map<HiscoreSkill, Integer> bossCounts = new HashMap<>();

        for (List<HiscoreSkill> bossList : PvmScore.ALL) {
            for (HiscoreSkill boss : bossList) {
                bossCounts.put(boss, bossCounts.getOrDefault(boss, 0) + 1);
            }
        }

        for (Map.Entry<HiscoreSkill, Integer> entry : bossCounts.entrySet()) {
            if (entry.getValue() > 1) {
                fail("Boss " + entry.getKey() + " appears in " + entry.getValue() + " categories");
            }
        }
    }
}