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
        if (localPlayer != null) {
            return this.activeUsernameToKillList.get(localPlayer.getName());
        } else {
            return new PlayerStat(null);
        }
    }

    public CompletableFuture<HiscoreResult> initLocalPlayer() {
        this.localPlayer = client.getLocalPlayer();
        PlayerStat localPlayerStat = new PlayerStat(this.localPlayer.getName());

        return localPlayerStat.fetchPlayerKC().whenComplete((result, err) -> {

            if (err != null) {
                log.error("Error fetching Kc's for local player on log in.");
            }

            this.activeUsernameToKillList.put(Objects.requireNonNull(this.localPlayer.getName()), localPlayerStat);
            log.debug("Finished fetching local player.");

        });
    }

    public PlayerStat addPlayer(String player) {

        if (player == null) {
            return null;
        }

        if (activeUsernameToKillList.containsKey(player)) {
            return activeUsernameToKillList.get(player);
        }

        if (activeUsernameToKillList.size() > 1000) {
            return null;
        }

        if (cachedUsernameToKillList.containsKey(player)) {
            activeUsernameToKillList.putIfAbsent(player, cachedUsernameToKillList.get(player));
            return cachedUsernameToKillList.remove(player);
        } else {
            PlayerStat ps = new PlayerStat(player);
            activeUsernameToKillList.putIfAbsent(player, ps);
            this.kcLookupQueue.offer(ps);
            return ps;
        }
    }

    // TODO refactor to a string?
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

        try {
            if (this.kcLookupQueue.isEmpty()) {
                return;
            }

            PlayerStat ps = this.kcLookupQueue.poll();
            executor.submit(() -> {

                try {
                    ps.fetchPlayerKC();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public class PlayerStat
    {
        @Getter
        private final String player;

        @Getter
        private final Map<HiscoreSkill, Integer> killCounts;
        @Getter
        private final Map<HiscoreSkill, Integer> pointCounts;

        private boolean hasFetchedKcs = false;

        private int calculatedLevel = -1;
        private int calculatedKc = -1;

        private List<Map.Entry<HiscoreSkill, Integer>> sortedByKc;
        private List<Map.Entry<HiscoreSkill, Integer>> sortedByScore;

        PlayerStat(String player) {
            this.player = player;
            this.killCounts = new HashMap<>();
            this.pointCounts = new HashMap<>(); // because its convenient to look these up later by Hiscore.
        }

        private void addKc(HiscoreSkill boss, int kc) {
            this.killCounts.put(boss, kc);
        }

        synchronized public List<Map.Entry<HiscoreSkill, Integer>> getSortedByScore() {
            if (!hasFetchedKcs) {
                kcLookupQueue.offer(this);
                return Collections.EMPTY_LIST;
            }

            if (sortedByScore == null) {
                sortedByScore = new ArrayList<>();
                //make a copy of kill counts so we don't mutate it.
                Map<HiscoreSkill, Integer> copy = new HashMap<>(killCounts);

                copy.entrySet().forEach((hiscore) -> {
                    int points = hiscore.getValue() * PvmScore.FULL_POINT_MAPPINGS.get(hiscore.getKey());
                    hiscore.setValue(points);
                    sortedByScore.add(hiscore);
                    pointCounts.put(hiscore.getKey(), points);
                });

                sortedByScore = sortedByScore.stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            }

            return sortedByScore;
        }

        synchronized public List<Map.Entry<HiscoreSkill, Integer>> getSortedByKC() {

            if (!hasFetchedKcs) {
                kcLookupQueue.offer(this);
                return Collections.EMPTY_LIST;
            }

            if (sortedByKc == null) {
                sortedByKc = killCounts.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            }
            return sortedByKc;
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

            return String.valueOf(calculateScore());
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

        private int calculateScore() {
            if (calculatedLevel != -1) {
                return this.calculatedLevel;
            }

            this.calculatedLevel = PvmScore.getScore(getKillCounts(), PvmScore.DIVISOR);

            return calculatedLevel;
        }

        synchronized public CompletableFuture<HiscoreResult> fetchPlayerKC()
        {
            long start = System.currentTimeMillis();

            log.debug("Attempting to fetch KC's for player {}", player);

            return hiscoreClient.lookupAsync(player, HiscoreEndpoint.NORMAL)
                    .whenComplete((result, err) -> {

                        if (err != null) {
                            log.error("Error fetching Kc's for {}", player);
                            return;
                        }

                        result.getSkills().forEach((hiscore, skill) -> {
                            if (hiscore.getType().equals(HiscoreSkillType.BOSS)) {
                                if (skill.getLevel() > 0) {
                                    this.addKc(hiscore, skill.getLevel());
                                }
                            }
                        });

                    calculateScore();
                    getSortedByKC();
                    hasFetchedKcs = true;
                    long end = System.currentTimeMillis();
                    log.debug("Hiscore Fetch took {} seconds for {}.", (end - start) / 1000, player);
                });
        }


    }

}
