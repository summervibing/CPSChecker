package de.marvin.cps.util;

import de.marvin.cps.CPSChecker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    /**
     * Runs {@link Runnable} in primary thread if necessary.
     *
     * @param runnable task to be run
     */
    public static void sync(Runnable runnable) {
        if (!Bukkit.isPrimaryThread()) {
            scheduler().runTask(CPSChecker.instance(), runnable);
            return;
        }
        runnable.run();
    }

    /**
     * Runs {@link Runnable} asynchronous.
     *
     * @param runnable task to be run
     */
    public static void async(Runnable runnable) {
        scheduler().runTaskAsynchronously(CPSChecker.instance(), runnable);
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
    public static BukkitTask repeatSync(Runnable runnable, long delay, long period) {
        return scheduler().runTaskTimer(CPSChecker.instance(), runnable, delay, period);
    }

    /**
     * Runs repeating {@link Runnable} on main thread.
     *
     * @param runnable task to be run repeatedly
     * @param period   the ticks to wait between runs
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask repeatSync(Runnable runnable, long period) {
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
    public static BukkitTask repeatAsync(Runnable runnable, long delay, long period) {
        return scheduler().runTaskTimerAsynchronously(CPSChecker.instance(), runnable, delay, period);
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
    public static BukkitTask delaySync(Runnable runnable, long delay) {
        return scheduler().runTaskLater(CPSChecker.instance(), runnable, delay);
    }

    /**
     * Runs delayed {@link Runnable} on a new thread.
     *
     * @param runnable task to be run delayed
     * @param delay    the ticks to wait before starting the runnable
     * @return a {@link BukkitTask} that can be used to cancel the task
     */
    public static BukkitTask delayAsync(Runnable runnable, long delay) {
        return scheduler().runTaskLater(CPSChecker.instance(), runnable, delay);
    }

    /**
     * Cancels a scheduled task.
     *
     * @param task task to cancel
     */
    public static void cancel(BukkitTask task) {
        if (task == null) return;
        task.cancel();
    }

    /**
     * Cancels a scheduled task by its ID.
     *
     * @param id id of the task to cancel
     */
    public static void cancel(int id) {
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
