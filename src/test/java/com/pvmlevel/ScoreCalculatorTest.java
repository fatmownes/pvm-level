package com.pvmlevel;

import net.runelite.client.hiscore.HiscoreSkill;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ScoreCalculatorTest {

    private Map<HiscoreSkill, Integer> killCounts;

    //better for more accurate testing of the point calculations
    private final int defaultDivisor = 1;

    @Before
    public void setUp() {
        killCounts = new HashMap<>();
    }

    @Test
    public void testEmptyKillCounts() {
        int score = PvmScore.getScore(killCounts, defaultDivisor);
        assertEquals(0, score);
    }

    @Test
    public void testDefaultBossPoints() {
        killCounts.put(HiscoreSkill.OBOR, 10);

        int expected = (PvmScore.DEFAULT_POINTS * 10) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testHardBossPoints() {
        killCounts.put(HiscoreSkill.VORKATH, 5);

        int expected = (PvmScore.HARD_POINTS * 5) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testEliteBossPoints() {
        killCounts.put(HiscoreSkill.VETION, 3);

        int expected = (PvmScore.ELITE_POINTS * 3) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testMasterBossPoints() {
        killCounts.put(HiscoreSkill.TZKAL_ZUK, 1);

        int expected = (PvmScore.GRAND_MASTER_POINTS * 1) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testRaidsPoints() {
        killCounts.put(HiscoreSkill.CHAMBERS_OF_XERIC, 50);

        int expected = (PvmScore.RAIDS_POINTS * 50) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testHardModeRaidsPoints() {
        killCounts.put(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE, 10);

        int expected = (PvmScore.HARD_MODE_RAIDS_POINTS * 10) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testGrandMasterBossPoints() {
        killCounts.put(HiscoreSkill.SOL_HEREDIT, 5);

        int expected = (PvmScore.GRAND_MASTER_POINTS * 5) / defaultDivisor;
        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testMultipleBossTypes() {
        killCounts.put(HiscoreSkill.ABYSSAL_SIRE, 10);      // DEFAULT_POINTS
        killCounts.put(HiscoreSkill.VORKATH, 5);                // HARD_POINTS
        killCounts.put(HiscoreSkill.CORPOREAL_BEAST, 3);          // ELITE_POINTS

        int expected = (
                (PvmScore.DEFAULT_POINTS * 10) +
                        (PvmScore.HARD_POINTS * 5) +
                        (PvmScore.ELITE_POINTS * 3)
        ) / defaultDivisor;

        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }

    @Test
    public void testZeroKillCount() {
        killCounts.put(HiscoreSkill.ZULRAH, 0);

        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(0, score);
    }

    @Test
    public void testDivisionByHundredRoundsDown() {

        int divisor = 100;

        killCounts.put(HiscoreSkill.SCURRIUS, 99);

        int score = PvmScore.getScore(killCounts, divisor);

        assertEquals(0, score);
    }

    @Test
    public void testLargeKillCounts() {
        killCounts.put(HiscoreSkill.CHAOS_ELEMENTAL, 10000);
        killCounts.put(HiscoreSkill.VORKATH, 5000);

        int expected = (
                (PvmScore.DEFAULT_POINTS * 10000) +
                        (PvmScore.HARD_POINTS * 5000)
        ) / defaultDivisor;

        int score = PvmScore.getScore(killCounts, defaultDivisor);

        assertEquals(expected, score);
    }
}

