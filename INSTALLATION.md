# VibeDrochka Plugin - Installation Guide

## 🎬 Welcome to VibeDrochka!

This plugin lets you play videos directly in Minecraft using maps and item frames. It's the most epic way to watch movies in your server!

## 📋 Requirements

### Server Requirements
- **Minecraft Server**: Spigot/Paper 1.20.1 or compatible
- **Java**: Java 8 or higher
- **FFmpeg**: Required for video processing

### Client Requirements
- **Nothing!** Players just need to join your server - no client-side mods required!

## 🚀 Installation Steps

### 1. Install FFmpeg on Your Server

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install ffmpeg
```

**Linux (CentOS/RHEL):**
```bash
sudo yum install epel-release
sudo yum install ffmpeg
```

**macOS:**
```bash
brew install ffmpeg
```

**Windows:**
1. Download FFmpeg from https://ffmpeg.org/download.html
2. Extract to a folder (e.g., `C:\ffmpeg`)
3. Add the `bin` folder to your system PATH

### 2. Install the Plugin

1. Download `VibeServerPlugin-1.0.0.jar`
2. Place it in your server's `plugins` folder
3. Restart your server
4. Check console for successful loading message

### 3. Verify Installation

1. Start your server
2. Check that the `VibeDrochka` folder was created in your server directory
3. Test the command: `/videdrochka` (should show usage)

## 🎮 How to Use

### Basic Usage

1. **Create a video item:**
   ```
   /videdrochka rickroll https://example.com/rickroll.mp4 4 3 25
   ```
   - `rickroll` = name for your video
   - `https://example.com/rickroll.mp4` = direct URL to video file
   - `4` = width in blocks (item frames)
   - `3` = height in blocks (item frames)
   - `25` = framerate in FPS (optional, default 20)

2. **Set up item frames:**
   - Place item frames in a 4x3 grid on a wall
   - Make sure all frames are empty and facing the same direction
   - Frames must be exactly aligned (no gaps)

3. **Deploy the video:**
   - Take the video item you received
   - Right-click on any item frame in your grid
   - The video will automatically deploy and start playing!

### Supported Video Formats
- **MP4** - Best compatibility
- **GIF** - Great for short clips
- **WebP** - Good compression

### Tips for Best Results

🎯 **Optimal Dimensions:**
- Small videos (2x2 to 4x4) work great for most content
- Large videos (8x8+) are impressive but use more server resources
- Maximum size: 128x128 blocks

⚡ **Performance Tips:**
- Lower framerate (10-15 FPS) for servers with many players
- Higher framerate (25-30 FPS) for cinematic experiences
- Test with small videos first

🔗 **URL Requirements:**
- Must be a direct link to the video file
- URL should end with .mp4, .gif, or .webp
- File must be publicly accessible (no login required)

## 🔧 Configuration

The plugin creates these folders automatically:
- `VibeDrochka/` - Where downloaded videos are stored
- `plugins/VibeDrochka/` - Plugin configuration (auto-created)

## 🛠️ Troubleshooting

### "FFmpeg process failed"
**Problem:** Video processing fails
**Solution:** 
1. Verify FFmpeg is installed: `ffmpeg -version`
2. Check video URL is accessible
3. Try a different video format

### "Could not find item frame grid"
**Problem:** Video won't deploy to frames
**Solution:**
1. Ensure frames are arranged in exact grid pattern
2. All frames must be empty (no items)
3. All frames must face the same direction
4. No gaps between frames

### "Invalid URL provided"
**Problem:** Command rejects the URL
**Solution:**
1. Use direct links to video files
2. Ensure URL is publicly accessible
3. Try downloading the video first to test

### Performance Issues
**Problem:** Server lag during video playback
**Solution:**
1. Reduce framerate: `/videdrochka name url width height 10`
2. Use smaller dimensions
3. Limit concurrent videos playing

## 🎪 Examples

### Movie Night Setup
```
/videdrochka movienight https://example.com/movie.mp4 8 6 24
```
Creates a cinematic 8x6 block screen perfect for group viewing!

### Animated GIF Display
```
/videdrochka meme https://example.com/funny.gif 2 2 15
```
Perfect for small animated displays in your base!

### Music Video
```
/videdrochka music https://example.com/song.mp4 6 4 30
```
High framerate for smooth music video playback!

## 🔐 Permissions

- `vibedrochka.use` - Allows using the /videdrochka command (default: true)

## 📞 Support

If you encounter issues:
1. Check server console for error messages
2. Verify FFmpeg installation
3. Test with a small, simple MP4 file first
4. Check that video URL is directly accessible

## 🎉 Have Fun!

You're all set to bring movies, memes, and videos to your Minecraft server! Create epic movie theaters, funny meme walls, or educational video displays. The possibilities are endless!

**Pro tip:** Create a dedicated area with pre-built item frame grids of different sizes for easy video deployment!

---

*VibeDrochka - Making Minecraft more entertaining, one video at a time!* 🎬