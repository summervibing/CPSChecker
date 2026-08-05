package de.marvin.cps.core.pattern.types;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.pattern.AbstractPattern;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a pattern that functions like the {@link HistoryPattern} but, additionally, highlights streaks
 * of consecutive clicks.
 * <p>
 * A streak is defined as a sequence of consecutive clicks without any empty ticks in between.
 * The streaks are highlighted with different colors based on their length:
 * <ul>
 *     <li>Streaks of 1 to 5 ticks with clicks are not highlighted.</li>
 *     <li>Streaks of 6 to 9 ticks with clicks are highlighted in yellow.</li>
 *     <li>Streaks of 10 or more ticks with clicks are highlighted in red.</li>
 * </ul>
 * <p>
 * <b>Note:</b> The streaks, at the moment, cannot be longer than the configured
 * {@link ClickSession#patternSize()}. This design choice is not intended and will be changed in the future
 * so that streaks of any length can be recorded.
 */
public class StreakPattern extends AbstractPattern {

    public StreakPattern() {
        super(
                Message.MONITOR_STREAK,
                "§aC = Click§7, §aA = Attack§7, §a§mC§r§a = Invalid click§7; §eStreakCount(§aCCCCCC§e)"
        );
    }

    /**
     * Prints the current {@link AbstractPattern} as a colored representation of the number of clicks per
     * {@link AbstractTick} and highlights streaks of consecutive clicks.
     *
     * @param ticks        Array of {@link AbstractTick} to print the current pattern of
     * @param currentIndex Current index in the tick array
     * @return Colored string representing the pattern with streaks highlighting consecutive clicks
     */
    @Override
    public @NotNull String print(
            @NotNull AbstractTick[] ticks,
            int currentIndex
    ) {
        var prefix = new String[ClickSession.displaySize()];
        var suffix = new String[ClickSession.displaySize()];

        // Scan entire pattern to find streaks of consecutive clicks
        var pos = 0;
        while (pos < ClickSession.patternSize()) {
            if (ticks[ClickSession.indexFrom(currentIndex, pos)].isEmpty()) {
                pos++;
                continue;
            }

            var start = pos;
            while (pos < ClickSession.patternSize() && !ticks[ClickSession.indexFrom(currentIndex, pos)].isEmpty())
                pos++;
            var length = pos - start;

            // Only highlight streaks with at least six ticks
            if (length >= 6) {
                var color = length >= 10 ? ChatColor.RED : ChatColor.YELLOW;
                if (start < ClickSession.displaySize())
                    prefix[start] = color + Integer.toString(length) + "(";

                int end = start + length - 1;
                if (end < ClickSession.displaySize())
                    suffix[end] = color + ")";
            }
        }

        // Build display
        var builder = new StringBuilder();
        for (int i = 0; i < ClickSession.displaySize(); i++) {
            if (prefix[i] != null) builder.append(prefix[i]);
            builder.append(ticks[ClickSession.indexFrom(currentIndex, i)].toFormattedChar());
            if (suffix[i] != null) builder.append(suffix[i]);
        }
        return builder.toString();
    }

}
