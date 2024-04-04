package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FoodReportRequest extends FoodRequest {
    protected static final String API_SEARCH_BY_ID = "food/";
    private final int id;

    public FoodReportRequest(int id) {
        this.id = id;
    }

    @Override
    public URI generateUri() throws URISyntaxException, IOException {
        String apiKey = getApiKey();
        String apiRequest = API_HOST + API_SEARCH_BY_ID + id + "?" + apiKey;
        return new URI(apiRequest);
    }
}
