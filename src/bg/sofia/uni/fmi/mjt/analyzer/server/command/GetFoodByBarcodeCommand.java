package bg.sofia.uni.fmi.mjt.analyzer.server.command;

import bg.sofia.uni.fmi.mjt.analyzer.server.exception.BarcodeImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.UserInvalidCommandException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.logger.Logger;
import bg.sofia.uni.fmi.mjt.analyzer.server.repository.FoodRepositoryAPI;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.barcode.BarcodeDecoder;
import com.google.zxing.NotFoundException;

import java.io.IOException;

public class GetFoodByBarcodeCommand implements Command {
    private static final String COMMAND_INVALID = "Invalid command";
    private static final String BARCODE_DELIMITER = "\\|";
    private static final String BARCODE_BY_CODE_COMMAND = "--code";
    private static final String BARCODE_BY_IMG_COMMAND = "--img";
    @Override
    public String execute(String params, FoodRepositoryAPI repository)
        throws UserInvalidCommandException, BarcodeImageNotFoundException {
        String barcode = getBarcode(params);

        if (barcode != null) {
            return repository.getFoodByBarcode(barcode).toString();
        } else {
            throw new UserInvalidCommandException(COMMAND_INVALID);
        }
    }

    private String getBarcode(String params) throws BarcodeImageNotFoundException {
        String[] splitParams = params.trim().split(BARCODE_DELIMITER);
        String barcode = null;
        for (String param : splitParams) {
            if (param.contains(BARCODE_BY_CODE_COMMAND)) {
                barcode = extractBarcode(param);
            } else if (param.contains(BARCODE_BY_IMG_COMMAND)) {
                barcode = getBarcodeByImage(param);
            }
        }
        return barcode;
    }

    private String getBarcodeByImage(String param) throws BarcodeImageNotFoundException {
        String barcodePath = extractBarcode(param);
        try {
            return BarcodeDecoder.getQrCode(barcodePath);
        } catch (NotFoundException | IOException notFoundException) {
            Logger.log(notFoundException);
            throw new BarcodeImageNotFoundException("Barcode image not found", notFoundException);
        }
    }

    private String extractBarcode(String command) {
        int startIndex = command.indexOf('=') + 1;
        return command.substring(startIndex);
    }
}
