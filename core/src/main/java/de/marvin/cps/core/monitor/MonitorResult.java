package de.marvin.cps.core.monitor;

/**
 * {@link MonitorResult} of a monitor operation.
 */
public enum MonitorResult {

    /**
     * Indicates that the monitor operation was successful.
     */
    SUCCESS,

    /**
     * Indicates that the player is already monitoring someone.
     */
    ALREADY_MONITORING,

    /**
     * Indicates that the player is not currently monitoring anyone.
     */
    NOT_MONITORING,

    /**
     * Indicates that the user to be monitored was not found.
     */
    USER_NOT_FOUND,

    /**
     * Indicates that the monitor currently is paused.
     */
    IS_PAUSED,

    /**
     * Usually unreachable error state.
     */
    UNKNOWN_ERROR;

}