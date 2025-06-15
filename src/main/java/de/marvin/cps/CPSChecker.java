package de.marvin.cps;

import de.marvin.cps.click.ClickHandler;
import de.marvin.cps.command.CPSCommand;
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

public class CPSChecker extends JavaPlugin {

    private static CPSChecker instance;

    private UserHandler userHandler;
    private ClickHandler clickHandler;
    private MonitorHandler monitorHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Caches messages from messages.yml
        Messages.initialize(this);

        // Initialize instances
        this.userHandler = new UserHandler();
        this.clickHandler = new ClickHandler(this.userHandler);
        this.monitorHandler = new MonitorHandler(this.userHandler);

        // Register listeners and commands
        this.registerListeners();
        this.registerCommands();

        this.updateTask();
    }

    @Override
    public void onDisable() {

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
     * Registers all commands.
     */
    private void registerCommands() {
        this.getCommand("cps").setExecutor(new CPSCommand(monitorHandler));
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
    private void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Gets the instance of the {@link CPSChecker} plugin.
     *
     * @return Instance of {@link CPSChecker}.
     */
    public static CPSChecker instance() {
        return instance;
    }

}
