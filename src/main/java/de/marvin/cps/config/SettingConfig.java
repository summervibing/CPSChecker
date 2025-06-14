package de.marvin.cps.config;

import org.bukkit.plugin.java.JavaPlugin;

public class SettingConfig extends AbstractConfig {

    public SettingConfig(
            final JavaPlugin plugin
    ) {
        super(plugin, "configuration.yml");
    }

    @Override
    public void loadDefaultValues() {

    }

}
