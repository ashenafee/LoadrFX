package com.loadrfx.entities;

public class YoutubeVideo extends Video implements Downloadable {

    private String videoTitle;

    /**
     * Construct a new YouTube Video object given a link.
     *
     * @param link - The URL for the video.
     */
    public YoutubeVideo(String link) {
        super(link);
    }

    /**
     * Get the title of the video.
     * @return - The title of the video.
     */
    public String getTitle() {
        return videoTitle;
    }

    /**
     * Grab the metadata for the video.
     */
    private void loadVideo() {
        // Get video title

        // Get video upload date

        // Get video description

        // Get video duration
        return;
    }
}
