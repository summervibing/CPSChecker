package de.marvin.cps.core.protocol;

import de.marvin.cps.api.protocol.ProtocolAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProtocolProvider {

    /**
     * Gets implementation instance of the given {@link Class<T>} based on the detected
     * {@link MinecraftVersion}.
     *
     * @param type Class type to get implementation for
     * @param <T>  Type of the class to get implementation for
     * @return Implementation instance of the given {@link Class<T>} or {@code null} if no implementation
     * is found
     */
    <T> @Nullable T get(@NotNull Class<T> type);

    /**
     * Gets the detected {@link MinecraftVersion} of the server.
     *
     * @return Detected {@link MinecraftVersion}.
     * @see Bukkit#getVersion()
     */
    @NotNull MinecraftVersion version();

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
