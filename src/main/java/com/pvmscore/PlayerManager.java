package com.pvmscore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.hiscore.*;

import java.util.*;
import java.util.concurrent.*;
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

    public PlayerManager(Client client, HiscoreClient hiscoreClient) {
        this.client = client;
        this.hiscoreClient = hiscoreClient;
    }

    public PlayerStat getLocalPlayer() {
        return this.activeUsernameToKillList.get(localPlayer.getName());
    }

    public CompletableFuture<HiscoreResult> initLocalPlayer() {
        this.localPlayer = client.getLocalPlayer();
        PlayerStat localPlayerStat = new PlayerStat(this.localPlayer);

        return localPlayerStat.fetchPlayerKC().whenComplete((result, err) -> {

            if (err != null) {
                log.error("Error fetching Kc's for local player on log in.");
            }

            this.activeUsernameToKillList.put(Objects.requireNonNull(this.localPlayer.getName()), localPlayerStat);
            log.debug("Finished fetching local player.");

        });
    }

    public void addPlayer(Player player) {

        if (player == null || player.getName() == null) {
            return;
        }

        if (activeUsernameToKillList.containsKey(player.getName())) {
            return;
        }

        if (activeUsernameToKillList.size() > 1000) {
            return;
        }

        if (cachedUsernameToKillList.containsKey(player.getName())) {
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
            log.debug("Caching player who left scene {}", name);
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

        synchronized public CompletableFuture<HiscoreResult> fetchPlayerKC()
        {
            long start = System.currentTimeMillis();

            log.debug("Attempting to fetch KC's for player {}", player.getName());

            return hiscoreClient.lookupAsync(player.getName(), HiscoreEndpoint.NORMAL)
                    .whenComplete((result, err) -> {

                        if (err != null) {
                            log.error("Error fetching Kc's for {}", player.getName());
                            return;
                        }

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
                });
        }


    }

}
