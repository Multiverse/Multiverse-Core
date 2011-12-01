package com.onarandombox.MultiverseCore.localization;

import java.util.Locale;
import java.util.Set;

/**
 * Multiverse 2 LazyMessageProvider
 * <p>
 * This interface describes a Multiverse-MessageProvider that only loads locales when they're needed.
 */
public interface LazyLocaleMessageProvider extends MessageProvider {

    /**
     * Loads a localization for a specified {@link Locale}.
     * <p>
     * If that localization is already loaded, this method will reload it.
     *
     * @param locale The desired {@link Locale}.
     * @throws LocalizationLoadingException When an error occurs while trying to load the specified localization.
     * @throws NoSuchLocalizationException When no localization was found for the desired locale.
     */
    public void loadLocale(Locale locale) throws NoSuchLocalizationException, LocalizationLoadingException;

    /**
     * Retrieves all loaded localizations.
     *
     * @return A {@link Set} of {@link Locale}s whose localizations are currently loaded.
     */
    public Set<Locale> getLoadedLocales();

    /**
     * Detects if a localization is loaded for the specified {@link Locale}.
     *
     * @param locale The {@link Locale}.
     * @return Whether a localization is loaded for the specified {@link Locale}.
     */
    public boolean isLocaleLoaded(Locale locale);

}
