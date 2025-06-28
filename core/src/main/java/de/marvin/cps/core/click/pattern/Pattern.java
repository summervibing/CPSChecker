package de.marvin.cps.core.click.pattern;

import de.marvin.cps.core.CPSChecker;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Represents a {@link Pattern} of clicks over a fixed time window.
 */
public class Pattern {

    private static int size = 100; // In ticks
    private static int displaySize = 40; // In ticks

    private final Tick[] ticks = new Tick[size];
    private int currentIndex = 0;

    /**
     * Creates a new {@link Pattern} with a fixed size of {@link Pattern#size} ticks.
     */
    public Pattern() {
        for (int i = 0; i < Pattern.size; i++)
            this.ticks[i] = new Tick();
    }

    /**
     * Configures the cached size and display size of {@link Pattern Patterns}.
     * <p>
     * If the given values are invalid, default values are used.
     *
     * @param size        Size of the pattern in ticks (default: 100).
     * @param displaySize Display size of the pattern in ticks (default: 40).
     */
    public static void configure(
            final int size,
            final int displaySize
    ) {
        if (size < 20 || size > 1200) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Invalid pattern size '" + size + "' given in configuration. "
                            + "Only values between 20-1200 are allowed. Using default size of "
                            + Pattern.size + " ticks."
            );
            return;
        }
        if (displaySize < 10 || displaySize > 60) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Invalid pattern display size '" + displaySize + "' given in configuration. "
                            + "Only values between 10-60 are allowed. Using default display size of "
                            + Pattern.displaySize + " ticks."
            );
            return;
        }
        if (displaySize > size) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Pattern display size '" + displaySize + "' given in configuration exceeds pattern cache size '"
                            + size + "'. " + "Using default display size of " + Pattern.displaySize + " ticks."
            );
            return;
        }

        // Update static fields
        Pattern.size = size;
        Pattern.displaySize = displaySize;
    }

    /**
     * Gets the set size of the {@link Pattern} in ticks.
     *
     * @return Size of the {@link Pattern} in ticks.
     */
    public static int size() {
        return Pattern.size;
    }

    /**
     * Gets the set display size of the {@link Pattern} in ticks.
     *
     * @return Display size of the {@link Pattern} in ticks.
     */
    public static int displaySize() {
        return Pattern.displaySize;
    }

    /**
     * Moves the {@link Pattern} window forward by one tick.
     * <p>
     * <b>Note:</b> Needs to be called every tick to update the pattern.
     */
    public void nextTick() {
        this.currentIndex = (this.currentIndex + 1) % Pattern.size;
        // Reset the current tick
        this.ticks[currentIndex] = new Tick();
    }

    /**
     * Adds a click to the current tick.
     *
     * @param invalid {@code true} if the click is invalid (e.g., hitting a block)
     */
    public void registerClick(
            final boolean invalid
    ) {
        this.ticks[this.currentIndex].addClick(invalid);
    }

    /**
     * Adds an attack to the current tick.
     */
    public void registerAttack() {
        this.ticks[this.currentIndex].addAttack();
    }

    /**
     * Gets this {@link Pattern} as a colored {@link String}.
     *
     * @return Colored {@link String}-representation of the {@link Pattern}.
     */
    public String history() {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < Pattern.displaySize; i++)
            stringBuilder.append(this.ticks[indexFrom(i)].toFormattedChar());
        return stringBuilder.toString();
    }

    /**
     * Gets this {@link Pattern} as a colored {@link String}.
     * <p>
     * Highlights consecutive clicks from at least 6 consecutive
     * ticks where clicks were detected.
     *
     * @return Colored {@link String}-representation of the {@link Pattern}
     * with streaks.
     */
    public String streak() {
        var prefix = new String[Pattern.displaySize];
        var suffix = new String[Pattern.displaySize];

        // Scan entire pattern to find streaks of consecutive clicks
        var pos = 0;
        while (pos < Pattern.size) {
            if (this.ticks[indexFrom(pos)].isEmpty()) {
                pos++;
                continue;
            }

            var start = pos;
            while (pos < Pattern.size && !this.ticks[indexFrom(pos)].isEmpty()) pos++;
            var length = pos - start;

            // Only highlight streaks with at least six ticks
            if (length >= 6) {
                var color = length >= 10 ? ChatColor.RED : ChatColor.YELLOW;
                if (start < Pattern.displaySize)
                    prefix[start] = color + Integer.toString(length) + "(";

                int end = start + length - 1;
                if (end < Pattern.displaySize)
                    suffix[end] = color + ")";
            }
        }

        // Build display
        var builder = new StringBuilder();
        for (int i = 0; i < Pattern.displaySize; i++) {
            if (prefix[i] != null) builder.append(prefix[i]);

            builder.append(this.ticks[indexFrom(i)].toFormattedChar());

            if (suffix[i] != null) builder.append(suffix[i]);
        }
        return builder.toString();
    }

    /**
     * Calculates the clicks per second in the
     * last 20 {@link Tick Ticks}.
     * <p>
     * If {@code onlyAttacks} is {@code true}, only counts attacks.
     *
     * @param onlyAttacks whether to count only attack clicks
     * @return the number of clicks per second in the last 20 ticks
     */
    public int clicksPerSecond(boolean onlyAttacks) {
        var totalClicks = 0;

        for (int i = 0; i < 20; i++) {
            var index = (this.currentIndex - i + Pattern.size) % Pattern.size;
            var tick = this.ticks[index];
            if (onlyAttacks) {
                totalClicks += tick.attacks();
                continue;
            }
            totalClicks += tick.clicks();
        }

        return totalClicks;
    }

    // Helper methods

    /**
     * Gets index of {@link Tick} in {@link Pattern#ticks}
     * based on the given position in the {@link Pattern}.
     *
     * @return Index in {@link Pattern#ticks} based on given position.
     */
    private int indexFrom(int position) {
        return (this.currentIndex - position + Pattern.size) % Pattern.size;
    }

}
