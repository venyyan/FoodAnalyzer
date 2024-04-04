package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.report;

import java.io.Serializable;

public record NutrientsValue(String value) implements Serializable {
    @Override
    public String toString() {
        return value;
    }
}
