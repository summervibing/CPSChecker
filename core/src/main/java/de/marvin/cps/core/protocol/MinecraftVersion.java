/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marvin.cps.core.protocol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    v1_8_8(47, true),
    v1_9(107, true), v1_9_1(108), v1_9_2(109), v1_9_4(110),
    // 1.10 and 1.10.1 have the same protocol version
    v1_10(210), v1_10_1(210), v1_10_2(210),
    v1_11(315), v1_11_2(316),
    v1_12(335), v1_12_1(338), v1_12_2(340),
    v1_13(393), v1_13_1(401), v1_13_2(404),
    v1_14(477), v1_14_1(480), v1_14_2(485), v1_14_3(490), v1_14_4(498),
    v1_15(573), v1_15_1(575), v1_15_2(578),
    v1_16(735), v1_16_1(736), v1_16_2(751), v1_16_3(753), v1_16_4(754), v1_16_5(754),
    v1_17(755), v1_17_1(756),
    v1_18(757), v1_18_1(757), v1_18_2(758),
    // 1.19.1 and 1.19.2 have the same protocol version
    v1_19(759), v1_19_1(760), v1_19_2(760), v1_19_3(761), v1_19_4(762),
    // 1.20 and 1.20.1 have the same protocol version; 1.20.3 and 1.20.4 have the same protocol version; 1.20.5 and 1.20.6 have the same protocol version
    v1_20(763), v1_20_1(763), v1_20_2(764), v1_20_3(765), v1_20_4(765), v1_20_5(766), v1_20_6(766),
    // 1.21 and 1.21.1 have the same protocol version; 1.21.2 and 1.21.3 have the same protocol version; 1.21.7 and 1.21.8 have the same protocol version; 1.21.9 and 1.21.10 have the same protocol version
    v1_21(767), v1_21_1(767), v1_21_2(768), v1_21_3(768), v1_21_4(769), v1_21_5(770), v_1_21_6(771), v_1_21_7(772), v_1_21_8(772), v_1_21_9(773), v_1_21_10(773), v_1_21_11(774),
    // 26.1.1 and 26.1.2 have the same protocol version
    v26_1_1(775), v26_1_2(775),
    v_26_2(776),
    //TODO UPDATE Add server version constant
    ERROR(-1, false, true);

    private static final @NotNull MinecraftVersion[] VALUES = values();
    private static final @NotNull MinecraftVersion[] REVERSED_VALUES;

    static {
        REVERSED_VALUES = values();
        for (int i = 0, j = REVERSED_VALUES.length - 1; i < j; i++, j--) {
            MinecraftVersion tmp = REVERSED_VALUES[j];
            REVERSED_VALUES[j] = REVERSED_VALUES[i];
            REVERSED_VALUES[i] = tmp;
        }
    }

    /**
     * The protocol version of this {@link MinecraftVersion}.
     */
    private final int protocolVersion;
    /**
     * The release name of this {@link MinecraftVersion}.
     */
    private final @NotNull String releaseName;
    /**
     * Whether the plugin has an implementation for this {@link MinecraftVersion}.
     */
    private final boolean isImplemented;

    MinecraftVersion(
            int protocolVersion
    ) {
        this.protocolVersion = protocolVersion;
        this.releaseName = this.name().substring(1).replace("_", ".");
        this.isImplemented = false;
    }

    MinecraftVersion(
            int protocolVersion,
            boolean isImplemented
    ) {
        this(protocolVersion, isImplemented, false);
    }

    MinecraftVersion(
            int protocolVersion,
            boolean isImplemented,
            boolean isNotRelease
    ) {
        this.protocolVersion = protocolVersion;
        this.isImplemented = isImplemented;
        if (isNotRelease) {
            this.releaseName = this.name();
        } else {
            this.releaseName = this.name().substring(1).replace("_", ".");
        }
    }

    /**
     * Returns the enum constants in reverse order.
     *
     * @return Array of {@link MinecraftVersion MinecraftVersions} in reverse order
     */
    public static @NotNull MinecraftVersion[] reversedValues() {
        return REVERSED_VALUES;
    }

    /**
     * Returns the latest protocol version.
     * This is the last enum constant in the ProtocolVersion enum.
     *
     * @return Latest {@link MinecraftVersion}
     */
    public static @NotNull MinecraftVersion latest() {
        return REVERSED_VALUES[1];
    }

    /**
     * Returns the oldest protocol version.
     * This is the first enum constant in the ProtocolVersion enum.
     *
     * @return Oldest {@link MinecraftVersion}
     */
    public static @NotNull MinecraftVersion oldest() {
        return VALUES[0];
    }

    //TODO Optimize
    @Deprecated
    public static @Nullable MinecraftVersion getById(
            int protocolVersion
    ) {
        for (MinecraftVersion version : VALUES) {
            if (version.protocolVersion == protocolVersion) {
                return version;
            }
        }
        return null;
    }

    /**
     * Returns the release name of this {@link MinecraftVersion}.
     * For example, for the V_1_18 enum constant, it would return "1.18".
     *
     * @return Release name of this {@link MinecraftVersion}
     */
    public @NotNull String releaseName() {
        return this.releaseName;
    }

    /**
     * Returns this {@link MinecraftVersion}'s protocol version.
     *
     * @return Protocol version of this {@link MinecraftVersion}
     */
    public int protocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Is this {@link MinecraftVersion} newer than the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is greater than the compared
     * server version's protocol version.
     *
     * @param target Compared {@link MinecraftVersion}
     * @return Whether this {@link MinecraftVersion} is newer than the compared {@link MinecraftVersion}
     */
    public boolean isNewerThan(
            @NotNull MinecraftVersion target
    ) {
        return this.ordinal() > target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} older than the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is less than
     * the compared server version's protocol version.
     *
     * @param target Compared {@link MinecraftVersion}
     * @return Whether this {@link MinecraftVersion} is older than the compared {@link MinecraftVersion}
     */
    public boolean isOlderThan(
            @NotNull MinecraftVersion target
    ) {
        return this.ordinal() < target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} newer than or equal to the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is greater than or
     * equal to the compared server version's protocol version.
     *
     * @param target Compared {@link MinecraftVersion}
     * @return Whether this {@link MinecraftVersion} is newer than or equal to the compared
     * {@link MinecraftVersion}
     */
    public boolean isNewerThanOrEquals(
            @NotNull MinecraftVersion target
    ) {
        return this.ordinal() >= target.ordinal();
    }

    /**
     * Is this {@link MinecraftVersion} older than or equal to the compared {@link MinecraftVersion}?
     * This method simply checks if this server version's protocol version is older than or equal to
     * the compared server version's protocol version.
     *
     * @param target Compared {@link MinecraftVersion}
     * @return Whether this {@link MinecraftVersion} is older than or equal to the compared
     * {@link MinecraftVersion}
     */
    public boolean isOlderThanOrEquals(
            @NotNull MinecraftVersion target
    ) {
        return this.ordinal() <= target.ordinal();
    }

    /**
     * Returns the latest implementation of the given {@link MinecraftVersion}.
     * If the given version is already implemented, it will return itself.
     * Otherwise, it will return the latest implemented version that is older
     * than the given version.
     * If no implementation is found, it will return {@link MinecraftVersion#ERROR}.
     *
     * @return The latest implemented {@link MinecraftVersion} that is older than or equal to the given
     * {@link MinecraftVersion}
     */
    public @NotNull MinecraftVersion latestImplementation() {
        // If the given version already is implemented, return it.
        if (this.isImplemented) return this;

        // Iterate through the reversed values to find the latest implementation
        for (var version : MinecraftVersion.REVERSED_VALUES)
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
     * @param comparison    The {@link VersionComparison Comparision Type}
     * @param targetVersion Compared {@link MinecraftVersion}
     * @return {@code true} if this {@link MinecraftVersion} is newer than, older than or equal to the
     * compared server version, depending on the comparison type, {@code false} otherwise
     * @see #isNewerThan(MinecraftVersion)
     * @see #isNewerThanOrEquals(MinecraftVersion)
     * @see #isOlderThan(MinecraftVersion)
     * @see #isOlderThanOrEquals(MinecraftVersion)
     */
    public boolean is(
            @NotNull VersionComparison comparison,
            @NotNull MinecraftVersion targetVersion
    ) {
        return switch (comparison) {
            case EQUALS -> this.protocolVersion == targetVersion.protocolVersion;
            case NEWER_THAN -> this.isNewerThan(targetVersion);
            case NEWER_THAN_OR_EQUALS -> this.isNewerThanOrEquals(targetVersion);
            case OLDER_THAN -> this.isOlderThan(targetVersion);
            case OLDER_THAN_OR_EQUALS -> this.isOlderThanOrEquals(targetVersion);
        };
    }

    /**
     * This enum contains all possible comparison types for {@link MinecraftVersion MinecraftVersions}.
     */
    public enum VersionComparison {
        /**
         * The {@link MinecraftVersion} equals the compared {@link MinecraftVersion}.
         */
        EQUALS,
        /**
         * The {@link MinecraftVersion} is newer than the compared {@link MinecraftVersion}.
         */
        NEWER_THAN,
        /**
         * The {@link MinecraftVersion} is newer than or equal to the compared {@link MinecraftVersion}.
         */
        NEWER_THAN_OR_EQUALS,
        /**
         * The {@link MinecraftVersion} is older than the compared {@link MinecraftVersion}.
         */
        OLDER_THAN,
        /**
         * The {@link MinecraftVersion} is older than or equal to the compared {@link MinecraftVersion}.
         */
        OLDER_THAN_OR_EQUALS;
    }

}
