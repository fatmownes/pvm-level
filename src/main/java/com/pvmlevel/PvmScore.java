package com.pvmlevel;

import com.google.common.collect.ImmutableList;
import net.runelite.client.hiscore.HiscoreSkill;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.runelite.client.hiscore.HiscoreSkill.*;

public class PvmScore
{

    static final int GRAND_MASTER_POINTS = 50;
    static final int HARD_MODE_RAIDS_POINTS = 25;
    static final int RAIDS_POINTS = 10;
    static final int MASTER_POINTS = 5;
    static final int ELITE_POINTS = 3;
    static final int HARD_POINTS = 2;
    static final int DEFAULT_POINTS = 1;

    static final int DIVISOR = 100;

    //50 pts
    private static final List<HiscoreSkill> GRAND_MASTER_BOSSES = ImmutableList.of(
            TZKAL_ZUK, SOL_HEREDIT
    );

    //25 pts
    private static final List<HiscoreSkill> HARD_MODE_RAIDS = ImmutableList.of(
            CHAMBERS_OF_XERIC_CHALLENGE_MODE, THEATRE_OF_BLOOD_HARD_MODE, TOMBS_OF_AMASCUT_EXPERT
    );

    //10 pts
    private static final List<HiscoreSkill> RAIDS = ImmutableList.of(
            CHAMBERS_OF_XERIC, THEATRE_OF_BLOOD, TOMBS_OF_AMASCUT
    );

    //5 pts
    private static final List<HiscoreSkill> MASTER_BOSSES = ImmutableList.of(
            TZTOK_JAD, // being included here because of time? Maybe move up.
            THE_WHISPERER,
            DUKE_SUCELLUS,
            THE_LEVIATHAN,
            VARDORVIS,
            YAMA,
            PHOSANIS_NIGHTMARE,
            THE_CORRUPTED_GAUNTLET,
            DOOM_OF_MOKHAIOTL,
            NEX /* This one is tough because of masses? */
    );

    //3 pts
    private static final List<HiscoreSkill> ELITE_BOSSES = ImmutableList.of(
            CALLISTO,
            VENENATIS,
            VETION,
            THE_GAUNTLET,
            PHANTOM_MUSPAH,
            ARAXXOR,
            NIGHTMARE, /* This one is tough because of masses? */
            CORPOREAL_BEAST
    );

    //2 pts
    private static final List<HiscoreSkill> HARD_BOSSES = ImmutableList.of(
            ZULRAH,
            VORKATH,
            ALCHEMICAL_HYDRA
    );

    //1 pt
    private static final List<HiscoreSkill> BOSSES = ImmutableList.of(
            ABYSSAL_SIRE, AMOXLIATL, ARTIO, BARROWS_CHESTS,
            BRYOPHYTA, CALVARION, CERBERUS,
            CHAOS_ELEMENTAL, CHAOS_FANATIC, COMMANDER_ZILYANA, CRAZY_ARCHAEOLOGIST, DAGANNOTH_PRIME,
            DAGANNOTH_REX, DAGANNOTH_SUPREME, DERANGED_ARCHAEOLOGIST, GENERAL_GRAARDOR,
            GIANT_MOLE, GROTESQUE_GUARDIANS, HESPORI,
            KALPHITE_QUEEN, KING_BLACK_DRAGON, KRAKEN,
            KREEARRA, KRIL_TSUTSAROTH, LUNAR_CHESTS, MIMIC, OBOR,
            SARACHNIS, SCORPIA, SCURRIUS,
            SHELLBANE_GRYPHON, SKOTIZO,
            SPINDEL, TEMPOROSS, THE_HUEYCOATL,
            THE_ROYAL_TITANS, THERMONUCLEAR_SMOKE_DEVIL, WINTERTODT,
            ZALCANO
    );


    public static int getScore(Map<HiscoreSkill, Integer> kcs, int divisor) {
        AtomicInteger score = new AtomicInteger();
        kcs.forEach((hiscore, kc) -> {

            if (BOSSES.contains(hiscore)) {
                score.addAndGet(DEFAULT_POINTS * kc);
            } else if(HARD_BOSSES.contains(hiscore)) {
                score.addAndGet(HARD_POINTS * kc);
            } else if(ELITE_BOSSES.contains(hiscore)) {
                score.addAndGet(ELITE_POINTS * kc);
            } else if(MASTER_BOSSES.contains(hiscore)) {
                score.addAndGet(MASTER_POINTS * kc);
            } else if(RAIDS.contains(hiscore)) {
                score.addAndGet(RAIDS_POINTS * kc);
            } else if(HARD_MODE_RAIDS.contains(hiscore)) {
                score.addAndGet(HARD_MODE_RAIDS_POINTS * kc);
            } else if(GRAND_MASTER_BOSSES.contains(hiscore)) {
                score.addAndGet(GRAND_MASTER_POINTS * kc);
            }

        });

        return score.get() / divisor;
    }


}
