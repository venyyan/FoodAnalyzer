package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info;

import java.io.Serializable;
import java.util.Collection;

public record FoodCollection(Collection<Food> foods) implements Serializable {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Food food : foods) {
            builder.append(food.toString());
        }
        return builder.toString();
    }
}
