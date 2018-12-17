package io.github.michelfaria.scavengehunt;

import org.bukkit.configuration.ConfigurationSection;

public final class Util {
    public static ConfigurationSection getSubsection(ConfigurationSection section, String key) {
        return section.contains(key)
                ? section.getConfigurationSection(key)
                : section.createSection(key);
    }
}
