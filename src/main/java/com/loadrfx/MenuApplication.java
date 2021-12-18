package com.loadrfx;

import com.loadrfx.entities.Format;
import com.loadrfx.entities.MyMediaVideo;
import com.loadrfx.entities.YoutubeVideo;
import com.loadrfx.usecases.Downloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
     * @param scene The scene to configure
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

        // Disable formatComboBox if Youtube is selected
        BooleanBinding youtubeSelected = Bindings.createBooleanBinding(() ->
                providerComboBox.getValue().equals("Youtube"), providerComboBox.valueProperty());
        formatComboBox.disableProperty().bind(youtubeSelected);
    }

    /**
     * Configure the buttons
     * @param scene The scene to configure
     * @param stage The stage to configure
     */
    private void configureButtons(Scene scene, Stage stage) {
        // Configure exit button
        Button exitButton = (Button) scene.lookup("#exitButton");
        exitButton.setOnMouseClicked(event -> stage.close());

        // Configure clear button
        Button clearButton = (Button) scene.lookup("#clearButton");
        clearButton.setOnMouseClicked(event -> clearButton(scene));

        // Configure download button
        Button downloadButton = (Button) scene.lookup("#downloadButton");
        downloadButton.setOnMouseClicked(event -> downloadButton(scene));
        disableDownload(scene, downloadButton);

        // Configure location button
        Button locationButton = (Button) scene.lookup("#locationButton");
        locationButton.setOnMouseClicked(event -> locationButton(scene));
    }

    /**
     * Wait for all fields to be filled before enabling the download button.
     * @param scene The scene to configure
     * @param downloadButton The download button to configure
     */
    private void disableDownload(Scene scene, Button downloadButton) {
        TextField videoInput = (TextField) scene.lookup("#videoInput");
        TextField filenameInput = (TextField) scene.lookup("#filenameInput");
        ComboBox<String> providerComboBox = (ComboBox<String>) scene.lookup("#providerCombo");
        ComboBox<Format> formatComboBox = (ComboBox<Format>) scene.lookup("#formatCombo");

        BooleanBinding videoInputBinding = Bindings.createBooleanBinding(() ->
                !videoInput.getText().isEmpty(), videoInput.textProperty());

        BooleanBinding filenameInputBinding = Bindings.createBooleanBinding(() ->
                !filenameInput.getText().isEmpty(), filenameInput.textProperty());

        BooleanBinding providerComboBoxBinding = Bindings.createBooleanBinding(() ->
                providerComboBox.getItems().contains(providerComboBox.getValue()), providerComboBox.valueProperty());

        BooleanBinding formatComboBoxBinding = Bindings.createBooleanBinding(() ->
                formatComboBox.getItems().contains(formatComboBox.getValue()) ||
                        !formatComboBox.isDisabled(), formatComboBox.valueProperty());

        downloadButton.disableProperty().bind(videoInputBinding.not()
                .or(filenameInputBinding.not())
                .or(providerComboBoxBinding.not())
                .or(formatComboBoxBinding.not()));
    }

    /**
     * Sets the location button to open a file chooser
     * @param scene The scene to configure
     */
    private void locationButton(Scene scene) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Download Location");
        // Print path chosen in file chooser
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            // Set the location text field
            TextField filenameInput = (TextField) scene.lookup("#filenameInput");
            filenameInput.setText(file.getAbsolutePath());
        }

    }

    /**
     * Set the clear button to clear all inputs
     * @param scene The scene to configure
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
     * @param scene The scene to configure
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
            downloadYoutube(scene, videoUrl, filename);
        } else if (providerComboBox.getValue().equals("MyMedia")) {
            downloadMyMedia(scene, videoUrl, filename, formatComboBox);
        }
    }

    /**
     * Download the video from MyMedia on a separate thread
     * @param scene The scene to configure
     * @param videoUrl The video url
     * @param filename The filename
     * @param formatComboBox The format combo box
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

    /**
     * Download the video from YouTube on a separate thread
     * @param scene The scene to configure
     * @param videoUrl The video url
     * @param filename The filename
     */
    private void downloadYoutube(Scene scene, String videoUrl, String filename) {
        YoutubeVideo myMediaVideo = new YoutubeVideo(videoUrl);
        Downloader downloader = new Downloader(myMediaVideo);

        // Download the video on a new thread
        ProgressBar progressBar = (ProgressBar) scene.lookup("#downloadProgress");
        progressBar.setProgress(-1);

        // Make thread with runnable
        Thread videoThread = new Thread(() -> {
            downloader.downloadVideo(filename, "mp4");

            // Notify main thread that the download is complete
            Platform.runLater(() -> {
                // Set progress bar text
                progressBar.setProgress(0);
            });
        });
        videoThread.start();
    }


    // MyMedia link: https://play.library.utoronto.ca/watch/38cf9e72a560dc4f4be8979c14ca57ea
    // YouTube link: https://youtu.be/tlTKTTt47WE
}
