package com.example.server;

@FunctionalInterface
/**
 * com.example.server.EventHandler interface. Implements handle method
 */
public interface EventHandler {
    void handle(String cmd, String arg);
}
