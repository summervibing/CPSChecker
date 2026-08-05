package de.marvin.cps.core.click.tick;

import de.marvin.cps.core.user.User;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single {@link AbstractTick Tick} of {@link User User} activity.
 * <p>
 * Each {@link AbstractTick Tick} can have multiple {@link AbstractTick#clicks()}.
 */
public abstract class AbstractTick {

    /**
     * Number of clicks in this {@link AbstractTick Tick}.
     */
    protected int clicks = 0;

    /**
     * Creates a new {@link AbstractTick Tick} with no clicks.
     */
    public AbstractTick() {
    }

    /**
     * Adds a click to this {@link AbstractTick Tick}.
     */
    public void addClick() {
        this.clicks++;
    }

    /**
     * Returns the number of clicks in this {@link AbstractTick Tick}.
     *
     * @return Number of clicks in this {@link AbstractTick Tick}
     */
    public int clicks() {
        return clicks;
    }

    /**
     * Returns the {@link AbstractTick Tick} as a {@link Character}.
     * <ul>
     *      <li>No click: ' '</li>
     *      <li>Tick with click and no attack: 'C'</li>
     *      <li>Tick with click and attack: 'A'</li>
     * </ul>
     *
     * @return The {@link Character} representation of this {@link AbstractTick Tick}
     */
    public abstract char toChar();

    /**
     * Returns the {@link AbstractTick Tick} as a formatted {@link String} of {@link AbstractTick#toChar()}.
     *
     * @return The colored {@link String} representation of this {@link AbstractTick Tick}
     * @see AbstractTick#color()
     * @see AbstractTick#extraFormatting()
     * @see AbstractTick#toChar()
     */
    public @NotNull String toFormattedChar() {
        var click = this.toChar();
        var color = this.color();
        return color + this.extraFormatting() + click;
    }

    /**
     * Delivers extra formatting for the {@link AbstractTick Tick}.
     *
     * @return Extra formatting for this {@link AbstractTick Tick}
     */
    protected @NotNull String extraFormatting() {
        return "";
    }

    /**
     * Returns the {@link ChatColor} of this {@link AbstractTick Tick} based on the number of clicks.
     * <ul>
     *      <li>No clicks: '§r'</li>
     *      <li>One click per tick: '§a'</li>
     *      <li>Two clicks per tick: '§e'</li>
     *      <li>Three or more clicks per tick: '§c'</li>
     * </ul>
     *
     * @return The color of this tick
     */
    private @NotNull ChatColor color() {
        return switch (this.clicks) {
            case 0 -> ChatColor.RESET;      // no clicks
            case 1 -> ChatColor.GREEN;      // one click per tick
            case 2 -> ChatColor.YELLOW;     // two clicks per tick
            default -> ChatColor.RED;       // three or more clicks per tick
        };
    }

    /**
     * Checks if this {@link AbstractTick Tick} is empty, meaning it has no clicks.
     *
     * @return {@code true} if this {@link AbstractTick Tick} has no clicks, {@code false} otherwise
     */
    public abstract boolean isEmpty();

}
