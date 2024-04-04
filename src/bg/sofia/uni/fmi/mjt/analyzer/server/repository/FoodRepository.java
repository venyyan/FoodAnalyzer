package bg.sofia.uni.fmi.mjt.analyzer.server.repository;

import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.FoodAPIResponseException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.logger.Logger;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodInfoRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodReportRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.FoodRequest;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.Food;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.Response;

import java.util.List;

public class FoodRepository implements FoodRepositoryAPI {
    private final FoodRetriever retriever;
    private final FoodCache foodCache;

    public FoodRepository(FoodCache foodCache, FoodRetriever retriever) {
        this.retriever = retriever;
        this.foodCache = foodCache;
    }

    @Override
    public Response getFoodByName(String name) {
        if (name == null || name.isEmpty()) {
            return Response.decline("Name of food can't be null or empty");
        }

        FoodCollection foodInfosByName = foodCache.getFoodInfo(name);
        if (foodInfosByName != null) {
            return Response.ok(foodInfosByName);
        }

        FoodRequest request = new FoodInfoRequest(name);
        try {
            foodInfosByName = retriever.getFoodInfos(request);
        } catch (FoodAPIResponseException exception) {
            Logger.log(exception);
            return Response.decline(exception.getMessage());
        }

        if (foodInfosByName.foods().isEmpty()) {
            return Response.nothingFound("No results found");
        }

        foodCache.addFoodInfo(name, foodInfosByName);
        return Response.ok(foodInfosByName);
    }

    @Override
    public Response getFoodById(int id) {
        if (id < 0) {
            return Response.decline("Id of food can't be negative");
        }

        FoodReport foodReport = foodCache.getFoodReport(id);
        if (foodReport != null) {
            return Response.ok(foodReport);
        }

        FoodRequest request = new FoodReportRequest(id);
        try {
            foodReport = retriever.getFoodReport(request);
        } catch (FoodAPIResponseException exception) {
            Logger.log(exception);
            return Response.decline(exception.getMessage());
        }

        if (foodReport == null) {
            return Response.nothingFound("No results found");
        }

        foodCache.addFoodReport(id, foodReport);
        return Response.ok(foodReport);
    }

    @Override
    public Response getFoodByBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return Response.decline("Barcode of food can't be null or empty");
        }

        Food foodInfoByBarcode = foodCache.getFoodInfoByBarcode(barcode);
        if (foodInfoByBarcode != null) {
            return Response.ok(new FoodCollection(List.of(foodInfoByBarcode)));
        }

        return Response.nothingFound("This barcode isn't available");
    }
}
