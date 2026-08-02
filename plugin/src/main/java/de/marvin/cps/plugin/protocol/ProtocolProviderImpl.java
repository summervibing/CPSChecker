package de.marvin.cps.plugin.protocol;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.marvin.cps.api.protocol.ProtocolAdapter;
import de.marvin.cps.core.CoreModule;
import de.marvin.cps.core.protocol.MinecraftVersion;
import de.marvin.cps.core.protocol.ProtocolProvider;
import de.marvin.cps.core.util.MaterialUtil;
import de.marvin.cps.plugin.command.CommandModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * The {@link ProtocolProviderImpl} class is responsible for detecting
 * the current Minecraft version of the server and providing
 * the appropriate {@link ProtocolAdapter} implementation based
 * on that version.
 */
public class ProtocolProviderImpl implements ProtocolProvider {

    private final JavaPlugin plugin;
    private final MinecraftVersion detectedVersion;
    private final MinecraftVersion implementationVersion;
    private final Injector injector;

    private final ProtocolAdapter protocolAdapter;

    /**
     * Constructs a new {@link ProtocolProviderImpl} instance.
     * It detects the current Minecraft version and initializes
     * the appropriate {@link ProtocolAdapter} implementation.
     *
     * @param plugin {@link JavaPlugin} instance
     */
    public ProtocolProviderImpl(
            @NotNull final JavaPlugin plugin
    ) {
        this.plugin = plugin;
        this.detectedVersion = this.detectMinecraftVersion();
        this.implementationVersion = this.detectedVersion.latestImplementation();

        // Check if the version is supported
        if (this.implementationVersion == null || this.implementationVersion == MinecraftVersion.ERROR) {
            this.injector = null;
            this.protocolAdapter = null;

            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "The server version %s does not seem to be supported. Disabling plugin..."
                            .formatted(this.detectedVersion.releaseName())
            );
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }

        // Load all instant breakable materials for the detected version
        MaterialUtil.load(this.detectedVersion);

        // Create Guice injector with CoreModule, CommandModule and version-dependent ProtocolModule
        this.injector = Guice.createInjector(
                new CoreModule(this.plugin),
                new CommandModule(),
                this.protocolModule(this.implementationVersion)
        );

        // Initialize ProtocolAdapter based on server version
        this.protocolAdapter = this.get(ProtocolAdapter.class);
        this.plugin.getLogger().log(
                Level.INFO,
                "Using protocol support %s for detected Minecraft version: %s"
                        .formatted(
                                this.protocolAdapter.version().releaseName(),
                                this.detectedVersion.releaseName()
                        )
        );
    }

    /**
     * {@inheritDoc}
     *
     * @param type class type to get implementation for
     * @param <T>  type of the class to get implementation for
     * @return Implementation instance of the given {@link Class<T>}.
     */
    public <T> T get(
            @NotNull final Class<T> type
    ) {
        return this.injector.getInstance(type);
    }

    /**
     * Gets {@link AbstractModule} for the detected {@link MinecraftVersion}.
     *
     * @return {@link AbstractModule} for the detected version.
     */
    private AbstractModule protocolModule(
            @NotNull final MinecraftVersion version
    ) {
        return switch (version) {
            case v1_8_8 -> new de.marvin.cps.protocol.v1_8_8.ProtocolModule();
            case v1_9 -> new de.marvin.cps.protocol.v1_9.ProtocolModule();
            default -> null;
        };
    }

    /**
     * Detects the current {@link MinecraftVersion} of the server
     * by comparing {@link Bukkit#getVersion()} with
     * {@link MinecraftVersion#releaseName()} of each known version.
     *
     * @return The detected {@link MinecraftVersion}.
     */
    private MinecraftVersion detectMinecraftVersion() {
        var current = this.plugin.getServer().getVersion();
        var fallback = MinecraftVersion.v1_8_8;
        if (current.toLowerCase().contains("unknown")) return fallback;
        for (MinecraftVersion version : MinecraftVersion.reversedValues())
            if (current.contains(version.releaseName())) return version;
        return fallback;
    }

    /**
     * {@inheritDoc}
     *
     * @return Detected {@link MinecraftVersion}.
     * @see Bukkit#getVersion()
     */
    public MinecraftVersion version() {
        return this.detectedVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @return The implementation {@link MinecraftVersion}.
     */
    public MinecraftVersion implementationVersion() {
        return this.implementationVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @return The {@link ProtocolAdapter} instance.
     */
    public ProtocolAdapter protocolAdapter() {
        return this.protocolAdapter;
    }

}
