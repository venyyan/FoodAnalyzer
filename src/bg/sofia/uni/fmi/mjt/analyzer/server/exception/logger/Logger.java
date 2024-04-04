package bg.sofia.uni.fmi.mjt.analyzer.server.exception.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "ExceptionLog.txt";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static void log(Throwable exception) {
        String formattedMessage = formatMessage(exception);
        writeLog(formattedMessage);
    }

    private static String formatMessage(Throwable exception) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        String timestamp = dateFormat.format(new Date());

        StringBuilder formattedMessage = new StringBuilder();
        formattedMessage.append("[").append(timestamp).append("] ")
            .append(exception.getMessage()).append(System.lineSeparator());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            formattedMessage.append("\t").append(stackTraceElement.toString()).append(System.lineSeparator());
        }
        return formattedMessage.toString();
    }

    private static void writeLog(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
        } catch (IOException ioException) {
            Logger.log(ioException);
            System.err.println("Error writing exception to file: " + ioException.getMessage());
        }
    }
}
