package com.example.client_simple;

public class ClientLauncher  {
    public final static int PORT = 1337;
    static boolean firstRequest = true;

    public static void main(String[] args) {
        Client client;

        while (true) {
            try {
                client = new Client(PORT);
                client.run();
                firstRequest = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
