package com.onarandombox.MultiverseCore.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utils class to compare bukkit server version. To be used for version specific feature.
 * Credits to EssentialsX ;)
 */
public class VersionUtils {

    public static final BukkitVersion v1_13_0_R01 = BukkitVersion.fromString("1.13.0-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_13_2_R01 = BukkitVersion.fromString("1.13.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_14_4_R01 = BukkitVersion.fromString("1.14.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_15_R01 = BukkitVersion.fromString("1.15-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_15_2_R01 = BukkitVersion.fromString("1.15.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_R01 = BukkitVersion.fromString("1.16-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_1_R01 = BukkitVersion.fromString("1.16.1-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_2_R01 = BukkitVersion.fromString("1.16.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_3_R01 = BukkitVersion.fromString("1.16.3-R0.1-SNAPSHOT");

    private static BukkitVersion serverVersion = null;
    private static BukkitVersion testServerVersion = v1_16_3_R01;

    public static BukkitVersion getServerVersion() {
        if (Bukkit.getName().equals("TestBukkit")) {
            return testServerVersion;
        }
        if (serverVersion == null) {
            serverVersion = BukkitVersion.fromString(Bukkit.getServer().getBukkitVersion());
        }
        return serverVersion;
    }

    public static void setTestServerVersion(String string) {
        testServerVersion = BukkitVersion.fromString(string);
    }

    public static boolean serverIsAtLeast(BukkitVersion o) {
        return getServerVersion().isHigherThanOrEquals(o);
    }

    public static boolean serverIsOlderThan(BukkitVersion o) {
        return getServerVersion().isLowerThan(o);
    }

    public static final class BukkitVersion implements Comparable<BukkitVersion> {
        private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.?([0-9]*)?(?:-pre(\\d))?(?:-?R?([\\d.]+))?(?:-SNAPSHOT)?");

        private final int major;
        private final int minor;
        private final int prerelease;
        private final int patch;
        private final double revision;

        private BukkitVersion(final int major, final int minor, final int patch, final double revision, final int prerelease) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
            this.prerelease = prerelease;
        }

        public static BukkitVersion fromString(String versionString) {
            if (versionString == null) {
                throw new IllegalArgumentException("Version string cannot be null!");
            }
            Matcher matcher = VERSION_PATTERN.matcher(versionString);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(versionString + " is not in valid version format. e.g. 1.8.8-R0.1");
            }
            return from(matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.groupCount() < 5 ? "" : matcher.group(5),
                    matcher.group(4));
        }

        private static BukkitVersion from(String major, String minor, String patch, String revision, String prerelease) {
            if (patch == null || patch.isEmpty()) {
                patch = "0";
            }
            if (revision == null || revision.isEmpty()) {
                revision = "0";
            }
            if (prerelease == null || prerelease.isEmpty()) {
                prerelease = "-1";
            }
            return new BukkitVersion(Integer.parseInt(major),
                    Integer.parseInt(minor),
                    Integer.parseInt(patch),
                    Double.parseDouble(revision),
                    Integer.parseInt(prerelease));
        }

        public boolean isHigherThan(final BukkitVersion o) {
            return compareTo(o) > 0;
        }

        public boolean isHigherThanOrEquals(final BukkitVersion o) {
            return compareTo(o) >= 0;
        }

        public boolean isLowerThan(final BukkitVersion o) {
            return compareTo(o) < 0;
        }

        public boolean isLowerThanOrEquals(final BukkitVersion o) {
            return compareTo(o) <= 0;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }

        public double getRevision() {
            return revision;
        }

        public int getPrerelease() {
            return prerelease;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final BukkitVersion that = (BukkitVersion) o;
            return major == that.major &&
                    minor == that.minor &&
                    patch == that.patch &&
                    revision == that.revision &&
                    prerelease == that.prerelease;
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor, patch, revision, prerelease);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(major + "." + minor);
            if (patch != 0) {
                sb.append(".").append(patch);
            }
            if (prerelease != -1) {
                sb.append("-pre").append(prerelease);
            }
            return sb.append("-R").append(revision).toString();
        }

        @Override
        public int compareTo(final BukkitVersion o) {
            if (major < o.major) {
                return -1;
            } else if (major > o.major) {
                return 1;
            }

            else if (minor < o.minor) {
                return -1;
            } else if (minor > o.minor) {
                return 1;
            }

            else if (patch < o.patch) {
                return -1;
            } else if (patch > o.patch) {
                return 1;
            }

            else if (prerelease < o.prerelease) {
                return -1;
            } else if (prerelease > o.prerelease) {
                return 1;
            }

            return Double.compare(revision, o.revision);
        }
    }
}
