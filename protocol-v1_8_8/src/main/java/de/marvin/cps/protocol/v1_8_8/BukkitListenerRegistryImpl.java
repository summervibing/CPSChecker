package de.marvin.cps.protocol.v1_8_8;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.protocol.BukkitListenerRegistry;
import de.marvin.cps.api.protocol.packetlistener.ClickListener;
import de.marvin.cps.protocol.v1_8_8.bukkitlistener.PlayerQuitListener;
import de.marvin.cps.protocol.v1_8_8.packetlistener.ClickListenerImpl;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Singleton
public class BukkitListenerRegistryImpl implements BukkitListenerRegistry {

    private final ClickListenerImpl clickListener;

    @Inject
    public BukkitListenerRegistryImpl(
            @NotNull final ClickListener clickListener
    ) {
        this.clickListener = (ClickListenerImpl) clickListener;
    }

    @Override
    public void registerListeners(@NotNull Consumer<Listener> registry) {
        registry.accept(new PlayerQuitListener(this.clickListener));
    }

}
