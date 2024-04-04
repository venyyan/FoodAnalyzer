package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.CacheFileNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FoodCache implements FoodInfoCache, FoodReportCache, FoodBarcodeCache {
    private final Map<String, FoodCollection> foodInfoCache;
    private final Map<Integer, FoodReport> foodReportCache;
    private final Map<String, Food> foodBarcodeCache;
    private final CacheFileManager cacheFileManager;

    public FoodCache() {
        this.foodInfoCache = new ConcurrentHashMap<>();
        this.foodReportCache = new ConcurrentHashMap<>();
        this.foodBarcodeCache = new ConcurrentHashMap<>();
        this.cacheFileManager = new CacheFileManager();
    }

    @Override
    public FoodCollection getFoodInfo(String foodName) {
        return foodInfoCache.get(foodName);
    }

    @Override
    public void addFoodInfo(String foodName, FoodCollection foodInfos) {
        foodInfoCache.putIfAbsent(foodName, foodInfos);

        for (Food food : foodInfos.foods()) {
            if (food.upc() != null) {
                addFoodReport(food.upc(), food);
            }
        }
    }

    @Override
    public FoodReport getFoodReport(int foodId) {
        return foodReportCache.get(foodId);
    }

    @Override
    public void addFoodReport(Integer id, FoodReport report) {
        foodReportCache.putIfAbsent(id, report);
    }

    @Override
    public Food getFoodInfoByBarcode(String barcode) {
        return foodBarcodeCache.get(barcode);
    }

    @Override
    public void addFoodReport(String barcode, Food report) {
        foodBarcodeCache.putIfAbsent(barcode, report);
    }

    public void writeCacheToFile(OutputStream outputStream) throws CacheFileNotFoundException {
        cacheFileManager.writeCacheToFile(foodInfoCache, foodReportCache, foodBarcodeCache, outputStream);
    }

    public void readCacheFromFile(InputStream inputStream) throws CacheFileNotFoundException {
        cacheFileManager.readCacheFromFile(foodInfoCache, foodReportCache, foodBarcodeCache, inputStream);
    }
}