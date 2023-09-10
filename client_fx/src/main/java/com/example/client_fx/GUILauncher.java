package com.example.client_fx;

import java.io.IOException;

/**
 * launches the GUIView
 */
public class GUILauncher {

    public static void main(String[] args) {
        try {
            GUIView.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
