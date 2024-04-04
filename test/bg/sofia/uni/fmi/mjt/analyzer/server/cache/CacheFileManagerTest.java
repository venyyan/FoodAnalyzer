package bg.sofia.uni.fmi.mjt.analyzer.server.cache;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.CacheFileNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.Nutrients;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.NutrientsValue;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class CacheFileManagerTest {
    @Test
    void testWriteCacheToFile() throws CacheFileNotFoundException, IOException, ClassNotFoundException {
        Food tempFood = new Food(1, "test", "800");
        FoodCollection temp = new FoodCollection(List.of(tempFood));
        Map<String, FoodCollection> foodInfoCache = new HashMap<>();
        foodInfoCache.put("test", temp);

        Nutrients nutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));
        FoodReport report = new FoodReport("test", "testIngredients", nutrients);
        Map<Integer, FoodReport> foodReportCache = new HashMap<>();
        foodReportCache.put(1, report);

        Food tempFood1 = new Food(2, "test", "800");
        Map<String, Food> foodBarcodeCache = new HashMap<>();
        foodBarcodeCache.put("800", tempFood1);

        CacheData expected;
        try (FileOutputStream fileOutputStream = new FileOutputStream("testCache.txt")) {
            CacheFileManager cache = new CacheFileManager();
            cache.writeCacheToFile(foodInfoCache, foodReportCache, foodBarcodeCache, fileOutputStream);

            expected = new CacheData(foodInfoCache, foodReportCache, foodBarcodeCache);
        }

        CacheData actual;
        try (FileInputStream fileInputStream = new FileInputStream("testCache.txt")) {
            ObjectInputStream testInput = new ObjectInputStream(fileInputStream);
            actual = (CacheData) testInput.readObject();
        }
        assertEquals(expected, actual,
            "Write cache to file not working properly. Expected: " + expected + ", but was: " + actual);
    }


    @Test
    void testWriteCacheToFileWhenIOOccurs() throws IOException {
        ObjectOutputStream objectOutputStream = mock(ObjectOutputStream.class);
        OutputStream outputStream = mock(OutputStream.class);

        CacheFileManager cache = new CacheFileManager();

        doThrow(new IOException("Test IO Exception")).when(objectOutputStream).writeObject(any());
        assertThrows(CacheFileNotFoundException.class, () ->
                cache.writeCacheToFile(mock(Map.class), mock(Map.class), mock(Map.class), outputStream),
            "CacheFileNotFoundException expected to be thrown");
    }

    @Test
    void testReadCacheFromFileWhenClassCannotBeCasted() throws IOException {
        CacheFileManager cache = new CacheFileManager();

        Food tempFood = new Food(1, "test", "800");
        try (FileOutputStream fileOutputStream = new FileOutputStream("invalid.txt");
             ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream)) {
            outputStream.writeObject(tempFood);
        }

        Map<String, FoodCollection> foodInfoCache = new HashMap<>();
        Map<Integer, FoodReport> foodReportCache = new HashMap<>();
        Map<String, Food> foodBarcodeCache = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream("invalid.txt")) {
            assertThrows(ClassCastException.class,
                () -> cache.readCacheFromFile(foodInfoCache, foodReportCache, foodBarcodeCache, inputStream),
                "ClassCastException expected to be thrown");
        }
    }
}
