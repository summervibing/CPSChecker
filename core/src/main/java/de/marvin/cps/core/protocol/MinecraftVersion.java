package de.marvin.cps.core.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Server Version.
 * This is a nice wrapper over minecraft's protocol versions.
 * You won't have to memorize the protocol version, just memorize the server version you see in the launcher.
 *
 * @author retrooper
 * @see <a href="https://wiki.vg/Protocol_version_numbers">https://wiki.vg/Protocol_version_numbers</a>
 * @since 1.6.9
 */
public enum MinecraftVersion {

    v1_7_10(5),
    v1_8(47), v1_8_3(47), v1_8_8(47, true),
    v1_9(107), v1_9_1(108), v1_9_2(109), v1_9_4(110),
    //1.10 and 1.10.1 are redundant
    v1_10(210), v1_10_1(210), v1_10_2(210),
    v1_11(315), v1_11_2(316),
    v1_12(335), v1_12_1(338), v1_12_2(340),
    v1_13(393), v1_13_1(401), v1_13_2(404),
    v1_14(477), v1_14_1(480), v1_14_2(485), v1_14_3(490), v1_14_4(498),
    v1_15(573), v1_15_1(575), v1_15_2(578),
    v1_16(735), v1_16_1(736), v1_16_2(751), v1_16_3(753), v1_16_4(754), v1_16_5(754),
    v1_17(755), v1_17_1(756),
    v1_18(757), v1_18_1(757), v1_18_2(758),
    //1.19.1 and 1.19.2 have the same protocol version
    v1_19(759), v1_19_1(760), v1_19_2(760), v1_19_3(761), v1_19_4(762),
    //1.20 and 1.20.1 have the same protocol version. 1.20.3 and 1.20.4 have the same protocol version. 1.20.5 and 1.20.6 have the same protocol version
    v1_20(763), v1_20_1(763), v1_20_2(764), v1_20_3(765), v1_20_4(765), v1_20_5(766), v1_20_6(766),
    //1.21 and 1.21.1 have the same protocol version. 1.21.2 and 1.21.3 have the same protocol version
    v1_21(767, true), v1_21_1(767), v1_21_2(768), v1_21_3(768), v1_21_4(769), v1_21_5(770),
    //TODO UPDATE Add server version constant
    ERROR(-1, false, true);

    private static final MinecraftVersion[] VALUES = values();
    private static final MinecraftVersion[] REVERSED_VALUES;

    static {
        REVERSED_VALUES = values();
        for (int i = 0, j = REVERSED_VALUES.length - 1; i < j; i++, j--) {
            MinecraftVersion tmp = REVERSED_VALUES[j];
            REVERSED_VALUES[j] = REVERSED_VALUES[i];
            REVERSED_VALUES[i] = tmp;
        }
    }

    private final int protocolVersion;
    private final String releaseName;
    private final boolean isImplemented;

    MinecraftVersion(
            final int protocolVersion
    ) {
        this.protocolVersion = protocolVersion;
        this.releaseName = name().substring(1).replace("_", ".");
        isImplemented = false;
    }

    MinecraftVersion(
            final int protocolVersion,
            final boolean isImplemented
    ) {
        this(protocolVersion, isImplemented, false);
    }

    MinecraftVersion(
            final int protocolVersion,
            final boolean isImplemented,
            final boolean isNotRelease
    ) {
        this.protocolVersion = protocolVersion;
        this.isImplemented = isImplemented;
        if (isNotRelease) {
            this.releaseName = name();
        } else {
            this.releaseName = name().substring(1).replace("_", ".");
        }
    }


    public static MinecraftVersion[] reversedValues() {
        return REVERSED_VALUES;
    }

    /**
     * Gets the latest protocol version.
     * This is the last enum constant in the ProtocolVersion enum.
     *
     * @return Latest protocol version.
     */
    public static MinecraftVersion latest() {
        return REVERSED_VALUES[1];
    }

    /**
     * Gets the oldest protocol version.
     * This is the first enum constant in the ProtocolVersion enum.
     *
     * @return Oldest protocol version.
     */
    public static MinecraftVersion oldest() {
        return VALUES[0];
    }

    //TODO Optimize
    @Deprecated
    public static MinecraftVersion getById(
            final int protocolVersion
    ) {
        for (MinecraftVersion version : VALUES) {
            if (version.protocolVersion == protocolVersion) {
                return version;
            }
        }
        return null;
    }

