package org.mvplugins.multiverse.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.apache.commons.lang.WordUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.MultiLineChart;
import org.bukkit.World;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
final class BstatsMetricsConfigurator {

    private static final int PLUGIN_ID = 7765;
    private static final String NO_GENERATOR_NAME = "N/A";

    private final WorldManager worldManager;
    private final Metrics metrics;

    @Inject
    private BstatsMetricsConfigurator(MultiverseCore plugin, WorldManager worldManager) {
        this.worldManager = worldManager;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
    }

    @PostConstruct
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
            for (MultiverseWorld w : worldManager.getLoadedWorlds()) {
                incrementCount(map, getGeneratorName(w));
            }
        });
    }

    private String getGeneratorName(MultiverseWorld world) {
        String gen = world.getGenerator();
        return (gen != null && !gen.equalsIgnoreCase("null")) ? gen.split(":")[0] : NO_GENERATOR_NAME;
    }

    private void addEnvironmentsMetric() {
        addAdvancedPieMetric("environments", map -> {
            for (MultiverseWorld w : worldManager.getLoadedWorlds()) {
                incrementCount(map, titleCaseEnv(w.getEnvironment()));
            }
        });
    }

    private String titleCaseEnv(World.Environment env) {
        String envName = env.name().replaceAll("_+", " ");
        return WordUtils.capitalizeFully(envName);
    }

    private void addWorldCountMetric() {
        addMultiLineMetric("world_count", map -> {
            map.put("Loaded worlds", worldManager.getLoadedWorlds().size());
            map.put("Total number of worlds", worldManager.getWorlds().size());
        });
    }

    private void addAdvancedPieMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(createAdvancedPieChart(chartId, metricsFunc));
    }

    private void addMultiLineMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(createMultiLineChart(chartId, metricsFunc));
    }

    private void incrementCount(Map<String, Integer> map, String key) {
        Integer count = map.getOrDefault(key, 0);
        map.put(key, count + 1);
    }

    private AdvancedPie createAdvancedPieChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new AdvancedPie(chartId, () -> map);
    }

    private MultiLineChart createMultiLineChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new MultiLineChart(chartId, () -> map);
    }
}
