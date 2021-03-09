package com.onarandombox.MultiverseCore.commandtools.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the value result parsed from command arguments.
 */
public class FlagResult {

    /**
     * Parse arguments into its flag key and values.
     *
     * @param args      The arguments to parse.
     * @param flagGroup The flags available to parse into.
     * @return The {@link FlagResult} from the parse.
     * @throws FlagParseFailedException there is an issue with parsing flags from string arguments.
     */
    public static FlagResult parse(@Nullable String[] args,
                                   @NotNull FlagGroup flagGroup) throws FlagParseFailedException {

        FlagResult flagResult = new FlagResult();

        // No args to parse.
        if (args == null || args.length <= 0) {
            return flagResult;
        }

        // First arg must be a flag.
        CommandFlag<?> currentFlag = flagGroup.getByKey(args[0]);
        boolean completed = false;

        // Parse the arguments.
        for (int i = 1, argsLength = args.length; i <= argsLength; i++) {
            if (currentFlag == null) {
                throw new FlagParseFailedException("%s is not a valid flag.", args[i-1]);
            }
            // This ensures that flag is not null during final parse.
            if (i >= argsLength) {
                break;
            }

            String arg = args[i];
            if (arg == null) {
                throw new FlagParseFailedException("Arguments cannot be null!");
            }

            CommandFlag<?> nextFlag = flagGroup.getByKey(arg);

            // Arg must be a flag key.
            if (currentFlag instanceof NoValueCommandFlag) {
                flagResult.add(currentFlag, currentFlag.getValue(), false);
                currentFlag = nextFlag;
                continue;
            }

            // Arg can be a flag key or value.
            if (currentFlag instanceof OptionalCommandFlag) {
                if (nextFlag != null) {
                    // It's a key.
                    flagResult.add(currentFlag, currentFlag.getValue(), false);
                    currentFlag = nextFlag;
                    continue;
                }
                // It's a value.
                flagResult.add(currentFlag, currentFlag.getValue(arg), true);
                if (i == argsLength - 1) {
                    completed = true;
                    break;
                }
                currentFlag = flagGroup.getByKey(args[++i]);
                continue;
            }

            // Arg must be a flag value.
            if (currentFlag instanceof RequiredCommandFlag) {
                if (nextFlag != null) {
                    // It's a key, error!
                    throw new FlagParseFailedException("%s flag '%s' requires a value input.",
                            currentFlag.getName(), currentFlag.getIdentifier());
                }
                // It's a value.
                flagResult.add(currentFlag, currentFlag.getValue(arg), true);
                if (i == argsLength - 1) {
                    completed = true;
                    break;
                }
                currentFlag = flagGroup.getByKey(args[++i]);
            }
        }

        // Parse last flag.
        if (!completed) {
            if (currentFlag instanceof RequiredCommandFlag) {
                throw new FlagParseFailedException("%s flag '%s' requires a value input.",
                        currentFlag.getName(), currentFlag.getIdentifier());
            }
            flagResult.add(currentFlag, currentFlag.getValue(), false);
        }

        return flagResult;
    }

    private final Map<CommandFlag<?>, SingleFlagResult<?>> resultMap;

    private FlagResult() {
        resultMap = new HashMap<>();
    }

    /**
     * Add a new value result from parsing arguments.
     *
     * @param flag      The flag that the value represents.
     * @param value     The value of the flag.
     * @param fromInput Denotes if flag was parsed by a user input.
     */
    private void add(CommandFlag<?> flag, Object value, boolean fromInput) {
        resultMap.put(flag, new SingleFlagResult<>(value, fromInput));
    }

    /**
     * Gets value of a flag.
     *
     * @param flag  The flag to get value from.
     * @param <T>   The type of value.
     * @return The value which is associated with the flag.
     */
    public <T> T getValue(CommandFlag<T> flag) {
        SingleFlagResult<?> result = resultMap.get(flag);
        if (result == null) {
            return flag.getDefaultValue();
        }
        return (T) result.value;
    }

    /**
     * Gets if the flag value is by a user input.
     *
     * @param flag  The flag to check against.
     * @return True if value is by user input, else false.
     */
    public boolean isByUserInput(CommandFlag<?> flag) {
        SingleFlagResult<?> result = resultMap.get(flag);
        if (result == null) {
            return false;
        }
        return result.isFromUserInput;
    }

    /**
     * Gets if the flag is a default value, and key was not present in user's command arguments.
     * i.e. Not in the {@link #resultMap}.
     *
     * @param flag  The flag to check against.
     * @return True if flag was not present in user's command arguments, else false.
     */
    public boolean isDefaulted(CommandFlag<?> flag) {
        return resultMap.get(flag) != null;
    }

    @Override
    public String toString() {
        return "FlagResult{" +
                "resultMap=" + resultMap +
                '}';
    }

    /**
     * Represents a single value result parsed.
     * Stores value and addition data such as if value is by user input.
     *
     * @param <T>   The type of value.
     */
    private static class SingleFlagResult<T> {
        private final T value;
        private final boolean isFromUserInput;

        /**
         * @param value     The resultant value from argument parsing.
         * @param fromInput Indicates if value is parsed by user input.
         */
        private SingleFlagResult(T value, boolean fromInput) {
            this.value = value;
            this.isFromUserInput = fromInput;
        }

        @Override
        public String toString() {
            return "SingleFlagResult{" +
                    "value=" + value +
                    ", isFromUserInput=" + isFromUserInput +
                    '}';
        }
    }
}
