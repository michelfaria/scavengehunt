package io.github.michelfaria.scavengehunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class CreateZoneCommandExecutor implements CommandExecutor {

    private final ScavengeHunt plugin;

    public CreateZoneCommandExecutor(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 11) {
            return false;
        }

        final int x1 = (int) Double.parseDouble(args[0]);
        final int y1 = (int) Double.parseDouble(args[1]);
        final int z1 = (int) Double.parseDouble(args[2]);
        final int x2 = (int) Double.parseDouble(args[3]);
        final int y2 = (int) Double.parseDouble(args[4]);
        final int z2 = (int) Double.parseDouble(args[5]);
        final int ox = (int) Double.parseDouble(args[6]);
        final int oy = (int) Double.parseDouble(args[7]);
        final int oz = (int) Double.parseDouble(args[8]);
        final String newZoneName = args[9];
        final String worldName = args[10];

        try {
            createZone(x1, y1, z1, x2, y2, z2, ox, oy, oz, newZoneName, worldName);
            sender.sendMessage("Zone created successfully!");
        } catch (ScavengeException ex) {
            sender.sendMessage(ex.getMessage());
        }
        return true;
    }

    private void createZone(float x1, float y1, float z1, float x2, float y2, float z2, float ox, float oy, float oz, String newZoneName, String worldName) {
        final ConfigurationSection zonesSection = Util.getSubsection(plugin.getConfig(), ScavengeHunt.DATA_KEY_ZONES);
        if (zonesSection.contains(newZoneName)) {
            throw new ScavengeException("Zone " + newZoneName + "already exists!");
        }

        final ConfigurationSection zoneSection = Util.getSubsection(zonesSection, newZoneName);
        zoneSection.set(ScavengeHunt.DATA_KEY_X1, x1);
        zoneSection.set(ScavengeHunt.DATA_KEY_Y1, y1);
        zoneSection.set(ScavengeHunt.DATA_KEY_Z1, z1);
        zoneSection.set(ScavengeHunt.DATA_KEY_X2, x2);
        zoneSection.set(ScavengeHunt.DATA_KEY_Y2, y2);
        zoneSection.set(ScavengeHunt.DATA_KEY_Z2, z2);
        zoneSection.set(ScavengeHunt.DATA_KEY_OX, ox);
        zoneSection.set(ScavengeHunt.DATA_KEY_OY, oy);
        zoneSection.set(ScavengeHunt.DATA_KEY_OZ, oz);
        zoneSection.set(ScavengeHunt.DATA_KEY_WORLD, worldName);
        plugin.saveConfig();
    }
}
