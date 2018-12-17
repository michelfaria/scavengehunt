package andro;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ScavengeHunt extends JavaPlugin {

    public static final String DATA_KEY_ZONES = "zones";
    public static final String DATA_KEY_WORLD = "world";
    public static final String DATA_KEY_X1 = "x1";
    public static final String DATA_KEY_Y1 = "y1";
    public static final String DATA_KEY_Z1 = "z1";
    public static final String DATA_KEY_X2 = "x2";
    public static final String DATA_KEY_Y2 = "y2";
    public static final String DATA_KEY_Z2 = "z2";
    public static final String DATA_KEY_OX = "ox";
    public static final String DATA_KEY_OY = "oy";
    public static final String DATA_KEY_OZ = "oz";
    public static final String DATA_KEY_BLOCK = "egg-material";
    public static final String DATA_FILE_PATH = "scavenge.yml";
    public static final String EGGS_COLLECTED_METADATA = "eggsCollected";

    private Game game;
    public List<Zone> zones;

    @Override
    public void onEnable() {
        super.onEnable();
        getLogger().info("Andro's Scavenge Hunt plugin loaded!");
        getCommand("createzone").setExecutor(new CreateZoneCommandExecutor(this));
        getCommand("warp").setExecutor(new WarpCommandExecutor(this));
        getCommand("zones").setExecutor(new ListZonesCommandExecutor(this));
        getCommand("scavstart").setExecutor(new StartCommandExecutor(this));
        getCommand("scavstop").setExecutor(new StopCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (isGameRunning()) {
            endGame();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        saveDefaultConfig();
    }

    public void startGame(long time) {
        if (isGameRunning()) {
            throw new ScavengeException("Game is already running");
        }
        game = new Game(this);
        game.start(time);
    }

    public void endGame() {
        if (!isGameRunning()) {
            throw new ScavengeException("Game is not running");
        }
        game.end();
        game = null;
    }

    public Set<String> listZones() {
        return Util.getSubsection(getConfig(), DATA_KEY_ZONES).getKeys(false);
    }

    public Optional<ConfigurationSection> getZoneByName(String name) {
        final ConfigurationSection zonesSection = Util.getSubsection(getConfig(), DATA_KEY_ZONES);
        return zonesSection.contains(name)
                ? Optional.of(zonesSection.getConfigurationSection(name))
                : Optional.empty();
    }

    public boolean isGameRunning() {
        return game != null;
    }

    public Material getEggMaterial() {
        return Material.SPAWNER;
    }
}
