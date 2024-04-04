package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.response.info;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record Food(@SerializedName("fdcId") int id,
                   String description,
                   @SerializedName("gtinUpc") String upc) implements Serializable {
    @Override
    public String toString() {
        return "[Food: " + id + ", " + description + ", " + upc + "]";
    }
}
