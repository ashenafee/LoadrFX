package com.loadrfx;

import com.loadrfx.entities.Format;
import com.loadrfx.entities.MyMediaVideo;
import com.loadrfx.usecases.Downloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        configureButtons(scene, stage);
        configureComboBoxes(scene);

        // Disable resizing
        stage.setResizable(false);

        stage.setTitle("Loadr");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Configure the combo boxes
     * @param scene
     */
    private void configureComboBoxes(Scene scene) {
        // Configure the provider combo box
        ComboBox<String> providerComboBox = (ComboBox<String>) scene.lookup("#providerCombo");
        providerComboBox.getItems().add("MyMedia");
        providerComboBox.getItems().add("Youtube");

        // Configure the format combo box
        ComboBox<Format> formatComboBox = (ComboBox<Format>) scene.lookup("#formatCombo");
        formatComboBox.getItems().add(Format.AVI);
        formatComboBox.getItems().add(Format.MKV);
        formatComboBox.getItems().add(Format.MOV);
        formatComboBox.getItems().add(Format.MP4);
        formatComboBox.getItems().add(Format.WMV);
    }

    /**
     * Configure the buttons
     * @param scene
     * @param stage
     */
    private void configureButtons(Scene scene, Stage stage) {
        // Configure exit button
        Button exitButton = (Button) scene.lookup("#exitButton");
        exitButton.setOnMouseClicked(event -> stage.close());

        // Configure clear button
        Button clearButton = (Button) scene.lookup("#clearButton");
        clearButton.setOnMouseClicked(event -> {clearButton(scene);} );

        // Configure download button
        Button downloadButton = (Button) scene.lookup("#downloadButton");
        downloadButton.setOnMouseClicked(event -> {downloadButton(scene);} );
        // TODO: Add a way to disable the download button until all inputs are filled
    }

    /**
     * Set the clear button to clear all inputs
     * @param scene
     */
    private void clearButton(Scene scene) {
        TextField videoInput = (TextField) scene.lookup("#videoInput");
        TextField filenameInput = (TextField) scene.lookup("#filenameInput");
        ComboBox<String> providerComboBox = (ComboBox<String>) scene.lookup("#providerCombo");
        ComboBox<Format> formatComboBox = (ComboBox<Format>) scene.lookup("#formatCombo");

        // TODO: Add a way to show the prompt
        providerComboBox.getSelectionModel().selectFirst();
        formatComboBox.getSelectionModel().selectFirst();

        videoInput.setText("");
        filenameInput.setText("");
    }

    /**
     * Set the download button to download the video
     * @param scene
     */
    private void downloadButton(Scene scene) {
        TextField videoInput = (TextField) scene.lookup("#videoInput");
        TextField filenameInput = (TextField) scene.lookup("#filenameInput");

        String videoUrl = videoInput.getText();
        String filename = filenameInput.getText();

        if (videoUrl.isEmpty() || filename.isEmpty()) {
            return;
        }

        // Get the provider and format
        ComboBox<String> providerComboBox = (ComboBox<String>) scene.lookup("#providerCombo");
        ComboBox<Format> formatComboBox = (ComboBox<Format>) scene.lookup("#formatCombo");

        if (providerComboBox.getValue().equals("Youtube")) {
            return;
        } else if (providerComboBox.getValue().equals("MyMedia")) {
            downloadMyMedia(scene, videoUrl, filename, formatComboBox);
        }
    }

    /**
     * Download the video from MyMedia on a separate thread
     * @param scene
     * @param videoUrl
     * @param filename
     * @param formatComboBox
     */
    private void downloadMyMedia(Scene scene, String videoUrl, String filename, ComboBox<Format> formatComboBox) {
        MyMediaVideo myMediaVideo = new MyMediaVideo(videoUrl);
        Downloader downloader = new Downloader(myMediaVideo);

        // Download the video on a new thread
        ProgressBar progressBar = (ProgressBar) scene.lookup("#downloadProgress");
        progressBar.setProgress(-1);

        // Make thread with runnable
        Thread videoThread = new Thread(() -> {
            downloader.downloadVideo(filename, formatComboBox.getValue().toString().toLowerCase());

            // Notify main thread that the download is complete
            Platform.runLater(() -> {
                // Set progress bar text
                progressBar.setProgress(0);
            });
        });
        videoThread.start();

        // Download transcript on a new thread
        Thread transcriptThread = new Thread(() -> downloader.downloadTranscript(filename));
        transcriptThread.start();
    }


    // MyMedia link: https://play.library.utoronto.ca/watch/38cf9e72a560dc4f4be8979c14ca57ea
    // YouTube link: https://youtu.be/tlTKTTt47WE
}
