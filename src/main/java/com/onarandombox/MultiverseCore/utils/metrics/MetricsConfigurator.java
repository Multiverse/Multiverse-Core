package com.onarandombox.MultiverseCore.utils.metrics;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MVWorld;
import jakarta.inject.Inject;
import org.apache.commons.lang.WordUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.jvnet.hk2.annotations.Service;

@Service
public class MetricsConfigurator {

    private static final int PLUGIN_ID = 7765;
    private static final String NO_GENERATOR_NAME = "N/A";

    private final MVWorldManager worldManager;
    private final Metrics metrics;

    @Inject
    private MetricsConfigurator(MultiverseCore plugin, MVWorldManager worldManager) {
        this.worldManager = worldManager;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
    }

    private MVWorldManager getWorldManager() {
        return worldManager;
    }

    private Collection<MVWorld> getMVWorlds() {
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
            for (MVWorld w : getMVWorlds()) {
                MetricsHelper.incrementCount(map, getGeneratorName(w));
            }
        });
    }

    private String getGeneratorName(MVWorld world) {
        String gen = world.getGenerator();
        return (gen != null && !gen.equalsIgnoreCase("null")) ? gen.split(":")[0] : NO_GENERATOR_NAME;
    }

    private void addEnvironmentsMetric() {
        addAdvancedPieMetric("environments", map -> {
            for (MVWorld w : getMVWorlds()) {
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
