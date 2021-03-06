package com.loadrfx.usecases;

import com.loadrfx.entities.Downloadable;
import com.loadrfx.entities.MyMediaVideo;
import com.loadrfx.entities.YoutubeVideo;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.ZipFile;

public class Downloader {

    private final Downloadable video;

    public Downloader(Downloadable video) {
        this.video = video;
    }

    /**
     * Download video.
     * @param filename - name of the file
     * @param format - format of the file
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
            cmd = commandYoutube(setup[0], setup[1]);
        }

        // Download the video
        saveVideo(cmd);
    }

    /**
     * Download subtitles.
     * @param filename - name of the file
     */
    public void downloadTranscript(String filename) {

        // Get user home directory
        String home = System.getProperty("user.home");

        MyMediaVideo myMediaVideo = (MyMediaVideo) video;
        String subtitles = myMediaVideo.getTranscriptLink();
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
     * @param filename - name of the file
     * @param format - format of the file
     * @return - setup parameters
     */
    private String[] setupDownload(String filename, String format) {
        // Get cwd
        String cwd = System.getProperty("user.dir");
        // Check if Windows or macOS
        String path = checkSystem(video.getClass().getSimpleName());

        String savePath = filename + "." + format;
        // Return a list of Strings
        return new String[]{savePath, path};
    }

    /**
     * Save the video using the command.
     * @param cmd - command to execute
     */
    private void saveVideo(String[] cmd) {
        try {
            executeCommands(cmd);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the commands passed in.
     * @param cmd - command to execute
     * @throws IOException - IOException
     * @throws InterruptedException - InterruptedException
     */
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
     * @param downloadType - type of download
     * @return - path to the executable
     */
    private String checkSystem(String downloadType) {
        String path;

        // If frameworks folder doesn't exist, create it
        File file = new File("frameworks");
        if (!file.exists()) {
            file.mkdir();
        }

        // Unzip binaries from jar
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                InputStream ffmpeg = Downloader.class.getResourceAsStream("/com/loadrfx/frameworks/win/ffmpeg.exe");
                InputStream ytdlp = Downloader.class.getResourceAsStream("/com/loadrfx/frameworks/win/yt-dlp.exe");
                Files.copy(ffmpeg, new File("frameworks/ffmpeg.exe").toPath());
                Files.copy(ytdlp, new File("frameworks/yt-dlp.exe").toPath());
            } else {
                InputStream ffmpeg = Downloader.class.getResourceAsStream("/com/loadrfx/frameworks/mac/ffmpeg");
                InputStream ytdlp = Downloader.class.getResourceAsStream("/com/loadrfx/frameworks/mac/yt-dlp");
                Files.copy(ffmpeg, new File("frameworks/ffmpeg").toPath());
                Files.copy(ytdlp, new File("frameworks/yt-dlp").toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (System.getProperty("os.name").contains("Windows")) {

            if (downloadType.equals("MyMediaVideo")) {
               path =  ".\\frameworks\\ffmpeg.exe";
            } else {
                path = ".\\frameworks\\yt-dlp.exe";
            }

        } else {

            String binary;
            if (downloadType.equals("MyMediaVideo")) {
                //binary = Objects.requireNonNull(Downloader.class.getResource("/com/loadrfx/frameworks/mac/ffmpeg")).getPath();
                binary = "./frameworks/ffmpeg";
            } else {
                //binary = Objects.requireNonNull(Downloader.class.getResource("/com/loadrfx/frameworks/mac/yt-dlp")).getPath();
                binary = "./frameworks/yt-dlp";
            }

            chmodBinary(binary);

            if (downloadType.equals("MyMediaVideo")) {
                //path = Objects.requireNonNull(Downloader.class.getResource("/com/loadrfx/frameworks/mac/ffmpeg")).getPath();
                path = "./frameworks/ffmpeg";
            } else {
                //path = Objects.requireNonNull(Downloader.class.getResource("/com/loadrfx/frameworks/mac/yt-dlp")).getPath();
                path = "./frameworks/yt-dlp";
            }

        }
        return path;
    }

    /**
     * Change the permissions of the binary.
     * @param binary - binary to change permissions
     */
    private void chmodBinary(String binary) {
        try {
            String[] cmd = new String[]{"chmod", "+x", binary};
            executeCommands(cmd);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the command-to-use if the video is from MyMedia.
     * @param filename - name of the file
     * @param path - path of the file
     * @return - command to use
     */
    private String[] commandMyMedia(String filename, String path) {
        String[] cmd;
        MyMediaVideo myMediaVideo = (MyMediaVideo) video;

        cmd = new String[]{path, "-i", myMediaVideo.getPlaylistLink(), "-codec", "copy", filename};
        return cmd;
    }

    /**
     * Return the command-to-use if the video is from YouTube.
     * @param filename - name of the file
     * @param path - path of the file
     * @return - command to use
     */
    private String[] commandYoutube(String filename, String path) {
        String[] cmd;
        YoutubeVideo youtubeVideo = (YoutubeVideo) video;

        cmd = new String[]{path, "-o", filename.split("\\.")[0], "-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4", youtubeVideo.getLink()};
        return cmd;
    }
}
