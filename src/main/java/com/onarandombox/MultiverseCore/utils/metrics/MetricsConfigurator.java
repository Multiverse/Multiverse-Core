package com.onarandombox.MultiverseCore.utils.metrics;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.apache.commons.lang.WordUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;

public class MetricsConfigurator {

    private static final int PLUGIN_ID = 7765;
    private static final String NO_GENERATOR_NAME = "N/A";

    public static void configureMetrics(MultiverseCore plugin) {
        MetricsConfigurator configurator = new MetricsConfigurator(plugin);
        configurator.initMetrics();
    }

    private final MultiverseCore plugin;
    private final Metrics metrics;

    private MetricsConfigurator(MultiverseCore plugin) {
        this.plugin = plugin;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
    }

    private MVWorldManager getWorldManager() {
        return plugin.getMVWorldManager();
    }

    private Collection<MultiverseWorld> getMVWorlds() {
        return getWorldManager().getMVWorlds();
    }

    private void initMetrics() {
        try {
            addCustomGeneratorsMetric();
            addEnvironmentsMetric();
            addWorldCountMetric();

            Logging.fine("Metrics enabled.");
        } catch (Exception e) {
            Logging.warning("There was an issue while enabling metrics:");
            e.printStackTrace();
        }
    }

    private void addCustomGeneratorsMetric() {
        addAdvancedPieMetric("custom_generators", map -> {
            for (MultiverseWorld w : getMVWorlds()) {
                MetricsHelper.incrementCount(map, getGeneratorName(w));
            }
        });
    }

    private String getGeneratorName(MultiverseWorld world) {
        String gen = world.getGenerator();
        return (gen != null && !gen.equalsIgnoreCase("null")) ? gen.split(":")[0] : NO_GENERATOR_NAME;
    }

    private void addEnvironmentsMetric() {
        addAdvancedPieMetric("environments", map -> {
            for (MultiverseWorld w : getMVWorlds()) {
                MetricsHelper.incrementCount(map, titleCaseEnv(w.getEnvironment()));
            }
        });
    }

    private String titleCaseEnv(World.Environment env) {
        String envName = env.name().replaceAll("_+", " ");
        return WordUtils.capitalizeFully(envName);
    }

    private void addWorldCountMetric() {
        addMultiLineMetric("world_count", map -> {
            int loadedWorldsCount = getMVWorlds().size();
            map.put("Loaded worlds", loadedWorldsCount);
            map.put("Total number of worlds", loadedWorldsCount + getWorldManager().getUnloadedWorlds().size());
        });
    }

    private void addAdvancedPieMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(MetricsHelper.createAdvancedPieChart(chartId, metricsFunc));
    }

    private void addMultiLineMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(MetricsHelper.createMultiLineChart(chartId, metricsFunc));
    }
}
