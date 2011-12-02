package com.onarandombox.MultiverseCore.localization;

import java.io.IOException;
import java.util.Locale;

/**
 * Thrown when an error occurs while a localization is loaded.
 */
public class LocalizationLoadingException extends IOException {
    private final Locale locale;

    public LocalizationLoadingException(Locale locale) {
        this.locale = locale;
    }

    public LocalizationLoadingException(String message, Locale locale) {
        super(message);
        this.locale = locale;
    }

    public LocalizationLoadingException(Throwable cause, Locale locale) {
        super(cause);
        this.locale = locale;
    }

    public LocalizationLoadingException(String message, Throwable cause, Locale locale) {
        super(message, cause);
        this.locale = locale;
    }

    /**
     * @return The locale we were trying to load.
     */
    public Locale getLocale() {
        return locale;
    }

    public String getMessage() {
        return super.getMessage() + " (While trying to load localization for locale " + getLocale() + ")";
    }

}
