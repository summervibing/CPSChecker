package de.marvin.cps.core.click.tick.types;

import de.marvin.cps.core.click.tick.AbstractTick;

/**
 * Represents a single {@link RightClickTick} of user activity
 * which can include multiple {@link RightClickTick#clicks} and
 * {@link RightClickTick#placements}.
 */
public class RightClickTick extends AbstractTick {

    private int placements = 0;

    /**
     * Adds a placement to the {@link RightClickTick}.
     */
    public void addPlacement() {
        this.placements++;
    }

    /**
     * Gets the number of {@link RightClickTick#placements}
     * in this {@link RightClickTick}.
     *
     * @return Number of placements in this {@link RightClickTick}.
     */
    public int placements() {
        return this.placements;
    }

    /**
     * {@inheritDoc}
     *
     * @return The {@link Character} representation of this {@link RightClickTick}.
     */
    @Override
    public char toChar() {
        if (this.clicks == 0) return ' ';
        return this.placements > 0 ? 'P' : 'C';
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if this {@link RightClickTick} has no {@link RightClickTick#clicks}
     *          and {@link RightClickTick#placements},
     *         {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.clicks == 0 && this.placements == 0;
    }
}
