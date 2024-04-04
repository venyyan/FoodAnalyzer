package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;

public interface FoodBarcodeCache {
    Food getFoodInfoByBarcode(String barcode);

    void addFoodReport(String barcode, Food report);
}
