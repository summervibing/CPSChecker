package de.marvin.cps.core.protocol;

import de.marvin.cps.api.protocol.ProtocolAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface ProtocolProvider {

    /**
     * Gets implementation instance of the given {@link Class<T>}
     * based on the detected {@link MinecraftVersion}.
     *
     * @param type class type to get implementation for
     * @param <T>  type of the class to get implementation for
     * @return Implementation instance of the given {@link Class<T>}.
     */
    <T> T get(@NotNull Class<T> type);

    /**
     * Gets the detected {@link MinecraftVersion} of the server.
     *
     * @return Detected {@link MinecraftVersion}.
     * @see Bukkit#getVersion()
     */
    MinecraftVersion version();

    /**
     * Gets the implementation {@link MinecraftVersion} of the current
     * {@link ProtocolAdapter} based on the detected version.
     *
     * @return The implementation {@link MinecraftVersion}.
     */
    MinecraftVersion implementationVersion();

    /**
     * Gets the latest {@link ProtocolAdapter} implementation
     * based on the detected {@link MinecraftVersion}.
     *
     * @return The {@link ProtocolAdapter} instance.
     */
    ProtocolAdapter protocolAdapter();

}
