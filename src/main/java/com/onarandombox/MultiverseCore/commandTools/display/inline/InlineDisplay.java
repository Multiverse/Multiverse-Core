package com.onarandombox.MultiverseCore.commandTools.display.inline;

import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;

public abstract class InlineDisplay<C extends InlineDisplay<?, T>, T> extends ContentDisplay<C, T> {

    protected String prefix = "";
    protected String suffix = "";
    protected String separator = DEFAULT_SEPARATOR;

    public static final String DEFAULT_SEPARATOR = ", ";

    public C withPrefix(String prefix) {
        this.prefix = prefix;
        return (C) this;
    }

    public C withSuffix(String suffix) {
        this.suffix = suffix;
        return (C) this;
    }

    public C withSeparator(String separator) {
        this.separator = separator;
        return (C) this;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getSeparator() {
        return separator;
    }
}
