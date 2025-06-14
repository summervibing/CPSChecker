package de.marvin.cps.click.pattern;

/**
 * Represents a {@link Pattern} of clicks over a fixed time window.
 */
public class Pattern {

    private static final int SIZE = 100; // 100 Ticks = 5 seconds
    public static final int DISPLAY_SIZE = 40; // Display only the last 40 ticks = 2 seconds
    private final Tick[] ticks = new Tick[SIZE];
    private int currentIndex = 0;

    /**
     * Creates a new {@link Pattern} with a fixed size of {@link Pattern#SIZE} ticks.
     */
    public Pattern() {
        for (int i = 0; i < SIZE; i++)
            this.ticks[i] = new Tick();
    }

    /**
     * Moves the {@link Pattern} window forward by one tick.
     * <p>
     * <b>Note:</b> Needs to be called every tick to update the pattern.
     */
    public void nextTick() {
        this.currentIndex = (this.currentIndex + 1) % SIZE;
        // Reset the current tick
        this.ticks[currentIndex] = new Tick();
    }

    /**
     * Adds a click to the current tick.
     *
     *  @param invalid {@code true} if the click is invalid (e.g., hitting a block)
     */
    public void registerClick(boolean invalid) {
        this.ticks[currentIndex].addClick(invalid);
    }

    /**
     * Adds an attack to the current tick.
     */
    public void registerAttack() {
        this.ticks[currentIndex].addAttack();
    }

    /**
     * Gets this {@link Pattern} as a colored {@link String}.
     *
     * @return Colored {@link String}-representation of the {@link Pattern}.
     */
    public String history() {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < DISPLAY_SIZE; i++)
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
        var builder = new StringBuilder();

        //TODO: Implement streak display logic

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
            var index = (this.currentIndex - i + SIZE) % SIZE;
            var tick = this.ticks[index];
            if (onlyAttacks) {
                totalClicks += tick.attacks();
                continue;
            }
            totalClicks += tick.clicks();
        }

        return totalClicks;
    }

    /**
     * Gets index of {@link Tick} in {@link Pattern#ticks}
     * based on the given position in the {@link Pattern}.
     *
     * @return Index in {@link Pattern#ticks} based on given position.
     */
    private int indexFrom(int position) {
        return (this.currentIndex - position + SIZE) % SIZE;
    }

}
