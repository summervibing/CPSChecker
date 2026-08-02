package de.marvin.cps.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import de.marvin.cps.api.protocol.BukkitListenerRegistry;
import de.marvin.cps.api.protocol.packetlistener.ClickListener;
import de.marvin.cps.api.protocol.packetlistener.MonitorListener;
import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.core.protocol.ProtocolProvider;
import de.marvin.cps.plugin.command.CPSCommand;
import de.marvin.cps.plugin.listener.PlayerConnectionListener;
import de.marvin.cps.plugin.protocol.ProtocolProviderImpl;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Entrypoint of the plugin. It initializes the plugin core and
 * version-dependent, registers listeners, and commands.
 */
public class CPSCheckerPluginLoader extends JavaPlugin {

    private static CPSCheckerPluginLoader loader;

    private ProtocolProvider protocolProvider;

    @Override
    public void onEnable() {
        CPSCheckerPluginLoader.loader = this;

        // Initialize protocol support
        this.protocolProvider = new ProtocolProviderImpl(this);

        // Initialize core
        CPSChecker.instance().setup(this, this.protocolProvider);

        // Register listeners and commands
        this.registerListeners();
        this.registerCommands();
    }

    /**
     * Registers all {@link Listener Listeners}.
     */
    private void registerListeners() {
        Consumer<Listener> registry = (listener) -> this.getServer().getPluginManager().registerEvents(
                listener,
                this
        );

        // Main listeners
        registry.accept(this.protocolProvider.get(PlayerConnectionListener.class));

        // Version-dependent listeners
        this.protocolProvider.get(BukkitListenerRegistry.class).registerListeners(registry);

        // Protocol listeners
        var protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(this.protocolProvider.get(ClickListener.class));
        protocolManager.addPacketListener(this.protocolProvider.get(MonitorListener.class));
    }

    /**
     * Registers all {@link org.bukkit.command.Command Commands} and
     * their {@link org.bukkit.command.TabCompleter TabCompleters}.
     */
    private void registerCommands() {
        this.registerCommand(
                "cps",
                this.protocolProvider.get(CPSCommand.class),
                this.protocolProvider.get(CPSCommand.class)
        );
    }

    /**
     * Gets the instance of the {@link CPSCheckerPluginLoader}.
     *
     * @return Instance of {@link CPSCheckerPluginLoader}.
     */
    public static CPSCheckerPluginLoader loader() {
        return CPSCheckerPluginLoader.loader;
    }

    // Helper methods

    /**
     * Registers a {@link CommandExecutor} and optionally
     * a {@link TabCompleter} to the given command.
     *
     * @param commandName          name of the command
     * @param commandInstance      command executor instance
     * @param tabCompleterInstance tab completer instance
     */
    private void registerCommand(
            @NotNull final String commandName,
            @NotNull final CommandExecutor commandInstance,
            @Nullable final TabCompleter tabCompleterInstance
    ) {
        // In case a command somehow is not defined in plugin.yml
        var command = this.getCommand(commandName);
        if (command == null) {
            this.getLogger().log(Level.SEVERE,
                    "Command '%s' is not defined in plugin.yml. Disabling plugin...".formatted(commandName)
            );
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set command executor
        command.setExecutor(commandInstance);

        // Set tab completer if provided
        if (tabCompleterInstance == null) return;
        command.setTabCompleter(tabCompleterInstance);
    }

}
