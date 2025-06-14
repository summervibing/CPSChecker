package de.marvin.cps.monitor;

import de.marvin.cps.message.Message;
import de.marvin.cps.message.Messages;
import de.marvin.cps.user.UserHandler;
import de.marvin.cps.util.ActionBarUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MonitorHandler {

    private final UserHandler userHandler;

    private final Map<Player, Monitor> monitoring = new HashMap<>();

    public MonitorHandler(
            @NotNull final UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * Accesses {@link Monitor} of given {@link Player}.
     *
     * @param player {@link Player} to access monitor of
     * @return {@link Monitor} of the given {@link Player}
     * or {@code null} if the player is not monitoring anyone.
     */
    public Monitor access(
            @NotNull final Player player
    ) {
        return this.monitoring.get(player);
    }

    /**
     * Retrieves all currently monitoring {@link Player Players}
     * and their associated {@link Monitor Monitors}.
     *
     * @return Unmodifiable {@link Map} of {@link Player Players}
     * and their {@link Monitor Monitors}.
     */
    public Map<Player, Monitor> monitors() {
        return Map.copyOf(this.monitoring);
    }

    /**
     * Starts monitor for given {@link Player} of
     * the user with the given {@link UUID}.
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @return {@link Result} of the operation.
     */
    public Result monitor(
            @NotNull final Player player,
            @NotNull final UUID uniqueId
    ) {
        return this.monitor(player, uniqueId, MonitorMode.BASIC);
    }

    /**
     * Starts monitor for given {@link Player} of
     * the user with the given {@link UUID}.
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @param mode {@link MonitorMode} to use for monitoring
     * @return {@link Result} of the operation.
     */
    public Result monitor(
            @NotNull final Player player,
            @NotNull final UUID uniqueId,
            @NotNull final MonitorMode mode
    ) {
        var user = this.userHandler.user(uniqueId);
        if (user == null) return Result.USER_NOT_FOUND;

        var monitor = this.access(player);
        if (monitor != null && monitor.user().uniqueId().equals(uniqueId)) {
            if (monitor.mode().equals(mode)) return Result.ALREADY_MONITORING;
            monitor.setMode(mode);
            return Result.SUCCESS;
        }

        this.monitoring.put(player, new Monitor(user, mode));
        return Result.SUCCESS;
    }

    /**
     * Stops monitor for given {@link Player}.
     *
     * @param player {@link Player} to stop monitor for
     * @return {@link Result} of the operation.
     */
    public Result stop(
            @NotNull final Player player
    ) {
        if (!this.monitoring.containsKey(player)) return Result.NOT_MONITORING;
        this.monitoring.remove(player);
        return Result.SUCCESS;
    }

    /**
     * Updates the action bar of every
     * currently monitoring player.
     */
    public void update() {
        var iterator = this.monitoring.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var player = entry.getKey();
            var monitor = entry.getValue();
            var user = monitor.user();

            // Stop monitor when user leaves
            if (!monitor.user().isOnline()) {
                iterator.remove();
                ActionBarUtil.sendActionBarMessage(player, Messages.formatted(
                        Message.MONITOR_PLAYER_LEFT,
                        Map.of("player", user.name())
                ));
                continue;
            }

            var pattern = user.currentPattern();
            ActionBarUtil.sendActionBarMessage(
                    player,
                    Messages.formatted(
                            monitor.mode().format(),
                            Map.of(
                                    "player_name", user.name(),
                                    "cps", pattern.clicksPerSecond(false),
                                    "attack_cps", pattern.clicksPerSecond(true),
                                    "pattern", monitor.mode().equals(MonitorMode.STREAK) ? pattern.streak()
                                            : pattern.history()
                            )
                    )
            );
        }
    }

    /**
     * {@link Result Result} of a monitor operation.
     */
    public enum Result {
        SUCCESS,
        USER_NOT_FOUND,
        ALREADY_MONITORING,
        NOT_MONITORING
    }

}
