package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.BarcodeImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.UserInvalidCommandException;
import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepository;
import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepositoryAPI;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;

import java.io.IOException;

public class CommandFactory {
    private static final String COMMAND_GET_FOOD = "get-food";
    private static final String COMMAND_GET_FOOD_REPORT = "get-food-report";
    private static final String COMMAND_GET_FOOD_BARCODE = "get-food-by-barcode";
    private static final String COMMAND_INVALID = "Invalid command";

    private static final int MIN_COMMAND_LENGTH = 2;

    private static final int COMMAND_NAME_ID = 0;
    private static final int COMMAND_PARAMS_ID = 1;

    private static final String LINE_DELIMITER = "\\s+";

    private final FoodRepositoryAPI repository;

    private static CommandFactory instance;

    private CommandFactory(FoodCache foodCache, FoodRetriever retriever) {
        this.repository = new FoodRepository(foodCache, retriever);
    }

    public static synchronized CommandFactory getInstance(FoodCache foodCache, FoodRetriever retriever) {
        if (instance == null) {
            instance = new CommandFactory(foodCache, retriever);
        }
        return instance;
    }

    public String readLine(String line)  {
        String[] splitLine = line.trim().split(LINE_DELIMITER);

        try {
            if (splitLine.length < MIN_COMMAND_LENGTH) {
                throw new UserInvalidCommandException(COMMAND_INVALID);
            }

            Command command = getCommand(splitLine[COMMAND_NAME_ID]);
            return command.execute(splitLine[COMMAND_PARAMS_ID], repository);

        } catch (UserInvalidCommandException | BarcodeImageNotFoundException | IOException invalidCommandException) {
            return invalidCommandException.getMessage();
        }
    }

    private Command getCommand(String commandName) throws UserInvalidCommandException {
        Command command;
        switch(commandName) {
            case COMMAND_GET_FOOD -> command = new GetFoodByNameCommand();

            case COMMAND_GET_FOOD_REPORT -> command = new GetFoodByIdCommand();

            case COMMAND_GET_FOOD_BARCODE -> command = new GetFoodByBarcodeCommand();

            default -> throw new UserInvalidCommandException(COMMAND_INVALID);
        }
        return command;
    }
}
