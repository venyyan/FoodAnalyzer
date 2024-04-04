package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GetFoodByBarcodeCommandTest {
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        FoodCache foodCache = new FoodCache();
        FoodRetriever retriever = mock();
        commandFactory = CommandFactory.getInstance(foodCache, retriever);
    }

    @Test
    void testExecuteWithInvalidParamsImage() {
        String expected = "Invalid command";
        String actual = commandFactory.readLine("get-food-by-barcode --ima=someImage.gif");
        assertEquals(expected, actual, "Expected response is: " + expected + ", but was: " + actual);
    }

    @Test
    void testExecuteWithInvalidParamsCode() {
        String expected = "Invalid command";
        String actual = commandFactory.readLine("get-food-by-barcode --coda=0000000000");
        assertEquals(expected, actual, "Expected response is: " + expected + ", but was: " + actual);
    }

    @Test
    void testExecuteWithBarcodeNotInCache() {
        String expected = "This barcode isn't available";
        String actual = commandFactory.readLine("get-food-by-barcode --code=0000000000");
        assertEquals(expected, actual, "Expected response is: " + expected + ", but was: " + actual);
    }

    @Test
    void testExecuteWithInvalidBarcodeImage() {
        String expected = "Barcode image not found";
        String actual = commandFactory.readLine("get-food-by-barcode --img=notFound.gif");
        assertEquals(expected, actual, "Expected response is: " + expected + ", but was: " + actual);
    }
}