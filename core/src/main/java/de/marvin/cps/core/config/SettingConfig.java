package de.marvin.cps.core.config;

import org.bukkit.plugin.java.JavaPlugin;
import de.marvin.cps.core.pattern.PatternType;
import org.jetbrains.annotations.NotNull;

public class SettingConfig extends AbstractConfig {

    public SettingConfig(
            @NotNull final JavaPlugin plugin
    ) {
        super(plugin, "configuration.yml");
    }

    /**
     * Gets the cache size for {@link PatternType Patterns} in ticks.
     *
     * @return Cache size for {@link PatternType Patterns} in ticks.
     */
    public int patternSize() {
        return this.file().getInt("pattern.size");
    }

    /**
     * Gets the display size for {@link PatternType Patterns} in ticks.
     *
     * @return Display size for {@link PatternType Patterns} in ticks.
     */
    public int patternDisplaySize() {
        return this.file().getInt("pattern.display-size");
    }

    /**
     * Loads default values into the configuration file
     * if they are not already present.
     */
    @Override
    public void loadDefaultValues() {
        this.file().addDefault("pattern.size", 100);
        this.file().addDefault("pattern.display-size", 40);
        this.file().options().copyDefaults(true);
    }

}
