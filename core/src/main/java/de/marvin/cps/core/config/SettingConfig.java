package de.marvin.cps.core.config;

import de.marvin.cps.core.pattern.PatternType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the configuration settings for this plugin.
 */
public class SettingConfig extends AbstractConfig {

    public SettingConfig(
            @NotNull JavaPlugin plugin
    ) {
        super(plugin, "configuration.yml");
    }

    /**
     * Returns the cache size for {@link PatternType Patterns} in ticks.
     *
     * @return Cache size for {@link PatternType Patterns} in ticks
     */
    public int patternSize() {
        return this.file().getInt("pattern.size");
    }

    /**
     * Returns the display size for {@link PatternType Patterns} in ticks.
     *
     * @return Display size for {@link PatternType Patterns} in ticks
     */
    public int patternDisplaySize() {
        return this.file().getInt("pattern.display-size");
    }

    /**
     * Loads default values into the configuration file if they are not already present.
     */
    @Override
    public void loadDefaultValues() {
        this.file().addDefault("pattern.size", 100);
        this.file().addDefault("pattern.display-size", 40);
        this.file().options().copyDefaults(true);
    }

}
