package org.mvplugins.multiverse.core.utils.metrics;

import java.util.Map;
import java.util.function.Consumer;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.apache.commons.lang.WordUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

@Service
public class MetricsConfigurator {

    private static final int PLUGIN_ID = 7765;
    private static final String NO_GENERATOR_NAME = "N/A";

    private final WorldManager worldManager;
    private final Metrics metrics;

    @Inject
    private MetricsConfigurator(MultiverseCore plugin, WorldManager worldManager) {
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
            for (LoadedMultiverseWorld w : worldManager.getLoadedWorlds()) {
                MetricsHelper.incrementCount(map, getGeneratorName(w));
            }
        });
    }

    private String getGeneratorName(LoadedMultiverseWorld world) {
        String gen = world.getGenerator();
        return (gen != null && !gen.equalsIgnoreCase("null")) ? gen.split(":")[0] : NO_GENERATOR_NAME;
    }

    private void addEnvironmentsMetric() {
        addAdvancedPieMetric("environments", map -> {
            for (LoadedMultiverseWorld w : worldManager.getLoadedWorlds()) {
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
            map.put("Loaded worlds", worldManager.getLoadedWorlds().size());
            map.put("Total number of worlds", worldManager.getWorlds().size());
        });
    }

    private void addAdvancedPieMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(MetricsHelper.createAdvancedPieChart(chartId, metricsFunc));
    }

    private void addMultiLineMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(MetricsHelper.createMultiLineChart(chartId, metricsFunc));
    }
}
