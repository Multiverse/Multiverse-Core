package com.onarandombox.MultiverseCore.utils.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bstats.bukkit.Metrics;

enum MetricsHelper {
    ;

    static Metrics.AdvancedPie createAdvancedPieChart(String chartId, Consumer<Map<String, Integer>> metricsFunc) {
        Map<String, Integer> map = new HashMap<>();
        metricsFunc.accept(map);
        return new Metrics.AdvancedPie(chartId, () -> map);
    }

}
