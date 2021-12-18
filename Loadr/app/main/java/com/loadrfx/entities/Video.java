package com.loadrfx.entities;

public class Video {

    protected String link;
    private Format format;

    /**
     * Construct a new entities.Video object given a link.
     * @param link - The URL for the video.
     */
    public Video(String link) {
        this.link = link;
    }

    /**
     * Get the URL for the video.
     * @return The URL for the video.
     */
    public String getLink() {
        return link;
    }

    /**
     * Get the format for the Video.
     * @return - The format of the video.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Set the format for the Video.
     * @param format - The format of the video.
     */
    public void setFormat(Format format) {
        this.format = format;
    }
}
