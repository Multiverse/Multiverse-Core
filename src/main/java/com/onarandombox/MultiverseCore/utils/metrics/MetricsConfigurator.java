package com.onarandombox.MultiverseCore.utils.metrics;

import java.util.Map;
import java.util.function.Consumer;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.apache.commons.lang.StringUtils;
import org.bstats.bukkit.Metrics;

public class MetricsConfigurator {

    private static final int PLUGIN_ID = 7765;

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

    private void initMetrics() {
        try {
            addCustomGeneratorsMetric();
            createEnvironmentsMetric();

            Logging.fine("Metrics enabled.");
        } catch (Exception e) {
            Logging.warning("There was an issue while enabling metrics:");
            e.printStackTrace();
        }
    }

    private void addCustomGeneratorsMetric() {
        addAdvancedPieMetric("custom_generators", map -> {
            for (MultiverseWorld w : plugin.getMVWorldManager().getMVWorlds()) {
                String gen = w.getGenerator() != null ? w.getGenerator() : "N/A";
                map.putIfAbsent(gen, 0);
                map.put(gen, map.get(gen) + 1);
            }
        });
    }

    private void createEnvironmentsMetric() {
        addAdvancedPieMetric("environments", map -> {
            for (MultiverseWorld w : plugin.getMVWorldManager().getMVWorlds()) {
                String env = w.getEnvironment().name().replace('_', ' ');
                env = StringUtils.capitalize(env.toLowerCase());
                map.putIfAbsent(env, 0);
                map.put(env, map.get(env) + 1);
            }
        });
    }

    private void addAdvancedPieMetric(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        metrics.addCustomChart(MetricsHelper.createAdvancedPieChart(chartId, metricsFunc));
    }
}