    /**
     * Gets the release name of this {@link MinecraftVersion}.
     * For example, for the V_1_18 enum constant, it would return "1.18".
     *
     * @return Release name.
     */
    public String releaseName() {
        return releaseName;
    }

    /**
     * Gets this {@link MinecraftVersion}'s protocol version.
     *
     * @return Protocol version.
     */
    public int protocolVersion() {
        return protocolVersion;
    }

    /**
     * Is this {@link MinecraftVersion} newer than the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is greater than
     * the compared server version's protocol version.
     *
     * @param target compared {@link MinecraftVersion}
     * @return Is this {@link MinecraftVersion} newer than the compared {@link MinecraftVersion}.
     */
    public boolean isNewerThan(
            @NotNull final MinecraftVersion target
    ) {
        return this.ordinal() > target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} older than the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is less than
     * the compared server version's protocol version.
     *
     * @param target compared {@link MinecraftVersion}
     * @return Is this {@link MinecraftVersion} older than the compared {@link MinecraftVersion}.
     */
    public boolean isOlderThan(
            @NotNull final MinecraftVersion target
    ) {
        return this.ordinal() < target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} newer than or equal to the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is greater than or
     * equal to the compared server version's protocol version.
     *
     * @param target compared {@link MinecraftVersion}
     * @return Is this {@link MinecraftVersion} newer than or equal to the compared {@link MinecraftVersion}.
     */
    public boolean isNewerThanOrEquals(
            @NotNull final MinecraftVersion target
    ) {
        return this.ordinal() >= target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} older than or equal to the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is older than or equal to
     * the compared server version's protocol version.
     *
     * @param target compared {@link MinecraftVersion}
     * @return Is this {@link MinecraftVersion} older than or equal to the compared {@link MinecraftVersion}.
     */
    public boolean isOlderThanOrEquals(
            @NotNull final MinecraftVersion target
    ) {
        return this.ordinal() <= target.ordinal();
    }

    /**
     * Gets the latest implementation of the given {@link MinecraftVersion}.
     * If the given version is already implemented, it will return itself.
     * Otherwise, it will return the latest implemented version that is older
     * than the given version.
     * If no implementation is found, it will return {@link MinecraftVersion#ERROR}.
     *
     * @return The latest implementation of given {@link MinecraftVersion}.
     */
    public MinecraftVersion latestImplementation() {
        // If the given version already is implemented, return it.
        if (this.isImplemented) return this;

        // Iterate through the reversed values to find the latest implementation
        for (MinecraftVersion version : MinecraftVersion.REVERSED_VALUES)
            if (version.isImplemented && version.isOlderThanOrEquals(this))
                return version;

        // No implementation found
        return ERROR;
    }

    /**
     * Is this server version newer than, older than or equal to the compared server version?
     * This method simply checks if this server version's protocol version is greater than, less
     * than or equal to the compared server version's protocol version.
     *
     * @param comparison    Comparison type.
     * @param targetVersion Compared server version.
     * @return true or false, based on the comparison type.
     * @see #isNewerThan(MinecraftVersion)
     * @see #isNewerThanOrEquals(MinecraftVersion)
     * @see #isOlderThan(MinecraftVersion)
     * @see #isOlderThanOrEquals(MinecraftVersion)
     */
    public boolean is(
            @NotNull final VersionComparison comparison,
            @NotNull final MinecraftVersion targetVersion
    ) {
        return switch (comparison) {
            case EQUALS -> this.protocolVersion == targetVersion.protocolVersion;
            case NEWER_THAN -> isNewerThan(targetVersion);
            case NEWER_THAN_OR_EQUALS -> isNewerThanOrEquals(targetVersion);
            case OLDER_THAN -> isOlderThan(targetVersion);
            case OLDER_THAN_OR_EQUALS -> isOlderThanOrEquals(targetVersion);
        };
    }

    /**
     * This enum contains all possible comparison types for server versions.
     */
    public enum VersionComparison {
        /*
        The server version equals the compared server version.
         */
        EQUALS,
        /*
        The server version is newer than the compared server version.
         */
        NEWER_THAN,
        /*
        The server version is newer than or equal to the compared server version.
         */
        NEWER_THAN_OR_EQUALS,
        /*
        The server version is older than the compared server version.
         */
        OLDER_THAN,
        /*
        The server version is older than or equal to the compared server version.
         */
        OLDER_THAN_OR_EQUALS;
    }

}
