package de.marvin.cps.api.protocol;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Registry for Bukkit {@link Listener Listeners}.
 */
public interface BukkitListenerRegistry {

    /**
     * Registers given {@link Listener Listeners} to {@link PluginManager}.
     *
     * @param registry {@link Consumer} that accepts {@link Listener} instances
     *                 to register them to the {@link PluginManager}
     */
    void registerListeners(@NotNull Consumer<Listener> registry);

}
