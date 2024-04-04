package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class FoodInfoRequest extends FoodRequest {
    private static final String API_SEARCH_BY_NAME = "foods/search?query=";
    private static final String API_KEYWORDS_AND = "%20";

    private static final String NAME_DELIMITER = " ";

    private final Collection<String> foodName;
    public FoodInfoRequest(String name) {
        this.foodName = Arrays.stream(name.trim().split(NAME_DELIMITER)).toList();
    }

    @Override
    public URI generateUri() throws URISyntaxException, IOException {
        String apiKey = getApiKey();
        String uri = API_HOST + API_SEARCH_BY_NAME + String.join(API_KEYWORDS_AND, foodName) +
            API_DELIMITER + apiKey;
        return new URI(uri);
    }
}
