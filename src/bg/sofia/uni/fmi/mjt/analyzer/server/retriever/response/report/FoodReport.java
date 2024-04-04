package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report;

import java.io.Serializable;

public record FoodReport(String description, String ingredients, Nutrients labelNutrients) implements Serializable  {
    @Override
    public String toString() {
        return '[' + description + ", " + ingredients + ", " + labelNutrients.toString() + ']';
    }
}