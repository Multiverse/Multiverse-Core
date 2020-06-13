package com.onarandombox.MultiverseCore.utils;

import java.util.HashMap;
import java.util.Map;

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
            createCustomGeneratorsMetric();
            createEnvironmentsMetric();

            Logging.fine("Metrics enabled.");
        } catch (Exception e) {
            Logging.warning("There was an issue while enabling metrics:");
            e.printStackTrace();
        }
    }

    private void createCustomGeneratorsMetric() {
        metrics.addCustomChart(new Metrics.AdvancedPie("custom_generators", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (MultiverseWorld w : plugin.getMVWorldManager().getMVWorlds()) {
                String gen = w.getGenerator() != null ? w.getGenerator() : "N/A";
                map.putIfAbsent(gen, 0);
                map.put(gen, map.get(gen) + 1);
            }

            return map;
        }));
    }

    private void createEnvironmentsMetric() {
        metrics.addCustomChart(new Metrics.AdvancedPie("environments", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (MultiverseWorld w : plugin.getMVWorldManager().getMVWorlds()) {
                String env = w.getEnvironment().name().replace('_', ' ');
                env = StringUtils.capitalize(env.toLowerCase());
                map.putIfAbsent(env, 0);
                map.put(env, map.get(env) + 1);
            }

            return map;
        }));
    }
}
