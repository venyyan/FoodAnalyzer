package bg.sofia.uni.fmi.mjt.analyzer.server.retriever;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.FoodAPIResponseException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.Nutrients;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.NutrientsValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FoodRetrieverTest {
    @Mock
    private HttpClient httpClientMock;
    @Mock
    private HttpResponse<String> httpResponseMock;
    @InjectMocks
    private FoodRetriever retriever;

    @Test
    void testGetFoodInfosEmptyCollection()
        throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("{foods:[]}");

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);
        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://testUri.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodCollection actualCollection = retriever.getFoodInfos(foodRequest);
        FoodCollection expectedCollection = new FoodCollection(List.of());

        assertIterableEquals(expectedCollection.foods(), actualCollection.foods(),
            "Expected size of the collection is 0, but was: " + actualCollection.foods().size());
    }

    @Test
    void testGetFoodInfos() throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body())
            .thenReturn("{foods:[{\"fdcId\": 111111, \"description\": \"RAFFAELLO, ALMOND COCONUT TREAT\"," +
                " \"gtinUpc\": \"009800146130\"}]}");

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);
        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://testUri.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodCollection actualCollection = retriever.getFoodInfos(foodRequest);
        FoodCollection expectedCollection =
            new FoodCollection(List.of(new Food(111111, "RAFFAELLO, ALMOND COCONUT TREAT", "009800146130")));

        assertEquals(expectedCollection, actualCollection,
            "Expected size is: " + expectedCollection.foods().size() +
                ", but was: " + actualCollection.foods().size());
    }

    @Test
    void testGetFoodReportNonExistingId() throws IOException, InterruptedException, URISyntaxException {
        when(httpResponseMock.statusCode()).thenReturn(400);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRetriever foodRetriever = new FoodRetriever(httpClientMock);
        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        assertThrows(FoodAPIResponseException.class, () -> foodRetriever.getFoodReport(foodRequest),
            "FoodAPIResponseException expected to be thrown!");
    }

    @Test
    void testGetFoodReportError404() throws IOException, InterruptedException, URISyntaxException {
        when(httpResponseMock.statusCode()).thenReturn(404);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRetriever foodRetriever = new FoodRetriever(httpClientMock);
        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        assertThrows(FoodAPIResponseException.class, () -> foodRetriever.getFoodReport(foodRequest),
            "FoodAPIResponseException expected to be thrown!");
    }

    @Test
    void testGetFoodReportWithValidResponse() throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("""
            {"description":"NUTELLA FERRERO WITH BREADSTICKS",
             "ingredients":"NUTELLA: SUGAR, PALM OIL, HAZELNUTS, COCOA, SKIM MILK, WHEY (MILK), LECITHIN AS EMULSIFIER (SOY), VANILLIN: AN ARTIFICIAL FLAVOR.",
             "labelNutrients":{
             "fat":{"value":14.0},
             "carbohydrates":{"value":34.0},
             "fiber":{"value":1.98},
             "protein":{"value":4.00},
             "calories":{"value":270}
             }
            }
            """);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodReport actualCollection = retriever.getFoodReport(foodRequest);

        Nutrients labelNutrients =
            new Nutrients(new NutrientsValue("14.0"), new NutrientsValue("34.0"), new NutrientsValue("1.98"),
                new NutrientsValue("4.00"), new NutrientsValue("270"));
        FoodReport expectedCollection = new FoodReport("NUTELLA FERRERO WITH BREADSTICKS",
            "NUTELLA: SUGAR, PALM OIL, HAZELNUTS, COCOA, SKIM MILK, WHEY (MILK), LECITHIN AS EMULSIFIER (SOY), VANILLIN:" +
                " AN ARTIFICIAL FLAVOR.", labelNutrients);

        assertEquals(actualCollection, expectedCollection, "Get food report not working properly. Expected: " +
            ", but was: " + actualCollection);
    }

    @Test
    void testGetFoodReportWithNoInfoAboutNutrientsAndInfo() throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("""
            {"description":"NUTELLA FERRERO WITH BREADSTICKS"}
            """);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodReport actualCollection = retriever.getFoodReport(foodRequest);

        Nutrients labelNutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));
        FoodReport expectedCollection = new FoodReport("NUTELLA FERRERO WITH BREADSTICKS",
            "No information provided", labelNutrients);

        assertEquals(actualCollection, expectedCollection, "Get food report not working properly. Expected: " +
            ", but was: " + actualCollection);
    }

    @Test
    void testGetFoodReportWithNoInfoAboutNutrients() throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("""
            {"description":"NUTELLA FERRERO WITH BREADSTICKS",
            "ingredients":"NUTELLA: SUGAR, PALM OIL, HAZELNUTS, COCOA, SKIM MILK, WHEY (MILK), LECITHIN AS EMULSIFIER (SOY), VANILLIN: AN ARTIFICIAL FLAVOR."
            }
            """);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodReport actualCollection = retriever.getFoodReport(foodRequest);

        Nutrients labelNutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));
        FoodReport expectedCollection = new FoodReport("NUTELLA FERRERO WITH BREADSTICKS",
            "NUTELLA: SUGAR, PALM OIL, HAZELNUTS, COCOA, SKIM MILK, WHEY (MILK), LECITHIN AS EMULSIFIER (SOY), VANILLIN: " +
                "AN ARTIFICIAL FLAVOR.", labelNutrients);

        assertEquals(actualCollection, expectedCollection, "Get food report not working properly. Expected: " +
            ", but was: " + actualCollection);
    }

    @Test
    void testGetFoodReportWithNoInfoAboutIngredients() throws IOException, InterruptedException, URISyntaxException, FoodAPIResponseException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("""
            {"description":"NUTELLA FERRERO WITH BREADSTICKS",
            "labelNutrients":{
             "fat":{"value":14.0},
             "carbohydrates":{"value":34.0},
             "fiber":{"value":1.98},
             "protein":{"value":4.00},
             "calories":{"value":270}
             }
            }
            """);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponseMock);

        FoodRequest foodRequest = Mockito.mock(FoodRequest.class);

        URI mockUri = new URI("https://example.com");
        when(foodRequest.generateUri()).thenReturn(mockUri);

        FoodReport actualCollection = retriever.getFoodReport(foodRequest);

        Nutrients labelNutrients =
            new Nutrients(new NutrientsValue("14.0"), new NutrientsValue("34.0"), new NutrientsValue("1.98"),
                new NutrientsValue("4.00"), new NutrientsValue("270"));
        FoodReport expectedCollection = new FoodReport("NUTELLA FERRERO WITH BREADSTICKS",
            "No information provided", labelNutrients);

        assertEquals(actualCollection, expectedCollection, "Get food report not working properly. Expected: " +
            ", but was: " + actualCollection);
    }
}