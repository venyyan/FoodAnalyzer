package bg.sofia.uni.fmi.mjt.analyzer.server.repository;

import bg.sofia.uni.fmi.mjt.analyzer.server.Response;

public interface FoodRepositoryAPI {
    Response getFoodByName(String name);

    Response getFoodById(int id);

    Response getFoodByBarcode(String barcode);
}
