package de.marvin.cps.message;

import de.marvin.cps.config.MessageConfig;

public enum Message {

    COMMAND_USAGE("command-usage", "§7Usage: §c/cps <username/uuid/off> [<mode>]"),
    ADMIN_USAGE("admin-usage", "§7Usage: §c/cps <username/uuid/off/list/stop> [<mode>/<username/uuid>]"),

    PLAYER_NOT_FOUND("player-not-found", "§cThe player was not found."),

    MONITORING_PLAYER("monitoring-player", "§7You are now monitoring §e%player% §7in §e%mode% §7mode."),
    MONITORING_OFF("monitoring-off", "§7You are no longer monitoring any player."),
    ALREADY_MONITORING("already-monitoring", "§cYou are already monitoring §e%player% §cin §e%mode% §cmode."),
    NOT_MONITORING("not-monitoring", "§cYou are currently not monitoring any player."),

    MONITOR_BASIC("monitor-basic", "§f%player_name% §8┃ §e%cps%§7/§e%attack_cps%"),
    MONITOR_HISTORY("monitor-history", "§f%player_name% §8┃ §e%cps%§7/§e%attack_cps% §8┃ §a%pattern%"),
    MONITOR_STREAK("monitor-streak", "§f%player_name% §8┃ §e%cps%§7/§e%attack_cps% §8┠ §a%pattern%"),
    MONITOR_PLAYER_LEFT("monitor-player-left", "§e%player_name% §cleft the server. Monitoring stopped."),

    ADMIN_CURRENT_MONITORS_HEADER("admin-current-monitors-header", "§7Currently §e%count% §7player(s) are monitoring:"),
    ADMIN_CURRENT_MONITOR("admin-current-monitor", " §8- §c%player% §7is monitoring §f%monitored%"),
    ADMIN_NO_CURRENT_MONITORS("admin-no-current-monitors", "§cNo players are currently monitoring anyone."),

    ADMIN_STOPPED_MONITOR("admin-stopped-monitor", "§cStopped monitor for §e%player%§c."),
    ADMIN_NOT_MONITORING("admin-not-monitoring", "§e%player% §cis currently not monitoring."),

    FLAG_CLICK_SPEED_LIMITER("flag-click-speed-limiter", "§e%player% §7failed click speed limiter (§f%cps% §7cps) [§f%latency% §7ms; §f%tps% §7tps]."),
    FLAG_CLICK_PATTERN("flag-click-pattern", "§e%player% §7failed click pattern (§f%pattern%§7) [§f%latency% §7ms; §f%tps% §7tps].");

    private final String path;
    private final String defaultMessage;

    Message(
            final String path,
            final String defaultMessage
    ) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Config path to the specific message that is stored in {@link MessageConfig}.
     * @return Configuration path of specific message.
     */
    public String path() {
        return this.path;
    }

    /**
     * Default message that is used if the message is not set in {@link MessageConfig}.
     * @return Default message of specific message.
     */
    public String defaultMessage() {
        return this.defaultMessage;
    }

}
