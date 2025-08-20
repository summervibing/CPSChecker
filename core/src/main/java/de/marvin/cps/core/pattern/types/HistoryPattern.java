package de.marvin.cps.core.pattern.types;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.pattern.AbstractPattern;
import org.jetbrains.annotations.NotNull;

public class HistoryPattern extends AbstractPattern {

    public HistoryPattern() {
        super(
                Message.MONITOR_HISTORY,
                "§aC = Click§7, §aA = Attack§7, §a§mC§r§a = Invalid click§7; §eC = 2 c/t§7, §cC = 3+ c/t"
        );
    }

    /**
     * Prints the current {@link AbstractPattern} as a colored
     * representation of the number of clicks per {@link AbstractTick}.
     *
     * @param ticks        Array of {@link AbstractTick} to print
     *                     the current pattern of
     * @param currentIndex Current index in the tick array
     * @return Colored string representation of the current {@link AbstractPattern}.
     */
    @Override
    public String print(
            @NotNull AbstractTick[] ticks,
            final int currentIndex
    ) {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < ClickSession.displaySize(); i++)
            stringBuilder.append(ticks[ClickSession.indexFrom(currentIndex, i)].toFormattedChar());
        return stringBuilder.toString();
    }

}
