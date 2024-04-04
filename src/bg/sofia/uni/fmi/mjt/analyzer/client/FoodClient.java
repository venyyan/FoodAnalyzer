package bg.sofia.uni.fmi.mjt.analyzer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class FoodClient {
    private static final int SERVER_PORT = 5214;
    private static final String CS_NAME = "UTF-8";
    private static final String HOSTNAME = "localhost";

    private static final String ENTRY_MESSAGE = """
                Welcome to FoodAnalyzer app!
                Available commands:\s
                    ~get-food /food name/~ to get food info by name
                    ~get-food-report /food id/~ to get food report by id
                    ~get-food-by-barcode --img=/image path/~ to get food info by barcode via image
                    ~get-food-by-barcode --code=/upc code/~ to get food info by barcode via upc
                """;

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, CS_NAME));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, CS_NAME), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(HOSTNAME, SERVER_PORT));

            System.out.println(ENTRY_MESSAGE);

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if ("quit".equals(message)) {
                    break;
                }

                writer.println(message);

                String reply = reader.readLine();
                System.out.println(reply);
            }
        } catch (IOException e) {
            System.out.println("The server has shut down. Please contact an admin");
        }
    }
}
