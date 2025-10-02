package com.vibedrochka.video;

public class VideoData {
    private final String name;
    private final String url;
    private final int width;
    private final int height;
    private final int framerate;
    
    public VideoData(String name, String url, int width, int height, int framerate) {
        this.name = name;
        this.url = url;
        this.width = width;
        this.height = height;
        this.framerate = framerate;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getFramerate() {
        return framerate;
    }
}