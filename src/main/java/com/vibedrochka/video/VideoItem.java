package com.vibedrochka.video;

import com.vibedrochka.VibeDrochkaPlugin;
import com.vibedrochka.utils.ItemFrameDetector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class VideoItem implements Listener {
    
    private final VibeDrochkaPlugin plugin;
    private final VideoData videoData;
    private final List<BufferedImage> frames;
    private final NamespacedKey videoKey;
    
    public VideoItem(VibeDrochkaPlugin plugin, VideoData videoData, List<BufferedImage> frames) {
        this.plugin = plugin;
        this.videoData = videoData;
        this.frames = frames;
        this.videoKey = new NamespacedKey(plugin, "video_item");
        
        // Register this as an event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set display name and lore
            meta.setDisplayName(ChatColor.AQUA + "📽 " + videoData.getName());
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Video: " + ChatColor.WHITE + videoData.getName(),
                ChatColor.GRAY + "Dimensions: " + ChatColor.WHITE + videoData.getWidth() + "x" + videoData.getHeight() + " blocks",
                ChatColor.GRAY + "Framerate: " + ChatColor.WHITE + videoData.getFramerate() + " FPS",
                ChatColor.GRAY + "Frames: " + ChatColor.WHITE + frames.size(),
                "",
                ChatColor.YELLOW + "Right-click on an item frame to deploy!"
            ));
            
            // Store video data in persistent data container
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(videoKey, PersistentDataType.STRING, videoData.getName());
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if player is holding a video item
        if (!isVideoItem(item)) {
            return;
        }
        
        // Check if player is clicking an item frame
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        
        ItemFrame clickedFrame = (ItemFrame) event.getRightClicked();
        
        // Get video data from the item
        String videoName = getVideoNameFromItem(item);
        if (videoName == null || !videoName.equals(videoData.getName())) {
            return;
        }
        
        event.setCancelled(true);
        
        // Detect item frame grid
        ItemFrameDetector detector = new ItemFrameDetector();
        List<List<ItemFrame>> frameGrid = detector.detectFrameGrid(clickedFrame, videoData.getWidth(), videoData.getHeight());
        
        if (frameGrid == null) {
            player.sendMessage(ChatColor.RED + "Could not find a " + videoData.getWidth() + "x" + videoData.getHeight() + 
                              " grid of item frames starting from the clicked frame!");
            player.sendMessage(ChatColor.YELLOW + "Make sure you have enough empty item frames arranged in the correct pattern.");
            return;
        }
        
        // Deploy video to the item frames
        deployVideo(player, frameGrid);
        
        // Remove the video item from player's inventory
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        
        player.sendMessage(ChatColor.GREEN + "Video deployed successfully!");
        player.sendMessage(ChatColor.AQUA + "Video is now playing on the item frames.");
    }
    
    private void deployVideo(Player player, List<List<ItemFrame>> frameGrid) {
        // Create a unique session ID for this video playback
        String sessionId = videoData.getName() + "_" + System.currentTimeMillis();
        
        // Create and start video session
        VideoSession session = new VideoSession(plugin, videoData, frames, frameGrid);
        plugin.getVideoManager().startVideoSession(sessionId, session);
        
        // Start playback
        session.startPlayback();
        
        player.sendMessage(ChatColor.GREEN + "Started video session: " + sessionId);
    }
    
    private boolean isVideoItem(ItemStack item) {
        if (item == null || item.getType() != Material.FILLED_MAP) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(videoKey, PersistentDataType.STRING);
    }
    
    private String getVideoNameFromItem(ItemStack item) {
        if (!isVideoItem(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(videoKey, PersistentDataType.STRING);
    }
}