package com.onarandombox.MultiverseCore.generators;

import com.onarandombox.MultiverseCore.api.GeneratorPlugin;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A default implementation of {@link GeneratorPlugin} for those generator plugins that do not provide their own
 * custom {@link GeneratorPlugin} implementation to Multiverse.
 */
public class SimpleGeneratorPlugin implements GeneratorPlugin {

    public static String TEST_WORLDNAME = "test";
    public static String DEFAULT_TEST_ID = "";

    private final Plugin plugin;
    private final List<String> workingIds;

    SimpleGeneratorPlugin(Plugin plugin) {
        this(plugin, DEFAULT_TEST_ID);
    }

    SimpleGeneratorPlugin(Plugin plugin, String testedId) {
        this.plugin = plugin;
        this.workingIds = new ArrayList<>();
        this.workingIds.add(testedId);
    }

    /**
     * Adds a known generator id that was tested to be working.
     *
     * @param id The known working generator id.
     */
    void addKnownWorkingId(String id) {
        if (!this.workingIds.contains(id)) {
            this.workingIds.add(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggestIds(@Nullable String currentIdInput) {
        return this.workingIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ChunkGenerator getDefaultChunkGenerator() throws Exception {
        return this.plugin.getDefaultWorldGenerator(TEST_WORLDNAME, this.workingIds.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ChunkGenerator getChunkGenerator(String id, String worldName) throws Exception {
        return this.plugin.getDefaultWorldGenerator(worldName, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Collection<String> getExampleUsages() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public @Nullable String getInfoLink() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }
}
