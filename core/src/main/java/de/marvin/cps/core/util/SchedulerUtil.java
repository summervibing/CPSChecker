package de.marvin.cps.core.util;

import de.marvin.cps.core.CPSChecker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchedulerUtil {

    /**
     * Runs {@link Runnable} in primary thread if necessary.
     *
     * @param runnable task to be run
     */
    public static void sync(
            @NotNull final Runnable runnable
    ) {
        if (!Bukkit.isPrimaryThread()) {
            scheduler().runTask(CPSChecker.instance().javaPlugin(), runnable);
            return;
        }
        runnable.run();
    }

    /**
     * Runs {@link Runnable} asynchronous.
     *
     * @param runnable task to be run
     */
    public static void async(
            @NotNull final Runnable runnable
    ) {
        scheduler().runTaskAsynchronously(CPSChecker.instance().javaPlugin(), runnable);
    }

    // Timer methods

    /**
     * Runs repeating {@link Runnable} on main thread.
     *
     * @param runnable task to be run repeatedly
     * @param delay    the ticks to wait before starting the runnable
     * @param period   the ticks to wait between runs
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatSync(
            @NotNull final Runnable runnable,
            final long delay,
            final long period
    ) {
        return scheduler().runTaskTimer(CPSChecker.instance().javaPlugin(), runnable, delay, period);
    }

    /**
     * Runs repeating {@link Runnable} on main thread.
     *
     * @param runnable task to be run repeatedly
     * @param period   the ticks to wait between runs
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatSync(
            @NotNull final Runnable runnable,
            final long period
    ) {
        return repeatSync(runnable, 0L, period);
    }

    /**
     * Runs repeating {@link Runnable} on a new thread.
     *
     * @param runnable task to be run repeatedly
     * @param delay    the ticks to wait before starting the runnable
     * @param period   the ticks to wait between runs
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatAsync(
            @NotNull final Runnable runnable,
            final long delay,
            final long period
    ) {
        return scheduler().runTaskTimerAsynchronously(CPSChecker.instance().javaPlugin(), runnable, delay, period);
    }

    /**
     * Runs repeating {@link Runnable} on a new thread.
     *
     * @param runnable task to be run repeatedly
     * @param period   the ticks to wait between runs
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatAsync(Runnable runnable, long period) {
        return repeatAsync(runnable, 0L, period);
    }

    // Delay methods

    /**
     * Runs delayed {@link Runnable} on main thread.
     *
     * @param runnable task to be run delayed
     * @param delay    the ticks to wait before starting the runnable
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask delaySync(
            @NotNull final Runnable runnable,
            final long delay
    ) {
        return scheduler().runTaskLater(CPSChecker.instance().javaPlugin(), runnable, delay);
    }

    /**
     * Runs delayed {@link Runnable} on a new thread.
     *
     * @param runnable task to be run delayed
     * @param delay    the ticks to wait before starting the runnable
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask delayAsync(
            @NotNull final Runnable runnable,
            final long delay
    ) {
        return scheduler().runTaskLater(CPSChecker.instance().javaPlugin(), runnable, delay);
    }

    /**
     * Cancels a scheduled task.
     *
     * @param task task to cancel
     */
    public static void cancel(
            @Nullable final BukkitTask task
    ) {
        if (task == null) return;
        task.cancel();
    }

    /**
     * Cancels a scheduled task by its ID.
     *
     * @param id id of the task to cancel
     */
    public static void cancel(
            final int id
    ) {
        if (id < 0) return;
        scheduler().cancelTask(id);
    }

    // Helper methods

    /**
     * Gets the scheduler for managing scheduled events.
     *
     * @return A scheduling service for this server.
     */
    private static BukkitScheduler scheduler() {
        return Bukkit.getScheduler();
    }

}
