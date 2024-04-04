package bg.sofia.uni.fmi.mjt.analyzer.server.retriever;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.FoodAPIResponseException;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.Nutrients;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.NutrientsValue;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FoodRetriever {
    private static final int ERROR_CODE_400 = 400;
    private static final int ERROR_CODE_404 = 404;
    private static final int ERROR_CODE_403 = 403;
    private final HttpClient httpClient;
    private static final Gson GSON = new Gson();

    public FoodRetriever(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public FoodCollection getFoodInfos(FoodRequest userRequest) throws FoodAPIResponseException {
        HttpResponse<String> response = getRetHttpResponse(userRequest);

        return GSON.fromJson(response.body(), FoodCollection.class);
    }

    public FoodReport getFoodReport(FoodRequest userRequest) throws FoodAPIResponseException {
        HttpResponse<String> response = getRetHttpResponse(userRequest);
        FoodReport foodReport = GSON.fromJson(response.body(), FoodReport.class);

        Nutrients labelNutrients =
            new Nutrients(new NutrientsValue("0"), new NutrientsValue("0"), new NutrientsValue("0"),
                new NutrientsValue("0"), new NutrientsValue("0"));

        if (foodReport.ingredients() == null && foodReport.labelNutrients() == null) {
            return new FoodReport(foodReport.description(), "No information provided",
                labelNutrients);
        } else if (foodReport.labelNutrients() == null) {
            return new FoodReport(foodReport.description(), foodReport.ingredients(),
                labelNutrients);
        } else if (foodReport.ingredients() == null) {
            return new FoodReport(foodReport.description(), "No information provided",
                foodReport.labelNutrients());
        }

        return foodReport;
    }

    private HttpResponse<String> getRetHttpResponse(FoodRequest userRequest) throws FoodAPIResponseException {
        HttpResponse<String> response;

        try {
            HttpRequest httpRequest =
                HttpRequest.newBuilder().uri(userRequest.generateUri()).build();
            response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException exception) {
            throw new FoodAPIResponseException("Unable to retrieve information from service. Please contact an admin",
                exception);
        }

        if (response.statusCode() == ERROR_CODE_400) {
            throw new FoodAPIResponseException("Error 400 while trying to get food");
        } else if (response.statusCode() == ERROR_CODE_403) {
            throw new FoodAPIResponseException("Error 403 while trying to get food - invalid authorization");
        } else if (response.statusCode() == ERROR_CODE_404) {
            throw new FoodAPIResponseException("Error 404 while trying to get food");
        }

        return response;
    }
}
