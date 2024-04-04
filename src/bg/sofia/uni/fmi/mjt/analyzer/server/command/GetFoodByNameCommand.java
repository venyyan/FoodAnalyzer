package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepositoryAPI;

public class GetFoodByNameCommand implements Command {
    @Override
    public String execute(String foodName, FoodRepositoryAPI repository) {
        return repository.getFoodByName(foodName).toString();
    }
}
