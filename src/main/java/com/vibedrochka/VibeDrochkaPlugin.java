package com.vibedrochka;

import com.vibedrochka.commands.VidedrochkaCommand;
import com.vibedrochka.video.VideoManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class VibeDrochkaPlugin extends JavaPlugin {
    
    private VideoManager videoManager;
    private File videoFolder;
    
    @Override
    public void onEnable() {
        // Create VibeDrochka folder
        videoFolder = new File(getDataFolder().getParentFile(), "VibeDrochka");
        if (!videoFolder.exists()) {
            if (videoFolder.mkdirs()) {
                getLogger().info("Created VibeDrochka folder for video storage");
            } else {
                getLogger().severe("Failed to create VibeDrochka folder!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        
        // Initialize video manager
        videoManager = new VideoManager(this);
        
        // Register command
        getCommand("videdrochka").setExecutor(new VidedrochkaCommand(this));
        
        getLogger().info("VibeDrochka plugin has been enabled!");
        getLogger().info("Video storage directory: " + videoFolder.getAbsolutePath());
    }
    
    @Override
    public void onDisable() {
        if (videoManager != null) {
            videoManager.shutdown();
        }
        getLogger().info("VibeDrochka plugin has been disabled!");
    }
    
    public VideoManager getVideoManager() {
        return videoManager;
    }
    
    public File getVideoFolder() {
        return videoFolder;
    }
}