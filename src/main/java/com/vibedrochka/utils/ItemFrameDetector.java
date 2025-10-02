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
        
        // For grid detection, the clicked frame is the TOP-RIGHT corner
        // We need to build the grid going LEFT and DOWN from there
        
        List<List<ItemFrame>> grid = new ArrayList<>();
        Location startLoc = startFrame.getLocation();
        
        System.out.println("[FrameDetector] Building " + gridWidth + "x" + gridHeight + " grid starting from TOP-RIGHT at " + 
                          startLoc.getBlockX() + "," + startLoc.getBlockY() + "," + startLoc.getBlockZ() + 
                          " facing " + facing);
        
        // Determine the direction vectors based on facing
        int leftX = 0, leftZ = 0; // Direction to go "left" in the grid (opposite of right)
        
        switch (facing) {
            case NORTH: leftX = -1; leftZ = 0; break;  // North wall: left = -X
            case SOUTH: leftX = 1; leftZ = 0; break;   // South wall: left = +X  
            case EAST: leftX = 0; leftZ = -1; break;   // East wall: left = -Z
            case WEST: leftX = 0; leftZ = 1; break;    // West wall: left = +Z
            case UP: leftX = -1; leftZ = 0; break;     // Ceiling: left = -X
            case DOWN: leftX = -1; leftZ = 0; break;   // Floor: left = -X
            default:
                System.out.println("[FrameDetector] Unsupported facing: " + facing);
                return null;
        }
        
        // Build grid row by row (top to bottom)
        for (int row = 0; row < gridHeight; row++) {
            List<ItemFrame> gridRow = new ArrayList<>();
            
            // Build each column in this row (right to left, so col=0 is rightmost)
            for (int col = 0; col < gridWidth; col++) {
                // Calculate position: start + (col * left_direction) + (row * down_direction)
                double x = startLoc.getX() + (col * leftX);
                double y = startLoc.getY() - row; // Go DOWN means -Y
                double z = startLoc.getZ() + (col * leftZ);
                
                Location targetLoc = new Location(startLoc.getWorld(), x, y, z);
                System.out.println("[FrameDetector] Looking for frame at grid[" + col + "," + row + "] = " + 
                                  targetLoc.getBlockX() + "," + targetLoc.getBlockY() + "," + targetLoc.getBlockZ());
                
                ItemFrame frame = findItemFrameAt(targetLoc, facing);
                
                if (frame == null) {
                    System.out.println("[FrameDetector] Missing frame at grid[" + col + "," + row + "]");
                    return null;
                }
                
                if (frame.getItem().getType() != org.bukkit.Material.AIR) {
                    System.out.println("[FrameDetector] Frame at grid[" + col + "," + row + "] is not empty");
                    return null;
                }
                
                System.out.println("[FrameDetector] Found frame at grid[" + col + "," + row + "] at " + 
                                  frame.getLocation().getBlockX() + "," + frame.getLocation().getBlockY() + "," + frame.getLocation().getBlockZ());
                gridRow.add(frame);
            }
            
            grid.add(gridRow);
        }
        
        System.out.println("[FrameDetector] Successfully built " + grid.size() + "x" + grid.get(0).size() + " grid");
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