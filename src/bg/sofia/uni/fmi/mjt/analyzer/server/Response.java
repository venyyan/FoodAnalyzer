package bg.sofia.uni.fmi.mjt.analyzer.server;

import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info.FoodCollection;

public record Response(Status status, String additionalInfo, FoodCollection foods, FoodReport food) {
    private enum Status {
        OK, DECLINED, NOTHING_FOUND
    }

    public static Response ok(FoodCollection foods) {
        return new Response(Status.OK, "", foods, null);
    }

    public static Response ok(FoodReport food) {
        return new Response(Status.OK, "", null, food);
    }

    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, null, null);
    }

    public static Response nothingFound(String message) {
        return new Response(Status.NOTHING_FOUND, message, null, null);
    }

    @Override
    public String toString() {
        if (status.equals(Status.OK)) {
            return foods != null ? foods.toString() : food.toString();
        } else {
            return additionalInfo;
        }
    }
}