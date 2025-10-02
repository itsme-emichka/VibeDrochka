package com.vibedrochka.video;

import com.vibedrochka.VibeDrochkaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoRenderer extends MapRenderer {
    
    private final VibeDrochkaPlugin plugin;
    private BufferedImage currentFrame;
    private boolean needsUpdate = true;
    
    public VideoRenderer(VibeDrochkaPlugin plugin) {
        super(false); // Not contextual
        this.plugin = plugin;
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (currentFrame != null) {
            // Draw the current frame to the map canvas
            canvas.drawImage(0, 0, currentFrame);
        }
    }
    
    public void updateFrame(BufferedImage frame) {
        this.currentFrame = frame;
        this.needsUpdate = true;
    }
    
    /**
     * Creates a custom renderer for a specific map position in the video grid
     */
    public static class GridVideoRenderer extends MapRenderer {
        private final VibeDrochkaPlugin plugin;
        private final int gridX, gridY;
        private final int totalGridWidth, totalGridHeight;
        private BufferedImage currentFrame;
        private boolean needsUpdate = true;
        
        public GridVideoRenderer(VibeDrochkaPlugin plugin, int gridX, int gridY, int totalGridWidth, int totalGridHeight) {
            super(false);
            this.plugin = plugin;
            this.gridX = gridX;
            this.gridY = gridY;
            this.totalGridWidth = totalGridWidth;
            this.totalGridHeight = totalGridHeight;
        }
        
        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            if (currentFrame != null) {
                // Calculate which part of the full image this map should display
                int mapSize = 128; // Maps are 128x128 pixels
                int sourceX = gridX * mapSize;
                int sourceY = gridY * mapSize;
                
                // Extract the portion of the frame for this map position
                BufferedImage mapPortion = extractMapPortion(currentFrame, sourceX, sourceY, mapSize, mapSize);
                
                if (mapPortion != null) {
                    canvas.drawImage(0, 0, mapPortion);
                }
            }
        }
        
        public void updateFrame(BufferedImage fullFrame) {
            this.currentFrame = fullFrame;
            this.needsUpdate = true;
        }
        
        private BufferedImage extractMapPortion(BufferedImage fullImage, int x, int y, int width, int height) {
            try {
                // Ensure we don't go out of bounds
                int actualWidth = Math.min(width, fullImage.getWidth() - x);
                int actualHeight = Math.min(height, fullImage.getHeight() - y);
                
                if (actualWidth <= 0 || actualHeight <= 0) {
                    return null;
                }
                
                // Extract the subimage
                BufferedImage portion = fullImage.getSubimage(x, y, actualWidth, actualHeight);
                
                // If the portion is smaller than the map size, create a black background
                if (actualWidth < width || actualHeight < height) {
                    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = result.createGraphics();
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, width, height);
                    g2d.drawImage(portion, 0, 0, null);
                    g2d.dispose();
                    return result;
                }
                
                return portion;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to extract map portion: " + e.getMessage());
                return null;
            }
        }
    }
}