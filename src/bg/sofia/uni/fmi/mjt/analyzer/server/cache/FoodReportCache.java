package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;

public interface FoodReportCache {
    FoodReport getFoodReport(int foodId);

    void addFoodReport(Integer id, FoodReport report);
}
