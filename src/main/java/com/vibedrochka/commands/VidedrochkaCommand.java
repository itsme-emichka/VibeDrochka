package com.vibedrochka.commands;

import com.vibedrochka.VibeDrochkaPlugin;
import com.vibedrochka.video.VideoData;
import com.vibedrochka.video.VideoItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URL;

public class VidedrochkaCommand implements CommandExecutor {
    
    private final VibeDrochkaPlugin plugin;
    
    public VidedrochkaCommand(VibeDrochkaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 4 || args.length > 5) {
            player.sendMessage(ChatColor.RED + "Usage: /videdrochka <name> <url> <width> <height> [framerate]");
            return true;
        }
        
        String name = args[0];
        String urlString = args[1];
        int width, height, framerate = 20; // Default 20 FPS
        
        // Validate URL
        try {
            new URL(urlString);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid URL provided!");
            return true;
        }
        
        // Validate width and height
        try {
            width = Integer.parseInt(args[2]);
            height = Integer.parseInt(args[3]);
            
            if (width <= 0 || height <= 0) {
                throw new NumberFormatException();
            }
            
            if (width > 128 || height > 128) {
                player.sendMessage(ChatColor.RED + "Maximum size is 128x128 blocks!");
                return true;
            }
            
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Width and height must be positive integers!");
            return true;
        }
        
        // Validate framerate if provided
        if (args.length == 5) {
            try {
                framerate = Integer.parseInt(args[4]);
                if (framerate <= 0 || framerate > 60) {
                    player.sendMessage(ChatColor.RED + "Framerate must be between 1 and 60 FPS!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Framerate must be a valid integer!");
                return true;
            }
        }
        
        player.sendMessage(ChatColor.YELLOW + "Processing video: " + name);
        player.sendMessage(ChatColor.GRAY + "URL: " + urlString);
        player.sendMessage(ChatColor.GRAY + "Dimensions: " + width + "x" + height + " blocks");
        player.sendMessage(ChatColor.GRAY + "Framerate: " + framerate + " FPS");
        
        // Create video data
        VideoData videoData = new VideoData(name, urlString, width, height, framerate);
        
        // Process video asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getVideoManager().processVideo(videoData, player);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Error processing video: " + e.getMessage());
                plugin.getLogger().severe("Error processing video: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return true;
    }
}