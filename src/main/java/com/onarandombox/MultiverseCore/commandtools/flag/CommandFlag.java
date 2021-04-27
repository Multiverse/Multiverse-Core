package com.onarandombox.MultiverseCore.commandtools.flag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>Represents a flag that can be used in commands. This works as a key value pair.</p>
 *
 * <p>Key is the {@link #identifier} and {@link #aliases} set.</p>
 * <p>Value is the {@link T} parsed based on 3 scenarios during command input:</p>
 * <ol>
 *     <li>Flag completely not present. {@link #getDefaultValue()}</li>
 *     <li>Flag key present but no value. {@link #getValue()}</li>
 *     <li>Flag key and value present. {@link #getValue(String)}</li>
 * </ol>
 *
 * @param <T>   The flag Type.
 */
public abstract class CommandFlag<T> {

    protected final String name;
    protected final String identifier;
    protected final Class<T> type;
    protected final Collection<String> aliases;

    /**
     * @param name          Readable name for the flag.
     * @param identifier    Unique key to identify the flag in command arguments.
     * @param type          The type of value this flag represents.
     */
    protected CommandFlag(@NotNull String name,
                          @NotNull String identifier,
                          @NotNull Class<T> type) {

        this.name = name;
        this.identifier = identifier;
        this.type = type;
        this.aliases = new ArrayList<>();
    }

    /**
     * Gets name of the Command Flag.
     *
     * @return The Command Flag name.
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets identifier of the Command Flag.
     *
     * @return The Command Flag identifier.
     */
    @NotNull
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Gets {@link T} Type of the Command Flag.
     *
     * @return The Command Flag type.
     */
    @NotNull
    public Class<T> getType() {
        return this.type;
    }

    /**
     * Gets all the alternative key identifiers set for this Command Flag.
     *
     * @return Collection of aliases.
     */
    @NotNull
    public Collection<String> getAliases() {
        return this.aliases;
    }

    /**
     * Add alternative key identifiers for this Command Flag.
     *
     * @param aliases   Alias(es) to be added.
     * @return A {@link CommandFlag}.
     */
    public CommandFlag<T> addAliases(String...aliases) {
        Collections.addAll(this.aliases, aliases);
        return this;
    }

    /**
     * Tab-complete suggestion for this Command Flag values.
     *
     * @return Collection of suggested values available.
     */
    public abstract Collection<String> suggestValue();

    /**
     * When this Command Flag can get value by a user input.
     *
     * @return The {@link T} value.
     */
    public abstract T getValue(@NotNull String input) throws FlagParseFailedException;

    /**
     * When this Command Flag user input value is null/not present.
     *
     * @return The {@link T} value.
     */
    public T getValue() throws FlagParseFailedException {
        return null;
    }

    /**
     * When this Command Flag is not present in command input.
     *
     * @return The {@link T} value.
     */
    public T getDefaultValue() {
        return null;
    }

    @Override
    public String toString() {
        return "CommandFlag{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", type=" + type +
                ", aliases=" + aliases +
                '}';
    }
}
