package de.marvin.cps;

import de.marvin.cps.click.ClickHandler;
import de.marvin.cps.click.pattern.Pattern;
import de.marvin.cps.command.CPSCommand;
import de.marvin.cps.command.CPSTabCompletion;
import de.marvin.cps.config.SettingConfig;
import de.marvin.cps.listener.PacketListener;
import de.marvin.cps.listener.PlayerConnectionListener;
import de.marvin.cps.message.Messages;
import de.marvin.cps.monitor.MonitorHandler;
import de.marvin.cps.user.UserHandler;
import de.marvin.cps.util.SchedulerUtil;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class CPSChecker extends JavaPlugin {

    private static CPSChecker instance;

    private UserHandler userHandler;
    private ClickHandler clickHandler;
    private MonitorHandler monitorHandler;

    private SettingConfig settingConfig;

    @Override
    public void onEnable() {
        CPSChecker.instance = this;

        // Caches messages from messages.yml
        Messages.initialize(this);

        // Loads configured values from configuration.yml
        this.settingConfig = new SettingConfig(this);
        Pattern.configure(
                this.settingConfig.patternSize(),
                this.settingConfig.patternDisplaySize()
        );

        // Initialize instances
        this.userHandler = new UserHandler();
        this.clickHandler = new ClickHandler(this.userHandler);
        this.monitorHandler = new MonitorHandler(this.userHandler);

        // Register listeners and commands
        this.registerListeners();
        this.registerCommands();

        this.updateTask();
    }

    /**
     * Registers all listeners.
     */
    private void registerListeners() {
        this.registerListener(new PlayerConnectionListener(
                this.userHandler
        ));

        new PacketListener(
                this,
                this.clickHandler,
                this.monitorHandler
        );
    }

    /**
     * Registers all {@link org.bukkit.command.Command Commands} and
     * their {@link org.bukkit.command.TabCompleter TabCompleters}.
     */
    private void registerCommands() {
        this.getCommand("cps").setExecutor(new CPSCommand(
                this.monitorHandler
        ));
        this.getCommand("cps").setTabCompleter(new CPSTabCompletion(
                this,
                this.monitorHandler
        ));
    }

    /**
     * Asynchronous repeating {@link BukkitTask} to update
     * {@link de.marvin.cps.monitor.Monitor Monitors} and
     * {@link de.marvin.cps.click.pattern.Pattern Patterns}
     * on every server tick.
     */
    private void updateTask() {
        SchedulerUtil.repeatAsync(() -> {
            this.monitorHandler.update();
            this.clickHandler.update();
        }, 1L);
    }

    // Helper methods

    /**
     * Registers a listener to the {@link PluginManager}.
     *
     * @param listener Listener to register
     */
    private void registerListener(
            @NotNull final Listener listener
    ) {
        this.getServer().getPluginManager().registerEvents(
                listener,
                this
        );
    }

    /**
     * Gets the instance of the {@link CPSChecker} plugin.
     *
     * @return Instance of {@link CPSChecker}.
     */
    public static CPSChecker instance() {
        return CPSChecker.instance;
    }

}
