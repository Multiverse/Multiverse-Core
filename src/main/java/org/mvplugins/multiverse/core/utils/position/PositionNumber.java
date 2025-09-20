package org.mvplugins.multiverse.core.utils.position;

import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.exceptions.utils.position.PositionParseException;

/**
 * Represents a number that can be either absolute or relative (prefixed with '~').
 *
 * @since 5.3
 */
@ApiStatus.AvailableSince("5.3")
public sealed interface PositionNumber permits PositionNumber.Relative, PositionNumber.Absolute {

    /**
     * Creates an absolute PositionNumber. Absolute numbers represent fixed coordinates that will not be adjusted
     * based on any base value.
     *
     * @param value the absolute value
     * @return a PositionNumber representing the absolute value
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    static PositionNumber ofAbsolute(double value) {
        return new Absolute(value);
    }

    /**
     *  Creates a relative PositionNumber. Relative numbers are prefixed with '~' and represent an offset
     *  from a base value. For example, a relative PositionNumber of '~5' means 5 units more than the base value,
     *  while '~ -3' means 3 units less than the base value.
     *
     * @param value the relative offset value
     * @return a PositionNumber representing the relative offset
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    static PositionNumber ofRelative(double value) {
        return new Relative(value);
    }

    /**
     * Parses a PositionNumber from a string. The string can represent either an absolute number (e.g., "10.5")
     * or a relative number prefixed with '~' (e.g., "~5" or "~ -3"). If the string is "~" with no number,
     * it is treated as a relative value of 0.
     *
     * @param string the string to parse
     * @return a PositionNumber representing the parsed value.
     * @throws PositionParseException If the string format is invalid.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    static PositionNumber fromString(String string) throws PositionParseException {
        if (string.startsWith("~")) {
            if (string.length() == 1) {
                return new Relative(0);
            }
            return new Relative(tryParseDouble(string.substring(1)));
        }
        return new Absolute(tryParseDouble(string));
    }

    private static double tryParseDouble(String str) throws PositionParseException {
        return Try.of(() -> Double.parseDouble(str))
                .getOrElseThrow(throwable -> new PositionParseException("Invalid number: " + str));
    }

    /**
     * Calculates the effective value based on the base value.
     * For absolute PositionNumbers, this returns the stored value.
     * For relative PositionNumbers, this returns base + stored value.
     *
     * @param base the base value to use for relative calculations
     * @return the calculated effective value
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    double getValue(double base);

    /**
     * Checks if this PositionNumber is relative.
     *
     * @return true if this is a relative PositionNumber, false if absolute
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    boolean isRelative();

    /**
     * Checks if this PositionNumber is absolute.
     *
     * @return true if this is an absolute PositionNumber, false if relative
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    boolean isAbsolute();

    /**
     * Gets the raw stored value without any base adjustment.
     *
     * @return the raw value (for relative, this is the offset; for absolute, this is the fixed value)
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    double getRawValue();

    final class Relative implements PositionNumber {

        private final double value;

        Relative(double value) {
            this.value = value;
        }

        @Override
        public double getValue(double base) {
            return base + value;
        }

        @Override
        public boolean isRelative() {
            return true;
        }

        @Override
        public boolean isAbsolute() {
            return false;
        }

        @Override
        public double getRawValue() {
            return value;
        }

        @Override
        public String toString() {
            return "~" + value;
        }
    }

     final class Absolute implements PositionNumber {

        private final double value;

        Absolute(double value) {
        this.value = value;
        }

        @Override
        public double getValue(double base) {
        return value;
        }

        @Override
        public boolean isRelative() {
            return false;
        }

        @Override
        public boolean isAbsolute() {
            return true;
        }

        @Override
        public double getRawValue() {
        return value;
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }
    }
}
