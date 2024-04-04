package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.CacheFileNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class CacheFileManager {
    public void writeCacheToFile(Map<String, FoodCollection> foodInfoCache,
                                 Map<Integer, FoodReport> foodReportCache, Map<String, Food> foodBarcodeCache,
                                 OutputStream outputStream) throws CacheFileNotFoundException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            CacheData cacheData = new CacheData(foodInfoCache, foodReportCache, foodBarcodeCache);
            objectOutputStream.writeObject(cacheData);
        } catch (IOException ioException) {
            throw new CacheFileNotFoundException("Warning! Can't find cache file", ioException);
        }
    }

    public void readCacheFromFile(Map<String, FoodCollection> foodInfoCache,
                                  Map<Integer, FoodReport> foodReportCache, Map<String, Food> foodBarcodeCache,
                                  InputStream inputStream) throws CacheFileNotFoundException {
        try (ObjectInputStream inputObjectStream = new ObjectInputStream(inputStream)) {
            CacheData cacheData = (CacheData) inputObjectStream.readObject();
            mergeCacheData(foodInfoCache, cacheData.foodInfoCache());
            mergeCacheData(foodReportCache, cacheData.foodReportCache());
            mergeCacheData(foodBarcodeCache, cacheData.foodBarcodeCache());
        } catch (IOException | ClassNotFoundException exception) {
            throw new CacheFileNotFoundException("Error! Unable to read cache data", exception);
        }
    }

    private <K, V> void mergeCacheData(Map<K, V> existingCache, Map<K, V> newCache) {
        existingCache.putAll(newCache);
    }
}
