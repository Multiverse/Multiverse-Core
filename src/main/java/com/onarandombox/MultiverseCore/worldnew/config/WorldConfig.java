package com.onarandombox.MultiverseCore.worldnew.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.handle.ConfigurationSectionHandle;
import io.vavr.control.Try;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldConfig {
    private final WorldConfigNodes configNodes;
    private final ConfigurationSectionHandle configHandle;

    public WorldConfig(@NotNull final ConfigurationSection configSection) {
        this.configNodes = new WorldConfigNodes();
        //todo: Config migration and version
        this.configHandle = ConfigurationSectionHandle.builder(configSection)
                .logger(Logging.getLogger())
                .nodes(configNodes.getNodes())
                .build();
        this.configHandle.load();
    }

    public Try<Object> getProperty(String name) {
        return configHandle.get(name);
    }

    public Try<Void> setProperty(String name, Object value) {
        return configHandle.set(name, value);
    }

    public void setAlias(String alias) {
        configHandle.set(configNodes.ALIAS, alias);
    }

    public @Nullable String getAlias() {
        return configHandle.get(configNodes.ALIAS);
    }

    public void setHidden(boolean hidden) {
        configHandle.set(configNodes.HIDDEN, hidden);
    }

    public boolean isHidden() {
        return configHandle.get(configNodes.HIDDEN);
    }

    public List<String> getWorldBlacklist() {
        return (List<String>) configHandle.get(configNodes.WORLD_BLACKLIST);
    }

    public void setWorldBlacklist(List<String> worldBlacklist) {
        configHandle.set(configNodes.WORLD_BLACKLIST, worldBlacklist);
    }
}
