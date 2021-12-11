package com.loadrfx.usecases;

import com.loadrfx.entities.Downloadable;
import com.loadrfx.entities.MyMediaVideo;
import com.loadrfx.entities.YoutubeVideo;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {

    private final Downloadable video;
    private String subtitles;

    public Downloader(Downloadable video) {
        this.video = video;
    }

    /**
     * Download video.
     * @param filename
     * @param format
     */
    public void downloadVideo(String filename, String format) {

        String[] cmd = new String[6];

        String[] setup = setupDownload(filename, format);

        // Sleep
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Build the download command
        if (video instanceof MyMediaVideo) {
            cmd = commandMyMedia(setup[0], setup[1]);
        } else if (video instanceof YoutubeVideo) {
            YoutubeVideo youtubeVideo = (YoutubeVideo) video;
        }

        // Download the video
        saveVideo(cmd);
    }

    /**
     * Download subtitles.
     * @param filename
     */
    public void downloadTranscript(String filename) {

        // Get user home directory
        String home = System.getProperty("user.home");
        Path savePath = Paths.get(home + "/Downloads/Loadr Downloads/" + filename);

        MyMediaVideo myMediaVideo = (MyMediaVideo) video;
        subtitles = myMediaVideo.getTranscriptLink();
        if (subtitles != null) {
            // Use a BufferedInputStream to read the file
            try {
                BufferedInputStream in = new BufferedInputStream(new URL(subtitles).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(savePath.toString() + ".vtt");
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Setup the download.
     * @param filename
     * @param format
     * @return
     */
    private String[] setupDownload(String filename, String format) {
        // Get cwd
        String cwd = System.getProperty("user.dir");
        // Get user home directory
        String home = System.getProperty("user.home");
        // Create Loadr Downloads directory if it doesn't exist
        Path downloads = Paths.get(home + "/Downloads/Loadr Downloads");
        if (!downloads.toFile().exists()) {
            downloads.toFile().mkdir();
        }
        // Set save path
        Path savePath = Paths.get(home + "/Downloads/Loadr Downloads/" + filename + "." + format);
        // Check if Windows or macOS
        String path = checkSystem(cwd, video.getClass().getSimpleName());

        // Return a list of Strings
        return new String[]{savePath.toString(), path};
    }

    /**
     * Save the video using the command.
     * @param cmd
     */
    private void saveVideo(String[] cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if Windows or macOS and the type of executable.
     * @param cwd
     * @param downloadType
     * @return
     */
    private String checkSystem(String cwd, String downloadType) {
        String path;
        if (System.getProperty("os.name").contains("Windows")) {

            if (downloadType.equals("MyMediaVideo")) {
                path = cwd + "\\src\\main\\java\\com\\loadrfx\\frameworks\\ffmpeg-win.exe";
            } else {
                // TODO: Add path for youtube-dl
                path = "";
            }

        } else {

            // Execute chmod on ffmpeg so it's executable
            try {
                String[] cmd = new String[]{"chmod", "+x", cwd + "/src/main/java/com/loadrfx/frameworks/ffmpeg-mac"};
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                InputStream is = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println(line);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            if (downloadType.equals("MyMediaVideo")) {
                path = cwd + "/src/main/java/com/loadrfx/frameworks/ffmpeg-mac";
            } else {
                // TODO: Add path for youtube-dl
                path = "";
            }

        }
        return path;
    }

    /**
     * Return the command-to-use if the video is from MyMedia.
     * @param filename
     * @param path
     * @return
     */
    private String[] commandMyMedia(String filename, String path) {
        String[] cmd;
        MyMediaVideo myMediaVideo = (MyMediaVideo) video;

        cmd = new String[]{path, "-i", myMediaVideo.getPlaylistLink(), "-codec", "copy", filename};
        return cmd;
    }
}
