package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoodInfoRequestTest {

    @Test
    void testGenerateUriWithoutParams() throws URISyntaxException, IOException {
        FoodInfoRequest request = new FoodInfoRequest("");

        String expected = URI.create(
                "https://api.nal.usda.gov/fdc/v1/foods/search?query=&api_key=dskl3ok3c80wgU0xjgb1OKBc1etkqMOduKgBydlP")
            .toString();

        String actual = request.generateUri().toString();

        assertEquals(expected, actual, "Expected uri is: " + expected + ", but was: " + actual);
    }

    @Test
    void testGenerateUriWithOneParameter() throws URISyntaxException, IOException {
        FoodInfoRequest request = new FoodInfoRequest("nutella");

        String expected = URI.create(
                "https://api.nal.usda.gov/fdc/v1/foods/search?query=nutella&api_key=dskl3ok3c80wgU0xjgb1OKBc1etkqMOduKgBydlP")
            .toString();

        String actual = request.generateUri().toString();

        assertEquals(expected, actual, "Expected uri is: " + expected + ", but was: " + actual);
    }

    @Test
    void testGenerateUriWithMultipleParameters() throws URISyntaxException, IOException {
        FoodInfoRequest request = new FoodInfoRequest("nutella chocolate");

        String expected = URI.create(
                "https://api.nal.usda.gov/fdc/v1/foods/search?query=nutella%20chocolate&api_key=dskl3ok3c80wgU0xjgb1OKBc1etkqMOduKgBydlP")
            .toString();

        String actual = request.generateUri().toString();

        assertEquals(expected, actual, "Expected uri is: " + expected + ", but was: " + actual);
    }
}