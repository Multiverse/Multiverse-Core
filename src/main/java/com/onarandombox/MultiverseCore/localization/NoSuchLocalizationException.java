package com.onarandombox.MultiverseCore.localization;

import java.util.Locale;

/**
 * Thrown when the requested localization is not found.
 */
public class NoSuchLocalizationException extends LocalizationLoadingException {

    public NoSuchLocalizationException(Locale locale) {
        super(locale);
    }

    public NoSuchLocalizationException(String message, Locale locale) {
        super(message, locale);
    }

    public NoSuchLocalizationException(String message, Throwable cause, Locale locale) {
        super(message, cause, locale);
    }

    public NoSuchLocalizationException(Throwable cause, Locale locale) {
        super(cause, locale);
    }

}
