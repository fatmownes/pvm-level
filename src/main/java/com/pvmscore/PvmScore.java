package com.pvmscore;

import com.google.common.collect.ImmutableList;
import net.runelite.client.hiscore.HiscoreSkill;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.runelite.client.hiscore.HiscoreSkill.*;

public class PvmScore
{

    public static final int GRAND_MASTER_POINTS = 50;
    public static final int HARD_MODE_RAIDS_POINTS = 25;
    public static final int RAIDS_POINTS = 10;
    public static final int MASTER_POINTS = 5;
    public static final int ELITE_POINTS = 3;
    public static final int HARD_POINTS = 2;
    public static final int DEFAULT_POINTS = 1;

    public static final List<Integer> POINT_VALUES = List.of(GRAND_MASTER_POINTS,
            HARD_MODE_RAIDS_POINTS,
            RAIDS_POINTS,
            MASTER_POINTS,
            ELITE_POINTS,
            HARD_POINTS,
            DEFAULT_POINTS);

    public static final int DIVISOR = 1;

    //50 pts
    public static final List<HiscoreSkill> GRAND_MASTER_BOSSES = ImmutableList.of(
            TZKAL_ZUK, SOL_HEREDIT
    );

    //25 pts
    public static final List<HiscoreSkill> HARD_MODE_RAIDS = ImmutableList.of(
            CHAMBERS_OF_XERIC_CHALLENGE_MODE, THEATRE_OF_BLOOD_HARD_MODE, TOMBS_OF_AMASCUT_EXPERT
    );

    //10 pts
    public static final List<HiscoreSkill> RAIDS = ImmutableList.of(
            CHAMBERS_OF_XERIC, THEATRE_OF_BLOOD, TOMBS_OF_AMASCUT
    );

    //5 pts
    public static final List<HiscoreSkill> MASTER_BOSSES = ImmutableList.of(
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
    public static final List<HiscoreSkill> ELITE_BOSSES = ImmutableList.of(
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
    public static final List<HiscoreSkill> HARD_BOSSES = ImmutableList.of(
            ZULRAH,
            VORKATH,
            ALCHEMICAL_HYDRA
    );

    //1 pt
    public static final List<HiscoreSkill> BOSSES = ImmutableList.of(
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

    private static final Map<List<HiscoreSkill>, Integer> BOSS_LIST_TO_POINT_MAP =
            Map.of(GRAND_MASTER_BOSSES, GRAND_MASTER_POINTS,
                    HARD_MODE_RAIDS, HARD_MODE_RAIDS_POINTS,
                    RAIDS, RAIDS_POINTS,
                    MASTER_BOSSES, MASTER_POINTS,
                    ELITE_BOSSES, ELITE_POINTS,
                    HARD_BOSSES, HARD_POINTS,
                    BOSSES, DEFAULT_POINTS
            );

    public static final List<List<HiscoreSkill>> ALL = List.of(
            GRAND_MASTER_BOSSES,
            HARD_MODE_RAIDS,
            RAIDS,
            MASTER_BOSSES,
            ELITE_BOSSES, HARD_BOSSES, BOSSES);

    public static Map<HiscoreSkill, Integer> FULL_POINT_MAPPINGS;

    static {

        FULL_POINT_MAPPINGS = BOSS_LIST_TO_POINT_MAP.entrySet().stream()
                .flatMap(entry -> entry.getKey().stream()
                        .map(boss -> Map.entry(boss, entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

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
