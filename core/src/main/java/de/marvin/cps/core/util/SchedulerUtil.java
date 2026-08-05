package de.marvin.cps.core.util;

import de.marvin.cps.core.CPSChecker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for scheduling tasks in Bukkit/Spigot.
 */
public final class SchedulerUtil {

    /**
     * Do not instantiate this class. It is a utility class and should only be used statically.
     */
    private SchedulerUtil() {
        throw new AssertionError("Utility classes cannot be instantiated.");
    }

    /**
     * Runs {@link Runnable} in primary thread if necessary.
     *
     * @param runnable {@link Runnable Task} to be run
     */
    public static void sync(
            @NotNull Runnable runnable
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
     * @param runnable {@link Runnable Task} to be run
     */
    public static void async(
            @NotNull Runnable runnable
    ) {
        scheduler().runTaskAsynchronously(CPSChecker.instance().javaPlugin(), runnable);
    }

    // Timer methods

    /**
     * Runs repeating {@link Runnable} on main thread.
     *
     * @param runnable {@link Runnable Task} to be run repeatedly
     * @param delay    The ticks to wait before starting the runnable
     * @param period   The ticks to wait between runs
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatSync(
            @NotNull Runnable runnable,
            long delay,
            long period
    ) {
        return scheduler().runTaskTimer(CPSChecker.instance().javaPlugin(), runnable, delay, period);
    }

    /**
     * Runs repeating {@link Runnable} on main thread.
     *
     * @param runnable {@link Runnable Task} to be run repeatedly
     * @param period   The ticks to wait between runs
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatSync(
            @NotNull Runnable runnable,
            long period
    ) {
        return repeatSync(runnable, 0L, period);
    }

    /**
     * Runs repeating {@link Runnable} on a new thread.
     *
     * @param runnable {@link Runnable Task} to be run repeatedly
     * @param delay    The ticks to wait before starting the runnable
     * @param period   The ticks to wait between runs
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatAsync(
            @NotNull Runnable runnable,
            long delay,
            long period
    ) {
        return scheduler().runTaskTimerAsynchronously(CPSChecker.instance().javaPlugin(), runnable, delay, period);
    }

    /**
     * Runs repeating {@link Runnable} on a new thread.
     *
     * @param runnable {@link Runnable Task} to be run repeatedly
     * @param period   The ticks to wait between runs
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatAsync(
            @NotNull Runnable runnable,
            long period
    ) {
        return repeatAsync(runnable, 0L, period);
    }

    // Delay methods

    /**
     * Runs delayed {@link Runnable} on main thread.
     *
     * @param runnable {@link Runnable Task} to be run delayed
     * @param delay    The ticks to wait before starting the runnable
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask delaySync(
            @NotNull Runnable runnable,
            long delay
    ) {
        return scheduler().runTaskLater(CPSChecker.instance().javaPlugin(), runnable, delay);
    }

    /**
     * Runs delayed {@link Runnable} on a new thread.
     *
     * @param runnable {@link Runnable Task} to be run delayed
     * @param delay    The ticks to wait before starting the runnable
     * @return A {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask delayAsync(
            @NotNull Runnable runnable,
            long delay
    ) {
        return scheduler().runTaskLater(CPSChecker.instance().javaPlugin(), runnable, delay);
    }

    /**
     * Cancels a scheduled {@link BukkitTask}.
     *
     * @param task {@link BukkitTask} to cancel
     */
    public static void cancel(
            @Nullable BukkitTask task
    ) {
        if (task == null) return;
        task.cancel();
    }

    /**
     * Cancels a scheduled task by its ID.
     *
     * @param id ID of the task to cancel
     */
    public static void cancel(
            int id
    ) {
        if (id < 0) return;
        scheduler().cancelTask(id);
    }

    // Helper methods

    /**
     * Returns the scheduler for managing scheduled events.
     *
     * @return A scheduling service for this server.
     * @see Bukkit#getScheduler()
     */
    private static BukkitScheduler scheduler() {
        return Bukkit.getScheduler();
    }

}
