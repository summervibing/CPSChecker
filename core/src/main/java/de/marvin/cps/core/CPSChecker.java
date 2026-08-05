package de.marvin.cps.core;

import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.config.SettingConfig;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.monitor.Monitor;
import de.marvin.cps.core.protocol.ProtocolProvider;
import de.marvin.cps.core.util.SchedulerUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link CPSChecker} class is the core of the CPSChecker plugin.
 */
public final class CPSChecker {

    /**
     * Singleton instance of the {@link CPSChecker} core.
     */
    private final static CPSChecker INSTANCE = new CPSChecker();
    /**
     * Flag to indicate whether the core has been initialized.
     */
    private boolean initialized = false;

    private JavaPlugin plugin;
    private Logger logger;

    private ProtocolProvider protocolProvider;

    private SettingConfig settingConfig;

    /**
     * Initializes the {@link CPSChecker} core and sets up all necessary components.
     *
     * @param plugin {@link JavaPlugin} instance to use for initialization
     */
    public void setup(
            @NotNull JavaPlugin plugin,
            @NotNull ProtocolProvider protocolProvider
    ) {
        // To prevent multiple initializations
        if (this.initialized) {
            this.log(Level.WARNING, "Core already is initialized.");
            return;
        }
        this.initialized = true;

        // Set JavaPlugin instance
        this.plugin = plugin;

        // Set logger
        this.logger = plugin.getLogger();

        // Set ProtocolProvider instance
        this.protocolProvider = protocolProvider;

        // Caches messages from messages.yml
        Messages.initialize(plugin);

        // Loads configured values from configuration.yml
        this.settingConfig = new SettingConfig(plugin);
        ClickSession.configure(
                this.settingConfig.patternSize(),
                this.settingConfig.patternDisplaySize()
        );

        // Starts update task
        this.updateTask();
    }

    /**
     * Asynchronous repeating {@link BukkitTask} to update {@link Monitor Monitors} and
     * {@link PatternType Patterns} on every server tick.
     */
    private void updateTask() {
        var monitorHandler = this.protocolProvider.get(MonitorHandler.class);
        var clickHandler = this.protocolProvider.get(ClickHandler.class);

        SchedulerUtil.repeatAsync(() -> {
            monitorHandler.update();
            clickHandler.update();
        }, 1L);
    }

    // Getter methods

    /**
     * Returns the instance of the {@link CPSChecker} core.
     *
     * @return Instance of {@link CPSChecker} core
     */
    public static @NotNull CPSChecker instance() {
        return CPSChecker.INSTANCE;
    }

    /**
     * Returns the {@link JavaPlugin} instance.
     *
     * @return Instance of {@link JavaPlugin}
     */
    public JavaPlugin javaPlugin() {
        if (this.plugin == null) throw new IllegalStateException("JavaPlugin is not initialized.");
        return this.plugin;
    }

    /**
     * Logs a message to the {@link Logger} with the specified {@link Level}.
     *
     * @param level   {@link Level} of the log message
     * @param message Message to log
     */
    public void log(
            @NotNull Level level,
            @NotNull String message
    ) {
        if (this.logger == null) throw new IllegalStateException("Core is not set up.");
        this.logger.log(level, message);
    }

}
