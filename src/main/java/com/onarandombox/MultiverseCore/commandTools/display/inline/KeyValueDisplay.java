package com.onarandombox.MultiverseCore.commandTools.display.inline;

import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Used to display config/property values pair, each separated with a comma.
 */
public class KeyValueDisplay extends ContentDisplay<KeyValueDisplay, Map<String, Object>> {

    private String operator = DEFAULT_OPERATOR;

    public static final String DEFAULT_OPERATOR = " = ";

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull ShowKeyValue getShowRunnable() {
        return new ShowKeyValue(this);
    }

    public @NotNull KeyValueDisplay withOperator(@NotNull String operator) {
        this.operator = operator;
        return this;
    }

    public @NotNull String getOperator() {
        return operator;
    }
}
