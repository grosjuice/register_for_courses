package com.example.server;

/**
 * Launches the server via port 1337
 */
public class ServerLauncher {
    public final static int PORT = 1337;

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("com.example.server.Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}