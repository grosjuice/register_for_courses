package com.example.client_fx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Responsible for presenting the application's data to the user in a visual format. Defines the layout
 * of the user interface. Works in conjunction with the model and controller classes.
 */
public class GUIView extends Application {

    /**
     * The entry point for the application's main user interface. Method that is called when the application is
     * launched and is responsible for creating the main window or frame of the application and displaying it
     * on the screen.
     *
     * @param stage container that represents a window or frame on the screen
     * @throws IOException indicates an error or exception that might occur when loading the FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("interface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setTitle("Inscription UDEM");
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);  // resizing window option disabled

        stage.show();
    }

    /**
     * starts the Java FX application
     * @throws IOException indicates an error or exception that might occur when launching the JavaFX application
     */
    public static void run() throws IOException {
        launch();
    }

}
