package com.onarandombox.MultiverseCore.utils.metrics;

import org.bstats.charts.AdvancedPie;
import org.bstats.charts.MultiLineChart;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

enum MetricsHelper {
    ;

    /**
     * Adds one to the value in the given map with the given key. If the key does not exist in the map, it will be added with a value of 1.
     */
    static void incrementCount(Map<String, Integer> map, String key) {
        Integer count = map.getOrDefault(key, 0);
        map.put(key, count + 1);
    }

    static AdvancedPie createAdvancedPieChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new AdvancedPie(chartId, () -> map);
    }

    static MultiLineChart createMultiLineChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new MultiLineChart(chartId, () -> map);
    }

}
