package de.marvin.cps.api.monitor;

import de.marvin.cps.core.monitor.Monitor;
import de.marvin.cps.core.monitor.MonitorMode;
import de.marvin.cps.core.monitor.MonitorResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Handles active player monitoring.
 */
public interface MonitorHandler {

    /**
     * Accesses {@link Monitor} of given {@link Player}.
     *
     * @param player {@link Player} to access monitor of
     * @return {@link Monitor} of the given {@link Player}
     * or {@code null} if the player is not monitoring anyone.
     */
    Monitor access(@NotNull Player player);

    /**
     * Checks if the given {@link Player} is
     * currently monitoring any user.
     *
     * @param player {@link Player} to check monitoring status for
     * @return {@code true} if the player is monitoring, otherwise {@code false}.
     */
    boolean isMonitoring(@NotNull Player player);

    /**
     * Retrieves all currently monitoring {@link Player Players}
     * and their associated {@link Monitor Monitors}.
     *
     * @return Unmodifiable {@link Map} of {@link Player Players}
     * and their {@link Monitor Monitors}.
     */
    Map<Player, Monitor> monitors();

    /**
     * Starts monitor for given {@link Player} of
     * the user with the given {@link UUID}.
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @return {@link MonitorResult} of the operation.
     */
    MonitorResult monitor(@NotNull Player player, @NotNull UUID uniqueId);

    /**
     * Starts monitor for given {@link Player} of
     * the user with the given {@link UUID}.
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @param mode {@link MonitorMode} to use for monitoring
     * @return {@link MonitorResult} of the operation.
     */
    MonitorResult monitor(@NotNull Player player, @NotNull UUID uniqueId, @NotNull MonitorMode mode);

    /**
     * Stops monitor for given {@link Player}.
     *
     * @param player {@link Player} to stop monitor for
     * @return {@link MonitorResult} of the operation.
     */
    MonitorResult stop(@NotNull Player player);

    /**
     * Switches to {@link Monitor#nextMode()} for
     * the given {@link Player}.
     *
     * @param player {@link Player} to switch {@link MonitorMode} for
     * @return {@link MonitorResult} of the operation.
     */
    MonitorResult nextMode(@NotNull Player player);

    /**
     * Updates the action bar of every currently monitoring player.
     */
    void update();

}
