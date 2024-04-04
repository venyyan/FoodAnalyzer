package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.CacheFileNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.Nutrients;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.NutrientsValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoodCacheTest {

    @BeforeAll
    static void setUpTestFile() throws IOException {
        Food testFood = new Food(1, "test", "800");
        FoodCollection temp = new FoodCollection(List.of(testFood));
        Map<String, FoodCollection> foodInfoCache = new HashMap<>();
        foodInfoCache.put("test", temp);

        Nutrients nutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));

        FoodReport report = new FoodReport("test", "testIngredients", nutrients);
        Map<Integer, FoodReport> foodReportCache = new HashMap<>();
        foodReportCache.put(1, report);

        Food tempFood1 = new Food(2, "test", "801");
        Map<String, Food> foodBarcodeCache = new HashMap<>();
        foodBarcodeCache.put("801", tempFood1);

        try(FileOutputStream fileOutputStream = new FileOutputStream("testCache.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            CacheData cacheData = new CacheData(foodInfoCache, foodReportCache, foodBarcodeCache);
            objectOutputStream.writeObject(cacheData);
        }
    }

    @Test
    void testReadFoodInfoCacheFromFile() throws CacheFileNotFoundException, IOException {
        Food testFood = new Food(1, "test", "800");
        FoodCollection expected = new FoodCollection(List.of(testFood));

        FoodCache cache = new FoodCache();

        try (FileInputStream fileInputStream = new FileInputStream("testCache.txt")) {
            cache.readCacheFromFile(fileInputStream);
        }

        assertEquals(expected, cache.getFoodInfo("test"), "Read food info cache from file not working properly. " +
            "Expected: " + expected + ", but was: " + cache.getFoodInfo("test"));
    }

    @Test
    void testReadFoodReportCacheFromFile() throws CacheFileNotFoundException, IOException {
        Nutrients nutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));
        FoodReport expected = new FoodReport("test", "testIngredients", nutrients);

        FoodCache cache = new FoodCache();

        try (FileInputStream fileInputStream = new FileInputStream("testCache.txt")) {
            cache.readCacheFromFile(fileInputStream);
        }

        assertEquals(expected, cache.getFoodReport(1), "Read food report cache from file not working properly. " +
            "Expected: " + expected + ", but was: " + cache.getFoodReport(1));
    }

    @Test
    void testReadFoodBarcodeCacheFromFile() throws CacheFileNotFoundException, IOException {
        Food expected = new Food(2, "test", "801");

        FoodCache cache = new FoodCache();

        try (FileInputStream fileInputStream = new FileInputStream("testCache.txt")) {
            cache.readCacheFromFile(fileInputStream);
        }

        assertEquals(expected, cache.getFoodInfoByBarcode("801"), "Read food report cache from file not working properly. " +
            "Expected: " + expected + ", but was: " + cache.getFoodInfoByBarcode("801"));
    }

    @Test
    void testAddFoodInfoIfAddingBarcode() {
        Map<String, Food> testBarcodeCache = new HashMap<>();

        Food food1 = new Food(1, "food1", "000");
        Food food2 = new Food(2, "food2", "001");
        Food food3 = new Food(3, "food3", null);

        FoodCollection testCollection = new FoodCollection(List.of(food1, food2, food3));
        testBarcodeCache.put("000", food1);
        testBarcodeCache.put("001", food2);

        FoodCache foodCache = new FoodCache();
        foodCache.addFoodInfo("testName", testCollection);
        assertEquals(testBarcodeCache.get("001"),
            foodCache.getFoodInfoByBarcode("001"), "Adding barcodes while adding food infos jot working properly. Expected: " +
            ", but was: " + foodCache.getFoodInfoByBarcode("001"));
    }
}
