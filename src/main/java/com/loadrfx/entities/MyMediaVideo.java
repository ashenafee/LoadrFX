package com.loadrfx.entities;

public class MyMediaVideo extends Video implements Downloadable {

    private String videoId;
    private String playlistLink;
    private String transcriptLink;

    private final String PLAYLIST = "https://stream.library.utoronto.ca:1935/MyMedia/play/mp4:1/";
    private final String TRANSCRIPT = "https://mymedia.library.utoronto.ca/storage/tracks/text/captions/";

    /**
     * Construct a new MyMedia Video object given a link.
     *
     * @param link - The URL for the video.
     */
    public MyMediaVideo(String link) {
        super(link);
        parseId();
        setPlaylistLink();
        setTranscriptLink();
    }

    /**
     * Parse the video ID from the link.
     */
    private void parseId() {
        this.videoId = this.link.substring(this.link.lastIndexOf("/") + 1);
    }

    /**
     * Get the playlist link for the video.
     * @return - The playlist link.
     */
    public String getPlaylistLink() {
        return this.playlistLink;
    }

    /**
     * Set the playlist link for the video.
     */
    private void setPlaylistLink() {
        this.playlistLink = PLAYLIST + this.videoId + ".mp4/playlist.m3u8";
    }

    /**
     * Get the transcript link for the video.
     * @return - The transcript link.
     */
    public String getTranscriptLink() {
        return this.transcriptLink;
    }

    /**
     * Set the transcript link for the video.
     */
    private void setTranscriptLink() {
        this.transcriptLink = TRANSCRIPT + this.videoId + "_en.vtt";
    }
}
