package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;

import java.io.Serializable;
import java.util.Map;

public record CacheData(Map<String, FoodCollection> foodInfoCache, Map<Integer, FoodReport> foodReportCache,
                        Map<String, Food> foodBarcodeCache) implements Serializable {
}
