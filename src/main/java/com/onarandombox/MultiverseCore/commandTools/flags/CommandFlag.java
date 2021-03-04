package com.onarandombox.MultiverseCore.commandTools.flags;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CommandFlag<T> {

    protected final String name;
    protected final String identifier;
    protected final Class<T> type;
    protected final ValueRequirement valueRequirement;
    protected final List<String> aliases;

    public CommandFlag(String name, String identifier, Class<T> type, ValueRequirement valueRequirement) {
        this.name = name;
        this.identifier = identifier;
        this.type = type;
        this.valueRequirement = valueRequirement;
        this.aliases = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Class<T> getType() {
        return this.type;
    }

    public ValueRequirement getValueRequirement() {
        return this.valueRequirement;
    }

    public Collection<String> getAliases() {
        return this.aliases;
    }

    CommandFlag<T> addAlias(String alias) {
        this.aliases.add(alias);
        return this;
    }

    public abstract Collection<String> suggestValue();

    public abstract T getValue(@NotNull String input) throws FlagParseFailedException;

    public T getValue() throws FlagParseFailedException {
        return null;
    }

    public T getDefaultValue() {
        return null;
    }

    @Override
    public String toString() {
        return "CommandFlag{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", type=" + type +
                ", valueRequirement=" + valueRequirement +
                ", aliases=" + aliases +
                '}';
    }

    public enum ValueRequirement {
        NONE,
        OPTIONAL,
        REQUIRED
    }
}
