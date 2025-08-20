package de.marvin.cps.core.click.tick.types;

import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.user.User;
import org.bukkit.ChatColor;

/**
 * Represents a single {@link LeftClickTick} of {@link User User}
 * activity which can include multiple {@link LeftClickTick#clicks} and
 * {@link LeftClickTick#attacks}.
 * <p>
 * Each {@link LeftClickTick} can have multiple {@link LeftClickTick#clicks}
 * and {@link LeftClickTick#attacks}, both counted separately. Also, a
 * click can be invalid (e.g., hitting a block) which is tracked by
 * {@link LeftClickTick#invalid}.
 * </p>
 */
public class LeftClickTick extends AbstractTick {

    /**
     * Number of invalid clicks in this {@link LeftClickTick}.
     */
    private int invalid = 0;

    /**
     * Number of ({@link org.bukkit.entity.Entity}) attacks in this {@link LeftClickTick}.
     */
    private int attacks = 0;

    /**
     * Adds an invalid click to this {@link LeftClickTick}.
     */
    public void addInvalidClick() {
        this.invalid++;
    }

    /**
     * Adds an attack to this {@link LeftClickTick}.
     */
    public void addAttack() {
        this.attacks++;
    }

    /**
     * {@inheritDoc}
     *
     * @return Number of clicks in this {@link AbstractTick}.
     */
    @Override
    public int clicks() {
        var clicks = this.clicks - this.invalid;
        return Math.max(0, clicks);
    }

    /**
     * Gets the number of ({@link org.bukkit.entity.Entity})
     * {@link LeftClickTick#attacks} in this {@link LeftClickTick}.
     *
     * @return Number of attacks in this {@link LeftClickTick}.
     */
    public int attacks() {
        return this.attacks;
    }

    /**
     * Gets the number of {@link LeftClickTick#invalid invalid clicks}
     * in this {@link LeftClickTick}.
     *
     * @return Number of invalid clicks in this {@link LeftClickTick}.
     */
    public int invalidClicks() {
        return this.invalid;
    }

    /**
     * {@inheritDoc}
     *
     * @return The {@link Character} representation of this {@link LeftClickTick}.
     */
    @Override
    public char toChar() {
        if (this.clicks == 0) return ' ';
        return this.attacks > 0 ? 'A' : 'C';
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link LeftClickTick} as {@link ChatColor#STRIKETHROUGH}
     * {@link LeftClickTick} if {@link LeftClickTick#invalid invalid clicks} are detected.
     */
    @Override
    protected String extraFormatting() {
        return this.invalid > 0 && this.clicks > 0 ? ChatColor.STRIKETHROUGH.toString() : "";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if this {@link LeftClickTick} has no {@link LeftClickTick#clicks}
     *          and {@link LeftClickTick#attacks},
     *         {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.clicks == 0 && this.attacks == 0;
    }
}
