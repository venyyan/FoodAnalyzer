package bg.sofia.uni.fmi.mjt.analyzer.server;

import bg.sofia.uni.fmi.mjt.analyzer.server.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.analyzer.server.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.CacheFileNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.server.exception.logger.Logger;
import bg.sofia.uni.fmi.mjt.analyzer.server.retriever.FoodRetriever;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;

public class FoodServer {
    public static final int SERVER_PORT = 5214;
    private static final int BUFFER_SIZE = 8192;
    private static final String SERVER_HOST = "localhost";
    private static final String CACHE_FILE_NAME = "cacheName.txt";
    private static final String STOP_COMMAND = "stop";

    private Selector selector;

    private final CommandFactory commandFactory;
    private final FoodCache foodCache;
    private boolean isRunning = true;

    private void terminate() {
        Thread inputThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (isRunning) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase(STOP_COMMAND)) {
                        isRunning = false;
                        if (selector != null) {
                            selector.wakeup();
                        }
                        break;
                    }
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private boolean checkIfFileIsEmpty() throws CacheFileNotFoundException {
        try {
            if (Files.size(Paths.get(FoodServer.CACHE_FILE_NAME)) == 0) {
                return true;
            }
        } catch (IOException ioException) {
            Logger.log(ioException);
            throw new CacheFileNotFoundException("Warning! Cache cannot be read from file", ioException);
        }
        return false;
    }

    public FoodServer() {
        terminate();
        this.foodCache = new FoodCache();
        FoodRetriever retriever = new FoodRetriever(HttpClient.newHttpClient());
        this.commandFactory = CommandFactory.getInstance(foodCache, retriever);

        try {
            if (!checkIfFileIsEmpty()) {
                try (InputStream inputStream = new FileInputStream(CACHE_FILE_NAME)) {
                    foodCache.readCacheFromFile(inputStream);
                } catch (FileNotFoundException fileNotFoundException) {
                    throw new CacheFileNotFoundException("Warning! Can't find cache file", fileNotFoundException);
                } catch (IOException ioException) {
                    throw new CacheFileNotFoundException("Error! Unable to cast read object to CacheData", ioException);
                }
            }
        } catch (CacheFileNotFoundException cacheFileNotFoundException) {
            Logger.log(cacheFileNotFoundException);
            System.out.println("Warning! Cache cannot be read from file. Please contact an admin");
        }
    }

    private void serviceThread(Selector selector, ByteBuffer buffer) throws IOException {
        while (isRunning) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                System.out.println("There aren't any ready channels");
                continue;
            }

            handleSelectedKeys(selector, buffer);
        }
    }

    private void handleSelectedKeys(Selector selector, ByteBuffer buffer) throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isReadable()) {
                try {
                    handleReadableKey(key, buffer);
                } catch (IOException exception) {
                    Logger.log(exception);
                }

            } else if (key.isAcceptable()) {
                handleAcceptableKey(key, selector);
            }
            keyIterator.remove();
        }
    }

    private void handleReadableKey(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        String request = getRequest(buffer, socketChannel);

        if (request == null) {
            return;
        }

        String response = commandFactory.readLine(request);

        buffer.clear();
        buffer.put((response + System.lineSeparator()).getBytes());
        buffer.flip();

        socketChannel.write(buffer);
        buffer.clear();
    }

    private String getRequest(ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int r = socketChannel.read(buffer);
        if (r < 0) {
            System.out.println("Client has closed the connection");

            try (OutputStream outputStream = new FileOutputStream(CACHE_FILE_NAME)) {
                foodCache.writeCacheToFile(outputStream);
            } catch (CacheFileNotFoundException cacheFileNotFoundException) {
                Logger.log(cacheFileNotFoundException);
                System.out.println("Warning! Cache cannot be written to file. Please contact an admin");
            }
            socketChannel.close();
            return null;
        }
        buffer.flip();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    private void handleAcceptableKey(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            serviceThread(selector, buffer);
        } catch (IOException serverException) {
            Logger.log(serverException);
            System.out.println("Error while trying to create server. Please contact an admin");
        }
    }

    public static void main(String[] args) {
        FoodServer server = new FoodServer();
        server.start();
    }
}
