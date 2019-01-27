package com.mpr.main.java.server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LogManager.getLogger("Server Logger");
    private final int port;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Starting server on port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            logger.warn("Error while starting the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(Integer.parseInt(args[0]));
        server.start();
    }
}