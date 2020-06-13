package com.onarandombox.MultiverseCore.utils.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bstats.bukkit.Metrics;

enum MetricsHelper {
    ;

    /**
     * Adds one to the value in the given map with the given key. If the key does not exist in the map, it will be added with a value of 1.
     */
    static void incrementCount(Map<String, Integer> map, String key) {
        Integer count = map.getOrDefault(key, 0);
        map.put(key, count + 1);
    }

    static Metrics.AdvancedPie createAdvancedPieChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new Metrics.AdvancedPie(chartId, () -> map);
    }

    static Metrics.MultiLineChart createMultiLineChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new Metrics.MultiLineChart(chartId, () -> map);
    }

}
