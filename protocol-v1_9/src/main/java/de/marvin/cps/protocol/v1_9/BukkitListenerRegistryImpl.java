package de.marvin.cps.protocol.v1_9;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.protocol.BukkitListenerRegistry;
import de.marvin.cps.api.protocol.packetlistener.ClickListener;
import de.marvin.cps.protocol.v1_9.bukkitlistener.PlayerQuitListener;
import de.marvin.cps.protocol.v1_9.packetlistener.ClickListenerImpl;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Registry for Bukkit {@link Listener Listeners}.
 */
@Singleton
public class BukkitListenerRegistryImpl implements BukkitListenerRegistry {

    private final @NotNull ClickListenerImpl clickListener;

    @Inject
    public BukkitListenerRegistryImpl(
            @NotNull ClickListener clickListener
    ) {
        this.clickListener = (ClickListenerImpl) clickListener;
    }

    /**
     * {@inheritDoc}
     *
     * @param registry {@link Consumer} that accepts {@link Listener} instances to register them to the
     *                 {@link PluginManager}
     */
    @Override
    public void registerListeners(
            @NotNull Consumer<Listener> registry
    ) {
        registry.accept(new PlayerQuitListener(this.clickListener));
    }

}
