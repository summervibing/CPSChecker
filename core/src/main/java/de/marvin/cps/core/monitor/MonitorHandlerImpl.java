package de.marvin.cps.core.monitor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.api.protocol.ProtocolAdapter;
import de.marvin.cps.api.user.UserHandler;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.util.SchedulerUtil;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Singleton
public class MonitorHandlerImpl implements MonitorHandler {

    private final UserHandler userHandler;
    private final ProtocolAdapter protocolAdapter;

    // Holds currently monitoring players and their monitors
    private final Map<Player, Monitor> monitoring = new HashMap<>();
    // Holds players that are switching between monitor modes
    private final Map<UUID, List<BukkitTask>> switching = new HashMap<>();

    @Inject
    public MonitorHandlerImpl(
            @NotNull final UserHandler userHandler,
            @NotNull final ProtocolAdapter protocolAdapter
    ) {
        this.userHandler = userHandler;
        this.protocolAdapter = protocolAdapter;
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to access monitor of
     * @return {@link Monitor} of the given {@link Player}
     * or {@code null} if the player is not monitoring anyone.
     */
    @Override
    public Monitor access(
            @NotNull final Player player
    ) {
        return this.monitoring.get(player);
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to check monitoring status for
     * @return {@code true} if the player is monitoring, otherwise {@code false}.
     */
    @Override
    public boolean isMonitoring(
            @NotNull final Player player
    ) {
        return this.monitoring.containsKey(player);
    }

    /**
     * {@inheritDoc}
     *
     * @return Unmodifiable {@link Map} of {@link Player Players}
     * and their {@link Monitor Monitors}.
     */
    @Override
    public Map<Player, Monitor> monitors() {
        return Map.copyOf(this.monitoring);
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult monitor(
            @NotNull final Player player,
            @NotNull final UUID uniqueId
    ) {
        return this.monitor(player, uniqueId, null);
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @param patternType {@link PatternType} to use for monitoring
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult monitor(
            @NotNull final Player player,
            @NotNull final UUID uniqueId,
            @Nullable final PatternType patternType
    ) {
        return this.monitor(player, uniqueId, patternType, null);
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to start monitor for
     * @param uniqueId {@link UUID} of the user to monitor
     * @param patternType {@link PatternType} to use for monitoring
     * @param clickType {@link ClickType} to use for monitoring
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult monitor(
            @NotNull final Player player,
            @NotNull final UUID uniqueId,
            @Nullable final PatternType patternType,
            @Nullable final ClickType clickType
    ) {
        var user = this.userHandler.user(uniqueId);
        if (user == null) return MonitorResult.USER_NOT_FOUND;

        var monitor = this.access(player);
        if (monitor != null && monitor.user().uniqueId().equals(uniqueId)) {
            if (patternType == null) return MonitorResult.ALREADY_MONITORING;

            var patternTypeAlreadySet = monitor.patternType().equals(patternType);
            var clickTypeAlreadySet = monitor.clickType().equals(clickType);

            if (patternTypeAlreadySet && clickType == null
                    || patternTypeAlreadySet && clickTypeAlreadySet) return MonitorResult.ALREADY_MONITORING;

            if (!clickTypeAlreadySet)
                monitor.setClickType(clickType == null ? ClickType.LEFT_CLICK : clickType);

            if (!patternTypeAlreadySet) {
                monitor.setPatternType(patternType);
                this.displayModeExplanation(player, monitor);
            }
            return MonitorResult.SUCCESS;
        }

        monitor = new Monitor(
                user,
                clickType == null ? ClickType.LEFT_CLICK : clickType,
                patternType == null ? PatternType.BASIC : patternType
        );
        this.monitoring.put(player, monitor);
        this.displayModeExplanation(player, monitor);
        return MonitorResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to stop monitor for
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult stop(
            @NotNull final Player player
    ) {
        if (!this.monitoring.containsKey(player)) return MonitorResult.NOT_MONITORING;
        this.monitoring.remove(player);
        return MonitorResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to switch {@link ClickType} for
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult nextClickType(
            @NotNull final Player player
    ) {
        var monitor = this.access(player);
        if (monitor == null) return MonitorResult.NOT_MONITORING;
        if (monitor.isPaused()) return MonitorResult.IS_PAUSED;

        this.switching.computeIfPresent(player.getUniqueId(), (uuid, tasks) -> {
            tasks.forEach(BukkitTask::cancel);
            return null;
        });

        monitor.nextClickType();
        return MonitorResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     *
     * @param player {@link Player} to switch {@link PatternType} for
     * @return {@link MonitorResult} of the operation.
     */
    @Override
    public MonitorResult nextPatternType(
            @NotNull final Player player
    ) {
        var monitor = this.access(player);
        if (monitor == null) return MonitorResult.NOT_MONITORING;

        this.switching.computeIfPresent(player.getUniqueId(), (uuid, tasks) -> {
            tasks.forEach(BukkitTask::cancel);
            return null;
        });

        monitor.nextPatternType();
        this.displayModeExplanation(player, monitor);
        return MonitorResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                this.protocolAdapter.sendActionBarMessage(player, Messages.formatted(
                        Message.MONITOR_PLAYER_LEFT,
                        Map.of("player_name", user.name())
                ));
                continue;
            }

            // Do not send update if the monitor is paused
            if (monitor.isPaused()) continue;

            var clickSession = user.clickSession();

            var highlightLeftIfRequired = monitor.clickType().isLeftClick() ? "§n" : "";
            var highlightRightIfRequired = monitor.clickType().isRightClick() ? "§n" : "";

            this.protocolAdapter.sendActionBarMessage(
                    player,
                    Messages.formatted(
                            monitor.pattern().format(),
                            Map.of(
                                    "player_name", user.name(),
                                    "left_cps", highlightLeftIfRequired
                                            + clickSession.clicksPerSecond(ClickType.LEFT_CLICK) + ChatColor.RESET,
                                    "attack_cps", highlightLeftIfRequired
                                            + clickSession.clicksPerSecond(ClickType.ATTACK) + ChatColor.RESET,
                                    "right_cps", highlightRightIfRequired
                                            + clickSession.clicksPerSecond(ClickType.RIGHT_CLICK) + ChatColor.RESET,
                                    "placement_cps", highlightRightIfRequired
                                            + clickSession.clicksPerSecond(ClickType.PLACEMENT) + ChatColor.RESET,
                                    "pattern", monitor.printPattern()
                            )
                    )
            );
        }
    }

    // Helper methods

    /**
     * Displays the explanation of the current {@link PatternType}
     * in the action bar of the given {@link Player}.
     *
     * @param player {@link Player} to display the explanation to
     * @param monitor {@link Monitor} to display current mode of
     */
    private void displayModeExplanation(
            @NotNull final Player player,
            @NotNull final Monitor monitor
    ) {
        monitor.setPaused(true);

        // Delay by two ticks to ensure the pattern selection
        // menu is sent after the previous one
        SchedulerUtil.delayAsync(() -> {
            this.protocolAdapter.sendActionBarMessage(
                    player,
                    selection(monitor.patternType())
            );

            var tasks = new ArrayList<BukkitTask>();
            this.switching.put(player.getUniqueId(), tasks);

            tasks.add(SchedulerUtil.delayAsync(() -> this.protocolAdapter.sendActionBarMessage(
                    player,
                    Messages.formatted(
                            monitor.pattern().format(),
                            Map.of(
                                    "player_name", "player",
                                    "left_cps", "left_clicks",
                                    "attack_cps", "attacks",
                                    "right_cps", "right_clicks",
                                    "placement_cps", "placements",
                                    "pattern", monitor.pattern().explanation()
                            )
                    )
            ), 20L));

            tasks.add(SchedulerUtil.delayAsync(() -> {
                monitor.setPaused(false);
                this.switching.remove(player.getUniqueId());
            }, 40L));
        }, 2L);
    }

    /**
     * Gets a formatted selection of all {@link PatternType MonitorModes}.
     *
     * @param selected {@link PatternType} that is currently selected
     * @return Formatted selection of all {@link PatternType MonitorModes}.
     */
    private String selection(
            @NotNull final PatternType selected
    ) {
        var builder = new StringBuilder();
        for (var mode : PatternType.values()) {
            builder.append(mode.equals(selected)
                    ? "" + ChatColor.RED + ChatColor.UNDERLINE
                    : ChatColor.GRAY).append(mode.name());
            if (mode != PatternType.values()[PatternType.values().length - 1])
                builder.append(ChatColor.RESET)
                        .append(ChatColor.DARK_GRAY)
                        .append(" ┃ ")
                        .append(ChatColor.RESET);
        }
        return builder.toString();
    }

}
