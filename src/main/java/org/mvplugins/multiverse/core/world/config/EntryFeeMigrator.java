package org.mvplugins.multiverse.core.world.config;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;
import org.mvplugins.multiverse.core.configuration.migration.MigratorAction;

/**
 * Migrates the entry fee settings. Assumes entry fee is disabled if currency is not set.
 */
public class EntryFeeMigrator implements MigratorAction {
    @Override
    public void migrate(ConfigurationSection config) {
        String currency = config.getString("entry-fee.currency", "");
        Logging.info("Entry fee currency: %s", currency);
        if (currency.isEmpty()) {
            config.set("entry-fee.enabled", false);
            config.set("entry-fee.currency", CurrencySerializer.VAULT_ECONOMY_CODE);
        } else {
            config.set("entry-fee.enabled", true);
        }
    }
}
