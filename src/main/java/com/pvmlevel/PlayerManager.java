package com.pvmlevel;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.hiscore.HiscoreSkillType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class PlayerManager {

    @Getter
    private final ConcurrentHashMap<String, PlayerStat> activeUsernameToKillList = new ConcurrentHashMap<>();

    private final HashMap<String, PlayerStat> cachedUsernameToKillList = new HashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Queue<PlayerStat> kcLookupQueue = new ConcurrentLinkedQueue<>();

    private final Client client;
    private final HiscoreClient hiscoreClient;
    private Player localPlayer;
    private PlayerStat emptyPlayerStat;

    public PlayerManager(Client client, HiscoreClient hiscoreClient) {
        this.client = client;
        this.hiscoreClient = hiscoreClient;

        this.emptyPlayerStat = new PlayerStat(null);

    }

    public PlayerStat getLocalPlayer() {
        return this.activeUsernameToKillList.get(localPlayer.getName());
    }

    public void initLocalPlayer() {
        this.localPlayer = client.getLocalPlayer();
        try {
            PlayerStat localPlayerStat = new PlayerStat(this.localPlayer);

            // TODO!!! Lets actually make this async
            // it slows down the login by a lot.

            executor.submit(localPlayerStat::fetchPlayerKC).get();
            this.activeUsernameToKillList.put(this.localPlayer.getName(), localPlayerStat);
            log.debug("Finished fetching local player.");
        } catch (Exception e) {
            log.error("Error in sync waiting for local lookup.");
        }
    }

    public void addPlayer(Player player) {

        if (player == null || player.getName() == null) {
            return;
        }

        if (activeUsernameToKillList.containsKey(player.getName())) {
            return;
        }

        //safety?
        if (activeUsernameToKillList.size() > 1000) {
            return;
        }

        if (cachedUsernameToKillList.containsKey(player.getName())) {
            log.debug("adding back user from the cache.");
            activeUsernameToKillList.putIfAbsent(player.getName(), cachedUsernameToKillList.get(player.getName()));
            cachedUsernameToKillList.remove(player.getName());
        } else {
            PlayerStat ps = new PlayerStat(player);
            activeUsernameToKillList.putIfAbsent(player.getName(), ps);
            this.kcLookupQueue.offer(ps);
        }
    }

    public void removePlayer(Player player) {
        String name =  Objects.requireNonNull(player.getName());
        PlayerStat stats = activeUsernameToKillList.remove(name);
        if (stats != null && stats.hasFetchedKcs) {
            log.debug("caching {}", name);
            cachedUsernameToKillList.put(name, stats);
        }

    }

    public PlayerStat getPlayer(String name) {
        return this.activeUsernameToKillList.get(name);
    }


    public void processLookups() {
        if (this.kcLookupQueue.isEmpty()) {
            return;
        }

        PlayerStat ps = this.kcLookupQueue.poll();

        executor.submit(ps::fetchPlayerKC);
    }


    public class PlayerStat
    {
        @Getter
        private final Player player;

        @Getter
        private final Map<HiscoreSkill, Integer> killCounts;

        private boolean hasFetchedKcs = false;

        private int calculatedLevel = -1;
        private int calculatedKc = -1;

        private List<Map.Entry<HiscoreSkill, Integer>> sorted;

        PlayerStat(Player player) {
            this.player = player;
            this.killCounts = new HashMap<>();
        }

        private void addKc(HiscoreSkill boss, int kc) {
            this.killCounts.put(boss, kc);
        }

        public List<Map.Entry<HiscoreSkill, Integer>> getSorted() {

            if (!hasFetchedKcs) {
                kcLookupQueue.offer(this);
                return Collections.EMPTY_LIST;
            }

            if (sorted == null) {
                sorted = killCounts.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            }
            return sorted;
        }

        public boolean hasFetchedKcs() {
            return this.hasFetchedKcs;
        }

        public String getLevel() {
            if (!hasFetchedKcs) {
                if (!kcLookupQueue.contains(this)){
                    kcLookupQueue.offer(this);
                }
                return "?";
            }

            return String.valueOf(calculateLevel());
        }

        public String getTotalKc() {
            if (!hasFetchedKcs) {
                if (!kcLookupQueue.contains(this)){
                    kcLookupQueue.offer(this);
                }
                return "?";
            }

            return String.valueOf(calculateTotalKc());
        }

        private int calculateTotalKc() {
            if (calculatedKc != -1) {
                return this.calculatedKc;
            }

            AtomicInteger total = new AtomicInteger();

            this.killCounts.values().forEach(total::addAndGet);

            this.calculatedKc = total.get();
            return this.calculatedKc;

        }

        private int calculateLevel() {
            if (calculatedLevel != -1) {
                return this.calculatedLevel;
            }

            this.calculatedLevel = PvmScore.getScore(getKillCounts(), PvmScore.DIVISOR);

            return calculatedLevel;
        }

        private void fetchPlayerKC()
        {
            try {

                long start = System.currentTimeMillis();

                log.debug("Attempting to fetch KC's for player {}", player.getName());


                HiscoreResult result = hiscoreClient.lookup(player.getName());
                result.getSkills().forEach((hiscore, skill) -> {

                    if (hiscore.getType().equals(HiscoreSkillType.BOSS)) {
                        if (skill.getLevel() > 0) {
                            this.addKc(hiscore, skill.getLevel());
                        }
                    }

                });
                calculateLevel();
                hasFetchedKcs = true;
                long end = System.currentTimeMillis();
                log.debug("Hiscore Fetch took {} seconds for {}.", (end - start) / 1000, player.getName());
            }
            catch (Exception e)
            {
                log.warn("Failed to fetch kill count for {}", player.getName());
                log.warn(e.toString());
            }
        }

    }

}
