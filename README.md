# LoadrFX
*A video downloader tool with support for MyMedia and YouTube.*

## Requirements

1. [OpenJDK 17.0.1](https://openjdk.java.net/install/)
2. An active internet connection
3. A computer (Windows/macOS*)

## Download

1. Download the correct file for your operating system:
    - [Windows]()
    - macOS*

## Usage

1. Open Loadr.
2. Input your video link in the first textbox (i.e., `https://youtu.be/dQw4w9WgXcQ`).
3. Select your preferred download location with the "Save as..." button.
4. Select the source of the video from the "Provider" dropdown menu (i.e., `Youtube`).
5. If you're downloading a **MyMedia video**, select your video format from the "Format" dropdown menu (i.e., `mkv`).
6. Click the "Download" button and wait for the progress bar to stop.

## Screenshots

| Menu | Example |
|------|---------|
|  ![image](https://user-images.githubusercontent.com/20289287/146665736-c973ef2a-6a1f-4cc5-924e-4bd65610decf.png)| ![image](https://user-images.githubusercontent.com/20289287/146665768-0a560179-be56-4323-93d8-60ce35ab5f7a.png) |

In the above example, I inputted `https://youtu.be/dQw4w9WgXcQ` to be downloaded to `C:\` and saved as `NGGYU` (because it's a YouTube video, the
format is automatically `.mp4`). I selected `Youtube` as the video provider which then allows the `Download` button to be clicked and the video
to be saved.

## Roadmap

- [ ] Create macOS app
- [ ] Allow download of YouTube subtitles
- [ ] Allow custom format specification for YouTube videos

## Disclaimer

Loadr is a tool for downloading videos from MyMedia and YouTube. It uses [ffmpeg](https://ffmpeg.org/) and [yt-dlp](https://github.com/yt-dlp/yt-dlp)
which provide the basis of the download functionality.
I am not responsible for any copyright infringement. Additionally, I am not responsible for any inappropriate use of this tool,
and I am not responsible for any loss of data or other damages. What this tool does is off the input of the end-user, and not the
developer. Use at your own risk.

**Support for macOS coming soon!*
