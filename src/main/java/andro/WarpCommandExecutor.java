package andro;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

import static andro.ScavengeHunt.*;

public class WarpCommandExecutor implements CommandExecutor {

    private final ScavengeHunt plugin;

    public WarpCommandExecutor(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command has to be used by a player!");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        return warpToZone((Player) commandSender, args[0]);
    }

    private boolean warpToZone(Player commandSender, String zoneNameInput) {
        final Player player = commandSender;
        final Optional<ConfigurationSection> zoneToWarpTo = plugin.getZoneByName(zoneNameInput);

        zoneToWarpTo.ifPresent(zone -> {
            final String zoneWorldName = zone.getString(DATA_KEY_WORLD);
            final World world = player.getServer().getWorld(zoneWorldName);
            if (world == null) {
                player.sendMessage("No such world: " + zoneWorldName);
            } else {
                player.teleport(new Location(world, zone.getDouble(DATA_KEY_OX), zone.getDouble(DATA_KEY_OY), zone.getDouble(DATA_KEY_OZ)));
                plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " has teleported to " + zone.getName() + "!");
            }
        });
        if (!zoneToWarpTo.isPresent()) {
            player.sendMessage("No such zone: " + zoneNameInput);
        }
        return true;
    }
}
