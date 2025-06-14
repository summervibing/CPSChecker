package de.marvin.cps.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class AbstractConfig {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration configuration;

    public AbstractConfig(
            final JavaPlugin plugin,
            final String fileName
    ) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        this.load();
    }

    /**
     * Loads the configuration file.
     */
    private void load() {
        if (!this.file.exists()) {
            this.loadDefaultValues();
            this.save();
        } else {
            try {
                this.configuration.load(this.file);
            } catch (IOException | InvalidConfigurationException exception) {
                this.plugin.getLogger().log(
                        Level.SEVERE,
                        "Could not load configuration file: " + this.file.getName(),
                        exception
                );
            }
        }
    }

    /**
     * Saves the configuration file.
     */
    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException exception) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Could not save configuration file: " + this.file.getName(),
                    exception
            );
        }
    }

    /**
     * Gets the file configuration.
     *
     * @return The file configuration.
     */
    public FileConfiguration file() {
        return this.configuration;
    }

    /**
     * Loads default values into the configuration file.
     */
    public abstract void loadDefaultValues();

}
