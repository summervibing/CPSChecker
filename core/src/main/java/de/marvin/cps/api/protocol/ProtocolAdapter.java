package de.marvin.cps.api.protocol;

import de.marvin.cps.core.protocol.MinecraftVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Interface which provides methods to interact with version-dependent protocol features of Minecraft.
 */
public interface ProtocolAdapter {

    /**
     * Sends a message to the action bar of the given {@link Player}.
     *
     * @param message Message to send to the action bar
     */
    void sendActionBarMessage(@NotNull Player bukkitPlayer, @NotNull String message);

    /**
     * Returns the {@link MinecraftVersion} of the current implementation.
     *
     * @return The {@link MinecraftVersion} of the current implementation
     */
    @NotNull MinecraftVersion version();

}
