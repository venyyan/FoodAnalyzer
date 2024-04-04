package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report;

import java.io.Serializable;

public record Nutrients(NutrientsValue fat,
                        NutrientsValue carbohydrates,
                        NutrientsValue fiber,
                        NutrientsValue protein,
                        NutrientsValue calories) implements Serializable {
    @Override
    public String toString() {
        return "[Nutrients: fat: " + fat + ", carbohydrates: "  + carbohydrates +
            ", fiber: " + fiber + ", protein: " + protein + ", calories: " + calories + "]";
    }
}
