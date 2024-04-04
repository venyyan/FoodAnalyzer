package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class CommandFactoryTest {
    private CommandFactory commandFactory;
    private final FoodCache foodCache = mock();
    private final FoodRetriever foodRetriever = mock();

    @BeforeEach
    void setUp() {
        commandFactory = CommandFactory.getInstance(foodCache, foodRetriever);
    }

    @Test
    void testSingletonInstance() {
        CommandFactory factory2 = CommandFactory.getInstance(foodCache, foodRetriever);
        assertSame(commandFactory, factory2,
            "The CommandFactory uses the Singleton design pattern and the two instances should be the same");
    }

    @Test
    void testReadLineInvalidCommand() {
        String expectedString = "Invalid command";
        String actual = commandFactory.readLine("get-food");
        assertEquals(expectedString, actual, "Expected: " + expectedString + ", but was: " + actual);
    }
}