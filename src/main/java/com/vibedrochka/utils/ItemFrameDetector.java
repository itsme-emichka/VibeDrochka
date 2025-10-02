package com.vibedrochka.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class ItemFrameDetector {
    
    /**
     * Detects a grid of item frames starting from a clicked frame
     * @param startFrame The frame that was clicked
     * @param gridWidth Width of the grid in blocks
     * @param gridHeight Height of the grid in blocks
     * @return A 2D list of ItemFrames representing the grid, or null if invalid
     */
    public List<List<ItemFrame>> detectFrameGrid(ItemFrame startFrame, int gridWidth, int gridHeight) {
        // Get the facing direction of the start frame
        BlockFace facing = startFrame.getFacing();
        
        // Determine the grid directions based on the frame's facing
        GridDirections directions = getGridDirections(facing);
        
        if (directions == null) {
            return null;
        }
        
        // Build the grid
        List<List<ItemFrame>> grid = new ArrayList<>();
        
        for (int y = 0; y < gridHeight; y++) {
            List<ItemFrame> row = new ArrayList<>();
            
            for (int x = 0; x < gridWidth; x++) {
                // Calculate the position for this grid cell
                Location frameLocation = calculateFrameLocation(startFrame.getLocation(), directions, x, y);
                
                // Find the item frame at this location
                ItemFrame frame = findItemFrameAt(frameLocation, facing);
                
                if (frame == null) {
                    // Missing frame, cannot create complete grid
                    return null;
                }
                
                // Check if frame is empty (no item)
                if (frame.getItem().getType() != org.bukkit.Material.AIR) {
                    // Frame is not empty, cannot use it
                    return null;
                }
                
                row.add(frame);
            }
            
            grid.add(row);
        }
        
        return grid;
    }
    
    /**
     * Determines the right and down directions for building a grid based on the frame's facing direction
     */
    private GridDirections getGridDirections(BlockFace facing) {
        switch (facing) {
            case NORTH:
                return new GridDirections(1, 0, 0, 0, 1, 0); // Right: +X, Down: +Y
            case SOUTH:
                return new GridDirections(-1, 0, 0, 0, 1, 0); // Right: -X, Down: +Y
            case EAST:
                return new GridDirections(0, 0, 1, 0, 1, 0); // Right: +Z, Down: +Y
            case WEST:
                return new GridDirections(0, 0, -1, 0, 1, 0); // Right: -Z, Down: +Y
            case UP:
                return new GridDirections(1, 0, 0, 0, 0, -1); // Right: +X, Down: -Z
            case DOWN:
                return new GridDirections(1, 0, 0, 0, 0, 1); // Right: +X, Down: +Z
            default:
                return null;
        }
    }
    
    /**
     * Calculates the location where a frame should be in the grid
     */
    private Location calculateFrameLocation(Location startLocation, GridDirections directions, int gridX, int gridY) {
        double x = startLocation.getX() + (gridX * directions.rightX) + (gridY * directions.downX);
        double y = startLocation.getY() + (gridX * directions.rightY) + (gridY * directions.downY);
        double z = startLocation.getZ() + (gridX * directions.rightZ) + (gridY * directions.downZ);
        
        return new Location(startLocation.getWorld(), x, y, z);
    }
    
    /**
     * Finds an item frame at the specified location with the specified facing direction
     */
    private ItemFrame findItemFrameAt(Location location, BlockFace expectedFacing) {
        // Search for nearby entities within a small radius
        for (Entity entity : location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
            if (entity instanceof ItemFrame) {
                ItemFrame frame = (ItemFrame) entity;
                
                // Check if it's at the right location and facing the right direction
                if (frame.getFacing() == expectedFacing && 
                    isLocationClose(frame.getLocation(), location, 0.1)) {
                    return frame;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Checks if two locations are close to each other within a tolerance
     */
    private boolean isLocationClose(Location loc1, Location loc2, double tolerance) {
        return Math.abs(loc1.getX() - loc2.getX()) <= tolerance &&
               Math.abs(loc1.getY() - loc2.getY()) <= tolerance &&
               Math.abs(loc1.getZ() - loc2.getZ()) <= tolerance;
    }
    
    /**
     * Helper class to store grid direction vectors
     */
    private static class GridDirections {
        final double rightX, rightY, rightZ;
        final double downX, downY, downZ;
        
        GridDirections(double rightX, double rightY, double rightZ, double downX, double downY, double downZ) {
            this.rightX = rightX;
            this.rightY = rightY;
            this.rightZ = rightZ;
            this.downX = downX;
            this.downY = downY;
            this.downZ = downZ;
        }
    }
}