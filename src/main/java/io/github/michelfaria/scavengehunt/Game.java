package io.github.michelfaria.scavengehunt;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

import static io.github.michelfaria.scavengehunt.ScavengeHunt.*;

public class Game implements Listener {
    private final ScavengeHunt plugin;

    private boolean isEnded;
    private long totalTime;
    private long timeLeft;

    private Long lastTime;
    private BossBar bossBar;
    private Integer updateTaskID;
    private Objective objective;

    public Game(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the game and cleans the state.
     */
    public void start(long time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time must be greater than 0");
        }
        isEnded = false;
        totalTime = time;
        timeLeft = time;

        initZones();
        setEveryoneToAdventure();
        createBossBar();
        createObjective();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().broadcastMessage(ChatColor.AQUA.toString() + "The scavenge hunt has begun! Get looking!");

        updateTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::update, 10, 10);
    }

    private void initZones() {
        plugin.zones = new ArrayList<>();
        final ConfigurationSection zonesSection = plugin.getConfig().getConfigurationSection(DATA_KEY_ZONES);

        zonesSection.getKeys(false).forEach(zone -> {
            final ConfigurationSection zoneSection = zonesSection.getConfigurationSection(zone);
            final int x1 = zoneSection.getInt(DATA_KEY_X1);
            final int y1 = zoneSection.getInt(DATA_KEY_Y1);
            final int z1 = zoneSection.getInt(DATA_KEY_Z1);
            final int x2 = zoneSection.getInt(DATA_KEY_X2);
            final int y2 = zoneSection.getInt(DATA_KEY_Y2);
            final int z2 = zoneSection.getInt(DATA_KEY_Z2);
            final String worldName = zoneSection.getString(DATA_KEY_WORLD);

            final World world = plugin.getServer().getWorlds()
                    .stream()
                    .filter(w -> w.getName().equals(worldName))
                    .findFirst()
                    .orElseThrow(() -> new ScavengeException("World not loaded: " + worldName));

            final int startx = Math.min(x1, x2);
            final int endx = Math.max(x1, x2);
            final int starty = Math.min(y1, y2);
            final int endy = Math.max(y1, y2);
            final int startz = Math.min(z1, z2);
            final int endz = Math.max(z1, z2);

            final List<Block> eggsInZone = new ArrayList<>();
            for (int x = startx; x <= endx; x++) {
                for (int y = starty; y <= endy; y++) {
                    for (int z = startz; z <= endz; z++) {
                        final Block block = world.getBlockAt(x, y, z);
                        if (block.getType().equals(plugin.getEggMaterial())) {
                            eggsInZone.add(block);
                        }
                    }
                }
            }

            System.out.println("eggsInZone.size() = " + eggsInZone.size());
            plugin.zones.add(new Zone(zone, eggsInZone, x1, y1, z1, x2, y2, z2));
        });

        System.out.println("zones = " + plugin.zones);
    }

    public void end() {
        if (isEnded) {
            throw new ScavengeException("Game already ended");
        }
        isEnded = true;

        if (plugin.getServer().getScheduler() != null) {
            plugin.getServer().getScheduler().cancelTask(updateTaskID);
        }
        HandlerList.unregisterAll(this);
        destroyBossBar();
        destroyObjective();
        plugin.zones = null;

        plugin.getServer().broadcastMessage(ChatColor.AQUA.toString() + "The scavenge hunt has ended!");
    }

    private void update() {
        updateTimer();
        updateBossBar();
        setEveryoneToAdventure();
    }

    private void setEveryoneToAdventure() {
        plugin.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(player -> !player.hasPermission("scavengehunt.admin"))
                .filter(player -> !player.getGameMode().equals(GameMode.ADVENTURE))
                .forEach(player -> player.setGameMode(GameMode.ADVENTURE));
    }

    private void createObjective() {
        final Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        objective = sc.registerNewObjective("eggsCollected", "dummy", "Collected", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        plugin.getServer().getOnlinePlayers()
                .forEach(player -> objective.getScore(player.getName()).setScore(0));
    }

    private void destroyObjective() {
        if (objective != null) {
            objective.unregister();
            objective = null;
        }
    }

    private void createBossBar() {
        bossBar = plugin.getServer().createBossBar(timeLeftString(), BarColor.PURPLE, BarStyle.SEGMENTED_20);
        bossBar.setVisible(true);
        plugin.getServer().getOnlinePlayers().forEach(bossBar::addPlayer);
    }

    private void updateBossBar() {
        if (bossBar == null) {
            throw new IllegalStateException("No bossbar");
        }
        bossBar.setTitle(timeLeftString());
        bossBar.setProgress((double) timeLeft / totalTime);
        bossBar.setVisible(true);
    }

    private void destroyBossBar() {
        if (bossBar == null) {
            throw new IllegalStateException("No bossbar");
        }
        bossBar.removeAll();
        bossBar.setVisible(false);
        bossBar = null;
    }

    private void updateTimer() {
        if (lastTime == null) {
            lastTime = System.currentTimeMillis();
        }
        final long now = System.currentTimeMillis();
        final long delta = now - lastTime;
        timeLeft -= delta;
        timeLeft = Math.max(timeLeft, 0);
        lastTime = now;
    }

    private String timeLeftString() {
        int seconds = (int) ((timeLeft / 1000) % 60);
        int minutes = (int) ((timeLeft / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (bossBar != null) {
            bossBar.addPlayer(event.getPlayer());
        }
        if (!objective.getScore(event.getPlayer().getName()).isScoreSet()) {
            objective.getScore(event.getPlayer().getName()).setScore(0);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            if (block.getType().equals(plugin.getEggMaterial())) {
                // see which zone this egg belongs to
                Zone zone = null;
                outer:
                for (Zone z : plugin.zones) {
                    for (Block e : z.getEggs()) {
                        if (block.equals(e)) {
                            zone = z;
                            break outer;
                        }
                    }
                }
                if (zone == null) {
                    event.getPlayer().sendMessage("Weird, this egg doesn't seem to belong to any zone! Just pretend it was never here, will you?");
                    block.breakNaturally();
                    return;
                }

                // found egg
                zone.addFound(block);
                plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + " found an egg at " + zone.getName() + "!");
                block.breakNaturally();
                playEggFoundSound();
                final Score score = objective.getScore(event.getPlayer().getName());
                score.setScore(score.getScore() + 1);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    private void playEggFoundSound() {
        plugin.getServer()
                .getOnlinePlayers()
                .forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 1, 1));
    }
}
