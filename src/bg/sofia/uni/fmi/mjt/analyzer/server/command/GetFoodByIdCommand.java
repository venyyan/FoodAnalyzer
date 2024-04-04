package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepositoryAPI;

public class GetFoodByIdCommand implements Command {

    @Override
    public String execute(String foodId, FoodRepositoryAPI repository) {
        return repository.getFoodById(Integer.parseInt(foodId)).toString();
    }
}
