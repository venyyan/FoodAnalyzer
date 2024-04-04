package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class FoodRequest {
    protected static final String API_HOST = "https://api.nal.usda.gov/fdc/v1/";
    protected static final String API_KEY_PATH = "apiKey.txt";
    protected static final String API_DELIMITER = "&";

    public abstract URI generateUri() throws URISyntaxException, IOException;

    protected String getApiKey() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(API_KEY_PATH))) {
            return reader.readLine();
        }
    }
}
