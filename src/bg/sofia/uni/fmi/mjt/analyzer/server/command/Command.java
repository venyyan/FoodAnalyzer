package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.BarcodeImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.UserInvalidCommandException;
import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepositoryAPI;

import java.io.IOException;

public interface Command {
    String execute(String params, FoodRepositoryAPI repository)
        throws IOException, UserInvalidCommandException, BarcodeImageNotFoundException;
}
