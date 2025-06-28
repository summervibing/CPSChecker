package de.marvin.cps.core.click.pattern;

import de.marvin.cps.core.user.User;
import org.bukkit.ChatColor;

/**
 * Represents a single {@link Tick} of {@link User User}
 * activity, which can include multiple clicks and attacks.
 * <p>
 * Each {@link Tick} can have multiple {@link Tick#clicks()} and
 * {@link Tick#attacks()}, both counted separately. Also, a click
 * can be invalid (e.g., hitting a block), which is tracked
 * by {@link Tick#invalid()}.
 * </p>
 */
public class Tick {

    private int clicks = 0;
    private int invalid = 0;
    private int attacks = 0;

    /**
     * Creates a new {@link Tick} with no clicks.
     */
    public Tick() { }

    /**
     * Adds a click to this {@link Tick}.
     *
     * @param invalid {@code true} if the click is invalid
     *                            (e.g., hitting a block),
     */
    public void addClick(
            final boolean invalid
    ) {
        this.clicks++;
        if (invalid) this.invalid++;
    }

    /**
     * Adds an attack to this {@link Tick}.
     */
    public void addAttack() {
        this.attacks++;
    }

    /**
     * Gets the number of clicks in this {@link Tick}.
     *
     * @return Number of clicks in this {@link Tick}.
     */
    public int clicks() {
        return this.clicks(false);
    }

    /**
     * Gets the number of {@link Tick#clicks} in this {@link Tick}.
     *
     * @param includeInvalid if {@code true}, includes invalid clicks;
     *                       if {@code false}, only counts valid clicks.
     * @return Number of clicks in this {@link Tick}.
     */
    public int clicks(
            final boolean includeInvalid
    ) {
        if (includeInvalid) return this.clicks;
        return this.clicks - this.invalid;
    }

    /**
     * Gets invalid clicks in this {@link Tick}.
     *
     * @return How many {@link Tick#clicks} are invalid.
     */
    public int invalid() {
        return this.invalid;
    }

    /**
     * Gets the number of {@link Tick#attacks} in this {@link Tick}.
     *
     * @return Number of attacks in this {@link Tick}.
     */
    public int attacks() {
        return this.attacks;
    }

    /**
     * Gets the {@link Tick} as a {@link Character}.
     * <li>No click: ' '</li>
     * <li>Tick with click and no attack: 'C'</li>
     * <li>Tick with click and attack: 'A'</li>
     *
     * @return The character representation of this tick.
     */
    public char toChar() {
        if (this.clicks == 0) return ' ';
        return this.attacks > 0 ? 'A' : 'C';
    }

    /**
     * Gets the {@link Tick} as a formatted {@link String} of {@link Tick#toChar()}.
     *
     * <li>No click: '§r '</li>
     * <li>One click per tick: '§aC'</li>
     * <li>Two clicks per tick: '§eC'</li>
     * <li>Three or more clicks per tick: '§cC'</li>
     *
     * @return The colored character representation of this tick.
     * @see Tick#toChar()
     */
    public String toFormattedChar() {
        var click = this.toChar();

        var color = switch (this.clicks) {
            case 0 -> ChatColor.RESET;      // no clicks
            case 1 -> ChatColor.GREEN;      // one click per tick
            case 2 -> ChatColor.YELLOW;     // two clicks per tick
            default -> ChatColor.RED;       // three or more clicks per tick
        };

        return color.toString() + (this.invalid() > 0 ? ChatColor.STRIKETHROUGH : "") + click;
    }

    /**
     * Checks if this {@link Tick} is empty, meaning it has no clicks or attacks.
     *
     * @return {@code true} if this {@link Tick} has no clicks or attacks,
     *         {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.clicks == 0 && this.attacks == 0;
    }

}
