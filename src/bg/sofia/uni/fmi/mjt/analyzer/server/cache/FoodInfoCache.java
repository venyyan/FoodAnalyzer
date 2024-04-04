package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;

public interface FoodInfoCache {
    FoodCollection getFoodInfo(String foodName);

    void addFoodInfo(String foodName, FoodCollection foodInfos);
}
