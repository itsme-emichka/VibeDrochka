package com.vibedrochka.video;

import com.vibedrochka.VibeDrochkaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitTask;

import java.awt.image.BufferedImage;
import java.util.List;

public class VideoSession {
    
    private final VibeDrochkaPlugin plugin;
    private final VideoData videoData;
    private final List<BufferedImage> frames;
    private final List<List<ItemFrame>> frameGrid;
    private final VideoRenderer.GridVideoRenderer[][] renderers;
    private final MapView[][] mapViews;
    
    private BukkitTask playbackTask;
    private int currentFrameIndex = 0;
    private boolean isPlaying = false;
    
    public VideoSession(VibeDrochkaPlugin plugin, VideoData videoData, List<BufferedImage> frames, List<List<ItemFrame>> frameGrid) {
        this.plugin = plugin;
        this.videoData = videoData;
        this.frames = frames;
        this.frameGrid = frameGrid;
        
        int gridHeight = frameGrid.size();
        int gridWidth = frameGrid.get(0).size();
        
        this.renderers = new VideoRenderer.GridVideoRenderer[gridHeight][gridWidth];
        this.mapViews = new MapView[gridHeight][gridWidth];
        
        initializeMaps();
    }
    
    private void initializeMaps() {
        for (int y = 0; y < frameGrid.size(); y++) {
            List<ItemFrame> row = frameGrid.get(y);
            for (int x = 0; x < row.size(); x++) {
                ItemFrame frame = row.get(x);
                
                // Create a new map view
                MapView mapView = Bukkit.createMap(frame.getWorld());
                mapViews[y][x] = mapView;
                
                // Create a custom renderer for this grid position
                VideoRenderer.GridVideoRenderer renderer = new VideoRenderer.GridVideoRenderer(
                    plugin, x, y, videoData.getWidth(), videoData.getHeight()
                );
                renderers[y][x] = renderer;
                
                // Clear existing renderers and add our custom one
                mapView.getRenderers().clear();
                mapView.addRenderer(renderer);
                
                // Create map item and place it in the frame
                ItemStack mapItem = new ItemStack(org.bukkit.Material.FILLED_MAP);
                mapItem.setDurability((short) mapView.getId());
                frame.setItem(mapItem);
            }
        }
    }
    
    public void startPlayback() {
        if (isPlaying) {
            return;
        }
        
        isPlaying = true;
        currentFrameIndex = 0;
        
        // Calculate delay between frames (in ticks, 20 ticks = 1 second)
        long delayTicks = Math.max(1, 20L / videoData.getFramerate());
        
        playbackTask = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!isPlaying || frames.isEmpty()) {
                    return;
                }
                
                // Get current frame
                BufferedImage currentFrame = frames.get(currentFrameIndex);
                
                // Update all renderers with the current frame
                for (int y = 0; y < renderers.length; y++) {
                    for (int x = 0; x < renderers[y].length; x++) {
                        if (renderers[y][x] != null) {
                            renderers[y][x].updateFrame(currentFrame);
                        }
                    }
                }
                
                // Force map update for all online players
                updateMapsForPlayers();
                
                // Move to next frame
                currentFrameIndex++;
                if (currentFrameIndex >= frames.size()) {
                    // Loop the video
                    currentFrameIndex = 0;
                }
            }
        }, 0L, delayTicks);
        
        plugin.getLogger().info("Started video playback for: " + videoData.getName() + 
                               " (Framerate: " + videoData.getFramerate() + " FPS, Delay: " + delayTicks + " ticks)");
    }
    
    private void updateMapsForPlayers() {
        // Force update for all map views
        for (int y = 0; y < mapViews.length; y++) {
            for (int x = 0; x < mapViews[y].length; x++) {
                if (mapViews[y][x] != null) {
                    // The map will be automatically updated when players look at it
                    // due to our custom renderer
                }
            }
        }
    }
    
    public void pausePlayback() {
        isPlaying = false;
    }
    
    public void resumePlayback() {
        if (!isPlaying && playbackTask != null && !playbackTask.isCancelled()) {
            isPlaying = true;
        }
    }
    
    public void stop() {
        isPlaying = false;
        if (playbackTask != null) {
            playbackTask.cancel();
            playbackTask = null;
        }
        
        // Clear the item frames
        clearFrames();
        
        plugin.getLogger().info("Stopped video playback for: " + videoData.getName());
    }
    
    private void clearFrames() {
        for (List<ItemFrame> row : frameGrid) {
            for (ItemFrame frame : row) {
                if (frame != null && frame.isValid()) {
                    frame.setItem(null);
                }
            }
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public int getCurrentFrame() {
        return currentFrameIndex;
    }
    
    public int getTotalFrames() {
        return frames.size();
    }
    
    public VideoData getVideoData() {
        return videoData;
    }
}