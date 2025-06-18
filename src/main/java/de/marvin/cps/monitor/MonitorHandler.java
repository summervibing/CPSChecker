package de.marvin.cps.monitor;

import de.marvin.cps.message.Message;
import de.marvin.cps.message.Messages;
import de.marvin.cps.user.UserHandler;
import de.marvin.cps.util.ActionBarUtil;
import de.marvin.cps.util.SchedulerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MonitorHandler {

    private final UserHandler userHandler;

    // Holds currently monitoring players and their monitors
    private final Map<Player, Monitor> monitoring = new HashMap<>();
    // Holds players that are switching between monitor modes
    private final Map<UUID, List<BukkitTask>> switching = new HashMap<>();

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
     * Switches to {@link Monitor#nextMode()} for
     * the given {@link Player}.
     *
     * @param player {@link Player} to switch {@link MonitorMode} for
     * @return {@link Result} of the operation.
     */
    public Result nextMode(
            @NotNull final Player player
    ) {
        var monitor = this.access(player);
        if (monitor == null) return Result.NOT_MONITORING;

        this.switching.computeIfPresent(player.getUniqueId(), (uuid, tasks) -> {
            tasks.forEach(BukkitTask::cancel);
            return null;
        });

        var next = monitor.nextMode();
        monitor.setPaused(true);

        ActionBarUtil.sendActionBarMessage(
                player,
                selection(next)
        );

        var tasks = new ArrayList<BukkitTask>();
        this.switching.put(player.getUniqueId(), tasks);

        tasks.add(SchedulerUtil.delayAsync(() -> ActionBarUtil.sendActionBarMessage(
                player,
                Messages.formatted(
                        monitor.mode().format(),
                        Map.of(
                                "player_name", "player",
                                "cps", "clicks",
                                "attack_cps", "attacks",
                                "pattern", monitor.mode().equals(MonitorMode.STREAK)
                                        ? "§aC = Click§7, §aA = Attack§7, §a§mC§r§a = Invalid click§7; §eStreakCount(§aCCCCCC§e)"
                                        : "§aC = Click§7, §aA = Attack§7, §a§mC§r§a = Invalid click§7; §eC = 2 c/t§7, §cC = 3+ c/t"
                        )
                )
        ), 20L));
        tasks.add(SchedulerUtil.delayAsync(() -> {
            monitor.setPaused(false);
            this.switching.remove(player.getUniqueId());
        }, 40L));

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

            // Stop monitor when player who monitors leaves
            if (!player.isOnline()) {
                iterator.remove();
                continue;
            }

            // Stop monitor when monitored user leaves
            if (!monitor.user().isOnline()) {
                iterator.remove();
                ActionBarUtil.sendActionBarMessage(player, Messages.formatted(
                        Message.MONITOR_PLAYER_LEFT,
                        Map.of("player_name", user.name())
                ));
                continue;
            }

            // Do not send update if the monitor is paused
            if (monitor.isPaused()) continue;

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

    /**
     * Gets a formatted selection of all {@link MonitorMode MonitorModes}.
     *
     * @param selected {@link MonitorMode} that is currently selected
     * @return Formatted selection of all {@link MonitorMode MonitorModes}.
     */
    private String selection(
            @NotNull final MonitorMode selected
    ) {
        var builder = new StringBuilder();
        for (var mode : MonitorMode.values()) {
            builder.append(mode.equals(selected)
                    ? "" + ChatColor.RED + ChatColor.UNDERLINE
                    : ChatColor.GRAY).append(mode.name());
            if (mode != MonitorMode.values()[MonitorMode.values().length - 1])
                builder.append(ChatColor.RESET)
                        .append(ChatColor.DARK_GRAY)
                        .append(" ┃ ")
                        .append(ChatColor.RESET);
        }
        return builder.toString();
    }

}
