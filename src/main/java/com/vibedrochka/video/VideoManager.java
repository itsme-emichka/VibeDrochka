package com.vibedrochka.video;

import com.vibedrochka.VibeDrochkaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VideoManager {
    
    private final VibeDrochkaPlugin plugin;
    private final ConcurrentHashMap<String, VideoSession> activeSessions;
    private final VideoRenderer renderer;
    
    public VideoManager(VibeDrochkaPlugin plugin) {
        this.plugin = plugin;
        this.activeSessions = new ConcurrentHashMap<>();
        this.renderer = new VideoRenderer(plugin);
    }
    
    public void processVideo(VideoData videoData, Player player) {
        try {
            // Send progress message
            plugin.getServer().getScheduler().runTask(plugin, () -> 
                player.sendMessage(ChatColor.YELLOW + "Downloading video file...")
            );
            
            // Download video file
            File videoFile = downloadVideo(videoData);
            
            plugin.getServer().getScheduler().runTask(plugin, () -> 
                player.sendMessage(ChatColor.YELLOW + "Extracting video frames...")
            );
            
            // Extract frames using FFmpeg
            List<BufferedImage> frames = extractFrames(videoFile, videoData);
            
            if (frames.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> 
                    player.sendMessage(ChatColor.RED + "Failed to extract frames from video!")
                );
                return;
            }
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.sendMessage(ChatColor.GREEN + "Video processed successfully!");
                player.sendMessage(ChatColor.GREEN + "Extracted " + frames.size() + " frames");
                
                // Create and give video item to player
                VideoItem videoItem = new VideoItem(plugin, videoData, frames);
                player.getInventory().addItem(videoItem.createItem());
                
                player.sendMessage(ChatColor.AQUA + "Right-click on an item frame to deploy the video!");
                player.sendMessage(ChatColor.GRAY + "Make sure you have enough item frames arranged in a " + 
                                   videoData.getWidth() + "x" + videoData.getHeight() + " grid");
            });
            
        } catch (Exception e) {
            plugin.getServer().getScheduler().runTask(plugin, () -> 
                player.sendMessage(ChatColor.RED + "Error processing video: " + e.getMessage())
            );
            plugin.getLogger().severe("Error processing video: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private File downloadVideo(VideoData videoData) throws IOException {
        URL url = new URL(videoData.getUrl());
        String fileName = videoData.getName() + "_" + System.currentTimeMillis();
        
        // Determine file extension from URL or content type
        String urlPath = url.getPath().toLowerCase();
        if (urlPath.endsWith(".mp4")) {
            fileName += ".mp4";
        } else if (urlPath.endsWith(".gif")) {
            fileName += ".gif";
        } else if (urlPath.endsWith(".webp")) {
            fileName += ".webp";
        } else {
            fileName += ".mp4"; // Default to mp4
        }
        
        File videoFile = new File(plugin.getVideoFolder(), fileName);
        
        try (InputStream in = url.openStream()) {
            Files.copy(in, videoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        return videoFile;
    }
    
    private List<BufferedImage> extractFrames(File videoFile, VideoData videoData) throws IOException, InterruptedException {
        List<BufferedImage> frames = new ArrayList<>();
        
        // Create temporary directory for frames
        File tempDir = new File(plugin.getVideoFolder(), "temp_" + System.currentTimeMillis());
        tempDir.mkdirs();
        
        try {
            // Calculate target dimensions maintaining aspect ratio
            int[] targetDimensions = calculateOptimalDimensions(videoData.getWidth(), videoData.getHeight());
            int targetWidth = targetDimensions[0];
            int targetHeight = targetDimensions[1];
            
            // Build FFmpeg command
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", videoFile.getAbsolutePath(),
                "-vf", "scale=" + targetWidth + ":" + targetHeight + ":force_original_aspect_ratio=decrease,pad=" + 
                       targetWidth + ":" + targetHeight + ":(ow-iw)/2:(oh-ih)/2:black",
                "-r", String.valueOf(videoData.getFramerate()),
                "-f", "image2",
                tempDir.getAbsolutePath() + "/frame_%04d.png"
            );
            
            Process process = pb.start();
            
            // Log FFmpeg output for debugging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    plugin.getLogger().info("FFmpeg: " + line);
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
            
            // Load extracted frames
            File[] frameFiles = tempDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (frameFiles != null) {
                java.util.Arrays.sort(frameFiles);
                
                for (File frameFile : frameFiles) {
                    try {
                        BufferedImage image = javax.imageio.ImageIO.read(frameFile);
                        if (image != null) {
                            frames.add(image);
                        }
                    } catch (IOException e) {
                        plugin.getLogger().warning("Failed to read frame: " + frameFile.getName());
                    }
                }
            }
            
        } finally {
            // Clean up temporary files
            deleteDirectory(tempDir);
        }
        
        return frames;
    }
    
    private int[] calculateOptimalDimensions(int blockWidth, int blockHeight) {
        // Each map is 128x128 pixels
        int pixelWidth = blockWidth * 128;
        int pixelHeight = blockHeight * 128;
        return new int[]{pixelWidth, pixelHeight};
    }
    
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    public void shutdown() {
        // Stop all active video sessions
        for (VideoSession session : activeSessions.values()) {
            session.stop();
        }
        activeSessions.clear();
    }
    
    public void startVideoSession(String sessionId, VideoSession session) {
        activeSessions.put(sessionId, session);
    }
    
    public void stopVideoSession(String sessionId) {
        VideoSession session = activeSessions.remove(sessionId);
        if (session != null) {
            session.stop();
        }
    }
    
    public VideoRenderer getRenderer() {
        return renderer;
    }
    
    public ConcurrentHashMap<String, VideoSession> getActiveSessions() {
        return activeSessions;
    }
}