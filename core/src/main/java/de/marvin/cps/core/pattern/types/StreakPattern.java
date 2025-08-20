package de.marvin.cps.core.pattern.types;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.pattern.AbstractPattern;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class StreakPattern extends AbstractPattern {

    public StreakPattern() {
        super(
                Message.MONITOR_STREAK,
                "§aC = Click§7, §aA = Attack§7, §a§mC§r§a = Invalid click§7; §eStreakCount(§aCCCCCC§e)"
        );
    }

    /**
     * Prints the current {@link AbstractPattern} as a colored
     * representation of the number of clicks per
     * {@link AbstractTick} and highlights streaks of consecutive clicks.
     *
     * @param ticks        Array of {@link AbstractTick} to print
     *                     the current pattern of
     * @param currentIndex Current index in the tick array
     * @return Colored string representing the pattern with streaks
     * highlighting consecutive clicks.
     */
    @Override
    public String print(
            @NotNull AbstractTick[] ticks,
            final int currentIndex
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
