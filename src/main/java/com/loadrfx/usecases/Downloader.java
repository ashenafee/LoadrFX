package com.loadrfx.usecases;

import com.loadrfx.entities.Downloadable;
import com.loadrfx.entities.MyMediaVideo;
import com.loadrfx.entities.YoutubeVideo;

import java.io.*;
import java.net.URL;

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
            cmd = commandYoutube(setup[0], setup[1], format);
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

        MyMediaVideo myMediaVideo = (MyMediaVideo) video;
        subtitles = myMediaVideo.getTranscriptLink();
        if (subtitles != null) {
            // Use a BufferedInputStream to read the file
            try {
                BufferedInputStream in = new BufferedInputStream(new URL(subtitles).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(filename + ".vtt");
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
        // Check if Windows or macOS
        String path = checkSystem(cwd, video.getClass().getSimpleName());

        String savePath = filename + "." + format;
        // Return a list of Strings
        return new String[]{savePath, path};
    }

    /**
     * Save the video using the command.
     * @param cmd
     */
    private void saveVideo(String[] cmd) {
        try {
            executeCommands(cmd);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeCommands(String[] cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null)
            System.out.println(line);
        process.waitFor();
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

            String binary = "";
            if (downloadType.equals("MyMediaVideo")) {
                binary = "/src/main/java/com/loadrfx/frameworks/mac/ffmpeg";
            } else {
                binary = "/src/main/java/com/loadrfx/frameworks/mac/yt-dlp";
            }

            // Execute chmod on ffmpeg/yt-dlp so it's executable
            chmodBinary(cwd, binary);

            if (downloadType.equals("MyMediaVideo")) {
                path = cwd + "/src/main/java/com/loadrfx/frameworks/mac/ffmpeg";
            } else {
                path = cwd + "/src/main/java/com/loadrfx/frameworks/mac/yt-dlp";
            }

        }
        return path;
    }

    private void chmodBinary(String cwd, String binary) {
        try {
            String[] cmd = new String[]{"chmod", "+x", cwd + binary};
            executeCommands(cmd);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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

    private String[] commandYoutube(String filename, String path, String format) {
        String[] cmd;
        YoutubeVideo youtubeVideo = (YoutubeVideo) video;

        cmd = new String[]{path, "-o", filename, "-S", "res:1080", youtubeVideo.getLink()};
        return cmd;
    }
}
