package com.pvmscore;

import com.google.common.collect.ImmutableList;
import net.runelite.api.NPC;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.hiscore.HiscoreSkill;

import java.util.HashMap;
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
            PHOSANIS_NIGHTMARE,
            THE_CORRUPTED_GAUNTLET,
            YAMA,
            DOOM_OF_MOKHAIOTL,
            NEX
    );

    //3 pts
    public static final List<HiscoreSkill> ELITE_BOSSES = ImmutableList.of(
            TZTOK_JAD,
            THE_WHISPERER,
            DUKE_SUCELLUS,
            THE_LEVIATHAN,
            VARDORVIS,
            PHANTOM_MUSPAH,
            ARAXXOR,
            NIGHTMARE,
            CORPOREAL_BEAST,
            CALLISTO,
            VENENATIS,
            VETION,
            THE_GAUNTLET,
            ZULRAH,
            VORKATH,
            ALCHEMICAL_HYDRA,
            CERBERUS,
            COMMANDER_ZILYANA,
            GENERAL_GRAARDOR,
            KREEARRA,
            KRIL_TSUTSAROTH
    );

    //2 pts
    public static final List<HiscoreSkill> HARD_BOSSES = ImmutableList.of(
            ABYSSAL_SIRE,
            ARTIO,
            CALVARION,
            SPINDEL,
            DAGANNOTH_PRIME,
            DAGANNOTH_REX,
            DAGANNOTH_SUPREME,
            LUNAR_CHESTS,
            KALPHITE_QUEEN
    );


    //1 pt
    public static final List<HiscoreSkill> EASY_BOSSES = ImmutableList.of(
            AMOXLIATL,
            BARROWS_CHESTS,
            BRYOPHYTA,
            CHAOS_ELEMENTAL,
            CHAOS_FANATIC,
            CRAZY_ARCHAEOLOGIST,
            DERANGED_ARCHAEOLOGIST,
            GIANT_MOLE,
            GROTESQUE_GUARDIANS,
            HESPORI,
            KING_BLACK_DRAGON,
            KRAKEN,
            MIMIC,
            OBOR,
            SARACHNIS,
            SCORPIA,
            SCURRIUS,
            SHELLBANE_GRYPHON,
            SKOTIZO,
            TEMPOROSS,
            THE_HUEYCOATL,
            THE_ROYAL_TITANS,
            THERMONUCLEAR_SMOKE_DEVIL,
            WINTERTODT,
            ZALCANO
    );

    private static final Map<List<HiscoreSkill>, Integer> BOSS_LIST_TO_POINT_MAP =
            Map.of(GRAND_MASTER_BOSSES, GRAND_MASTER_POINTS,
                    HARD_MODE_RAIDS, HARD_MODE_RAIDS_POINTS,
                    RAIDS, RAIDS_POINTS,
                    MASTER_BOSSES, MASTER_POINTS,
                    ELITE_BOSSES, ELITE_POINTS,
                    HARD_BOSSES, HARD_POINTS,
                    EASY_BOSSES, DEFAULT_POINTS
            );

    public static final List<List<HiscoreSkill>> ALL = List.of(
            GRAND_MASTER_BOSSES,
            HARD_MODE_RAIDS,
            RAIDS,
            MASTER_BOSSES,
            ELITE_BOSSES, HARD_BOSSES, EASY_BOSSES);

    public static Map<HiscoreSkill, Integer> FULL_POINT_MAPPINGS;
    public static Map<String, Integer> FULL_POINT_MAPPINGS_WITH_NPC_NAME;


    public static final Map<Integer, HiscoreSkill> NPC_ID_TO_BOSS = Map.ofEntries(
            // GRAND MASTER BOSSES
            Map.entry(NpcID.INFERNO_TZKALZUK_PLACEHOLDER, HiscoreSkill.TZKAL_ZUK), // Unsure
            Map.entry(NpcID.COLOSSEUM_SOL_P1, HiscoreSkill.SOL_HEREDIT), // Unsure

            // RAIDS - Note: These might not have single NPC constants
            Map.entry(NpcID.OLM_HEAD, HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE),
            Map.entry(NpcID.VERZIK_PHASE3_HARD, HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE),
            Map.entry(NpcID.TOA_WARDEN_TUMEKEN_PHASE3, HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT), // i dont see a different one for expert. tricky......!!! how will we do pts?

            Map.entry(NpcID.OLM_HEAD_SPAWNING, HiscoreSkill.CHAMBERS_OF_XERIC), //unsure if i should be using the other olm head
            Map.entry(NpcID.VERZIK_PHASE3, HiscoreSkill.THEATRE_OF_BLOOD),
            Map.entry(NpcID.TOA_WARDEN_TUMEKEN_PHASE3, HiscoreSkill.TOMBS_OF_AMASCUT), // there's also a p3 death NPC... but do those npcs actually ever die?

            // MASTER BOSSES
            Map.entry(NpcID.NIGHTMARE_CHALLENGE_WEAK_PHASE_04, HiscoreSkill.PHOSANIS_NIGHTMARE), // my best guess... again this npc has a dead npcID
            Map.entry(NpcID.CRYSTAL_HUNLLEF_MAGIC_HM, HiscoreSkill.THE_CORRUPTED_GAUNTLET), // 1
            Map.entry(NpcID.CRYSTAL_HUNLLEF_RANGED_HM, HiscoreSkill.THE_CORRUPTED_GAUNTLET), // 2
            Map.entry(NpcID.YAMA, HiscoreSkill.YAMA),
            Map.entry(NpcID.DOM_BOSS, HiscoreSkill.DOOM_OF_MOKHAIOTL), // 1
            Map.entry(NpcID.DOM_BOSS_SHIELDED, HiscoreSkill.DOOM_OF_MOKHAIOTL), //2
            Map.entry(NpcID.NEX_DYING, NEX), // TODO i want to test this fr

            // ELITE BOSSES
            Map.entry(NpcID.TZHAAR_FIGHTCAVE_SWARM_BOSS, HiscoreSkill.TZTOK_JAD),
            Map.entry(NpcID.WHISPERER, HiscoreSkill.THE_WHISPERER), // not sure what WHISPER_MELEE is
            Map.entry(NpcID.DUKE_SUCELLUS_AWAKE, HiscoreSkill.DUKE_SUCELLUS),
            Map.entry(NpcID.LEVIATHAN, HiscoreSkill.THE_LEVIATHAN),
            Map.entry(NpcID.VARDORVIS, HiscoreSkill.VARDORVIS),
            Map.entry(NpcID.MUSPAH_FINAL, HiscoreSkill.PHANTOM_MUSPAH),
            Map.entry(NpcID.ARAXXOR, HiscoreSkill.ARAXXOR),
            Map.entry(NpcID.NIGHTMARE_WEAK_PHASE_03, HiscoreSkill.NIGHTMARE),
            Map.entry(NpcID.CORP_BEAST, HiscoreSkill.CORPOREAL_BEAST),
            Map.entry(NpcID.CALLISTO, HiscoreSkill.CALLISTO),
            Map.entry(NpcID.VENENATIS, HiscoreSkill.VENENATIS),
            Map.entry(NpcID.VETION, HiscoreSkill.VETION),
            Map.entry(NpcID.CRYSTAL_HUNLLEF_MAGIC, HiscoreSkill.THE_GAUNTLET),
            Map.entry(NpcID.CRYSTAL_HUNLLEF_RANGED, HiscoreSkill.THE_GAUNTLET),
            Map.entry(NpcID.SNAKEBOSS_BOSS_MELEE, HiscoreSkill.ZULRAH),
            Map.entry(NpcID.SNAKEBOSS_BOSS_MAGIC, HiscoreSkill.ZULRAH),
            Map.entry(NpcID.SNAKEBOSS_BOSS_RANGED, HiscoreSkill.ZULRAH),
            Map.entry(NpcID.VORKATH, HiscoreSkill.VORKATH),
            Map.entry(NpcID.HYDRABOSS_2, HiscoreSkill.ALCHEMICAL_HYDRA),
            Map.entry(NpcID.CERBERUS_ATTACKING, HiscoreSkill.CERBERUS), // he has some other ones too that we might need to include.
            Map.entry(NpcID.GODWARS_SARADOMIN_AVATAR, HiscoreSkill.COMMANDER_ZILYANA),
            Map.entry(NpcID.GODWARS_BANDOS_AVATAR, HiscoreSkill.GENERAL_GRAARDOR),
            Map.entry(NpcID.GODWARS_ARMADYL_AVATAR, HiscoreSkill.KREEARRA),
            Map.entry(NpcID.GODWARS_ZAMORAK_AVATAR, HiscoreSkill.KRIL_TSUTSAROTH),

            // HARD BOSSES
            Map.entry(NpcID.ABYSSALSIRE_SIRE_APOCALYPSE, HiscoreSkill.ABYSSAL_SIRE),
            Map.entry(NpcID.CALLISTO_SINGLES, HiscoreSkill.ARTIO),
            Map.entry(NpcID.VETION_2_SINGLE, HiscoreSkill.CALVARION),
            Map.entry(NpcID.VENENATIS_SINGLES, HiscoreSkill.SPINDEL),
            Map.entry(NpcID.DAGCAVE_MAGIC_BOSS, HiscoreSkill.DAGANNOTH_PRIME),
            Map.entry(NpcID.DAGCAVE_MELEE_BOSS, HiscoreSkill.DAGANNOTH_REX),
            Map.entry(NpcID.DAGCAVE_RANGED_BOSS, HiscoreSkill.DAGANNOTH_SUPREME),
            Map.entry(-1, HiscoreSkill.LUNAR_CHESTS), // TODO not sure how we do this yet, perhaps we will do something custom with chest opening
            Map.entry(NpcID.SWAN_KALPHITE_2, HiscoreSkill.KALPHITE_QUEEN), // 1
            Map.entry(NpcID.KALPHITE_FLYINGQUEEN, HiscoreSkill.KALPHITE_QUEEN), // 2

            // EASY BOSSES
            Map.entry(NpcID.AMOXLIATL, HiscoreSkill.AMOXLIATL),
            Map.entry(-2, HiscoreSkill.BARROWS_CHESTS), // TODO not sure how we do this yet, perhaps we will do something custom with chest opening
            Map.entry(NpcID.GB_MOSSGIANT, HiscoreSkill.BRYOPHYTA),
            Map.entry(NpcID.CHAOSELEMENTAL, HiscoreSkill.CHAOS_ELEMENTAL),
            Map.entry(NpcID.CHAOS_FANATIC, HiscoreSkill.CHAOS_FANATIC),
            Map.entry(NpcID.CRAZY_ARCHAEOLOGIST, HiscoreSkill.CRAZY_ARCHAEOLOGIST),
            Map.entry(NpcID.FOSSIL_CRAZY_ARCHAEOLOGIST, HiscoreSkill.DERANGED_ARCHAEOLOGIST),
            Map.entry(NpcID.MOLE_GIANT, HiscoreSkill.GIANT_MOLE),
            Map.entry(NpcID.GARGBOSS_DUSK_PHASE4, HiscoreSkill.GROTESQUE_GUARDIANS),
            Map.entry(NpcID.HESPORI, HiscoreSkill.HESPORI),
            Map.entry(NpcID.KING_DRAGON, HiscoreSkill.KING_BLACK_DRAGON),
            Map.entry(NpcID.SLAYER_KRAKEN_BOSS, HiscoreSkill.KRAKEN),
            Map.entry(NpcID.TRAIL_MIMIC_COMBAT, HiscoreSkill.MIMIC),
            Map.entry(NpcID.HILLGIANT_BOSS, HiscoreSkill.OBOR),
            Map.entry(NpcID.SARACHNIS, HiscoreSkill.SARACHNIS),
            Map.entry(NpcID.SCORPIA, HiscoreSkill.SCORPIA),
            Map.entry(NpcID.RAT_BOSS_NORMAL, HiscoreSkill.SCURRIUS),
            Map.entry(NpcID.RAT_BOSS_INSTANCE, HiscoreSkill.SCURRIUS),
            Map.entry(NpcID.GRYPHON_BOSS, HiscoreSkill.SHELLBANE_GRYPHON),
            Map.entry(NpcID.CATA_BOSS, HiscoreSkill.SKOTIZO),
            Map.entry(NpcID.TEMPOROSS_BOSS_ENRAGED, HiscoreSkill.TEMPOROSS),
            Map.entry(NpcID.HUEY_HEAD_ENRAGED, HiscoreSkill.THE_HUEYCOATL), // TODO, test
            Map.entry(NpcID.RT_FIRE_QUEEN, HiscoreSkill.THE_ROYAL_TITANS), // TODO this will be bugged and we will see the drop twice.
            Map.entry(NpcID.RT_ICE_KING, HiscoreSkill.THE_ROYAL_TITANS), // TODO this will be bugged and we will see the drop twice.
            Map.entry(NpcID.SMOKE_DEVIL_BOSS, HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL),
//            Map.entry(-3, HiscoreSkill.WINTERTODT), // I dont think this is possible, straight up
            Map.entry(NpcID.ZALCANO_WEAK, HiscoreSkill.ZALCANO)
    );


    static {

        FULL_POINT_MAPPINGS = BOSS_LIST_TO_POINT_MAP.entrySet().stream()
                .flatMap(entry -> entry.getKey().stream()
                        .map(boss -> Map.entry(boss, entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        FULL_POINT_MAPPINGS_WITH_NPC_NAME = BOSS_LIST_TO_POINT_MAP.entrySet().stream()
                .flatMap(entry -> entry.getKey().stream()
                        .map(boss -> {

                            String bossName = boss.getName();
                            if (boss.equals(CHAMBERS_OF_XERIC)) {
                                bossName = "";
                            }

                            return Map.entry(boss.getName(), entry.getValue());
                        }))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        buildHiscoreToNpcId();

    }

    private static void buildHiscoreToNpcId() {
        Map<Integer, HiscoreSkill> temp = new HashMap<>();

        temp.put(NpcID.AMOXLIATL, AMOXLIATL);

    }

    public static int getScore(Map<HiscoreSkill, Integer> kcs, int divisor) {
        AtomicInteger score = new AtomicInteger();
        kcs.forEach((hiscore, kc) -> {

            if (EASY_BOSSES.contains(hiscore)) {
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
