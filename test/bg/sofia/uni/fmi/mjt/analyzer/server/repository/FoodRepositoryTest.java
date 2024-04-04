package bg.sofia.uni.fmi.mjt.analyzer.server.repository;

import bg.sofia.uni.fmi.mjt.analyzer.server.Response;
import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.FoodAPIResponseException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.Nutrients;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.NutrientsValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FoodRepositoryTest {
    private final FoodCache foodCacheMock = mock();
    private final FoodRetriever retrieverMock = mock();
    private final FoodRepository foodRepository = new FoodRepository(foodCacheMock, retrieverMock);

    @Test
    void testGetFoodByNameWithNullName() {
        Response expectedResponse = Response.decline("Name of food can't be null or empty");
        Response actualResponse = foodRepository.getFoodByName(null);

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByNameWithEmptyName() {
        Response expectedResponse = Response.decline("Name of food can't be null or empty");
        Response actualResponse = foodRepository.getFoodByName("");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByNameWithNameInCache() {
        Food foodNutella = new Food(1, "Nutella chocolate", "72527273070");
        FoodCollection foodCollection = new FoodCollection(List.of(foodNutella));

        when(foodCacheMock.getFoodInfo("nutella")).thenReturn(foodCollection);

        Response expectedResponse = Response.ok(foodCollection);
        Response actualResponse = foodRepository.getFoodByName("nutella");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByNameWithNameNotInCacheThrowsFoodAPIResponseException() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodInfo("nutella")).thenReturn(null);

        when(retrieverMock.getFoodInfos(any(FoodRequest.class))).thenThrow(new FoodAPIResponseException("test"));

        Response expectedResponse = Response.decline("test");
        Response actualResponse = foodRepository.getFoodByName("nutella");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByNameNothingFound() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodInfo("nutella")).thenReturn(null);

        FoodCollection foodCollection = new FoodCollection(List.of());
        when(retrieverMock.getFoodInfos(any(FoodRequest.class))).thenReturn(foodCollection);

        Response expectedResponse = Response.nothingFound("No results found");
        Response actualResponse = foodRepository.getFoodByName("nutella");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByNameSuccessfulFind() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodInfo("nutella")).thenReturn(null);

        Food food1 = new Food(1, "test1", "800700600");
        Food food2 = new Food(2, "test2", "100200300");
        Collection<Food> foods = List.of(food1, food2);
        FoodCollection foodCollection = new FoodCollection(foods);
        when(retrieverMock.getFoodInfos(any(FoodRequest.class))).thenReturn(foodCollection);

        Response expectedResponse = Response.ok(foodCollection);
        Response actualResponse = foodRepository.getFoodByName("nutella");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByIdWithInvalidId() {
        Response expected = Response.decline("Id of food can't be negative");
        Response actual = foodRepository.getFoodById(-1);

        assertEquals(expected, actual,
            "Expected response is: " + expected.additionalInfo() + ", but was: " + actual.additionalInfo());
    }

    @Test
    void testGetFoodByIdWithIdInCache() {
        FoodReport foodReport = getFoodReport();

        when(foodCacheMock.getFoodReport(26)).thenReturn(foodReport);

        Response expectedResponse = Response.ok(foodReport);
        Response actualResponse = foodRepository.getFoodById(26);

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByIdWithNameNotInCacheThrowsFoodAPIResponseException() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodReport(26)).thenReturn(null);

        when(retrieverMock.getFoodReport(any(FoodRequest.class))).thenThrow(new FoodAPIResponseException("test"));

        Response expectedResponse = Response.decline("test");
        Response actualResponse = foodRepository.getFoodById(26);

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByIdNothingFound() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodReport(26)).thenReturn(null);

        when(retrieverMock.getFoodInfos(any(FoodRequest.class))).thenReturn(null);

        Response expectedResponse = Response.nothingFound("No results found");
        Response actualResponse = foodRepository.getFoodById(26);

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByIdSuccessfulFind() throws FoodAPIResponseException {
        when(foodCacheMock.getFoodReport(26)).thenReturn(null);

        FoodReport foodReport = getFoodReport();
        when(retrieverMock.getFoodReport(any(FoodRequest.class))).thenReturn(foodReport);

        Response expectedResponse = Response.ok(foodReport);
        Response actualResponse = foodRepository.getFoodById(26);

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    private FoodReport getFoodReport() {
        NutrientsValue fat = new NutrientsValue("testFat");
        NutrientsValue carbohydrates = new NutrientsValue("testCarbo");
        NutrientsValue fiber = new NutrientsValue("testFiber");
        NutrientsValue protein = new NutrientsValue("testProtein");
        NutrientsValue calories = new NutrientsValue("testCalories");

        Nutrients nutrients = new Nutrients(fat, carbohydrates, fiber, protein, calories);
        return new FoodReport("testDesccription", "testIngredients", nutrients);
    }

    @Test
    void testGetFoodByBarcodeWithNullBarcode() {
        Response expected = Response.decline("Barcode of food can't be null or empty");
        Response actual = foodRepository.getFoodByBarcode(null);

        assertEquals(expected, actual,
            "Expected response is: " + expected.additionalInfo() + ", but was: " + actual.additionalInfo());
    }

    @Test
    void testGetFoodByBarcodeWithEmptyBarcode() {
        Response expected = Response.decline("Barcode of food can't be null or empty");
        Response actual = foodRepository.getFoodByBarcode("");

        assertEquals(expected, actual,
            "Expected response is: " + expected.additionalInfo() + ", but was: " + actual.additionalInfo());
    }

    @Test
    void testGetFoodByBarcodeWithBarcodeInCache() {
        Food foodNutella = new Food(1, "Nutella chocolate", "72527273070");

        when(foodCacheMock.getFoodInfoByBarcode("800700600")).thenReturn(foodNutella);

        Response expectedResponse = Response.ok(new FoodCollection(List.of(foodNutella)));
        Response actualResponse = foodRepository.getFoodByBarcode("800700600");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }

    @Test
    void testGetFoodByBarcodeWithoutBeingInCache() {
        when(foodCacheMock.getFoodInfoByBarcode("800700600")).thenReturn(null);

        Response expectedResponse = Response.nothingFound("This barcode isn't available");
        Response actualResponse = foodRepository.getFoodByBarcode("800700600");

        assertEquals(expectedResponse, actualResponse,
            "The expected response was: " + expectedResponse.additionalInfo() + ", but was: " +
                actualResponse.additionalInfo());
    }
}