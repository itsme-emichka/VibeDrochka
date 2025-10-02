# VibeDrochka - Minecraft Video Plugin

Play videos directly in Minecraft using maps and item frames!

## Features

- 🎬 Play videos in Minecraft using maps on item frames
- 📱 Support for MP4, GIF, and WebP formats
- 🎮 Simple command interface: `/videdrochka <name> <url> <width> <height> [framerate]`
- 🔧 Automatic aspect ratio optimization
- ⚡ Configurable framerate (1-60 FPS, default 20 FPS)
- 📦 Single item deployment - get one item that auto-deploys to item frames
- 🔍 Smart item frame detection and validation
- 🔄 Looping video playback
- 📁 Automatic file storage in VibeDrochka folder

## Requirements

- Minecraft Server 1.21.8 (Paper recommended)
- FFmpeg installed on server machine
- Java 21+

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Install FFmpeg on your server:
   - **Linux/macOS**: `sudo apt install ffmpeg` or `brew install ffmpeg`
   - **Windows**: Download from https://ffmpeg.org/download.html
4. Restart your server
5. The VibeDrochka folder will be automatically created

## Usage

### Basic Command
```
/videdrochka <name> <url> <width> <height> [framerate]
```

### Parameters
- `name`: A unique name for your video
- `url`: Direct URL to the video file (MP4, GIF, or WebP)
- `width`: Width of the video in blocks (1-128)
- `height`: Height of the video in blocks (1-128) 
- `framerate`: Optional framerate in FPS (1-60, default: 20)

### Example
```
/videdrochka rickroll https://example.com/video.mp4 4 3 25
```

### How to Use

1. Run the command to process your video
2. You'll receive a special video item in your inventory
3. Place item frames in a grid matching your specified dimensions
4. Right-click on any item frame in the grid with the video item
5. The video will automatically deploy and start playing!

## Permissions

- `vibedrochka.use`: Allows using the videdrochka command (default: true)

## Technical Details

- Videos are automatically scaled to fit the specified dimensions without distortion
- Each map displays 128x128 pixels
- Larger videos are split across multiple maps seamlessly
- Files are stored in the `VibeDrochka` folder on your server
- Videos loop automatically when they reach the end

## Troubleshooting

### "Could not find item frame grid"
- Ensure you have enough empty item frames
- Frames must be arranged in the exact grid size you specified
- All frames must be on the same wall and facing the same direction

### "FFmpeg process failed"
- Verify FFmpeg is installed and accessible
- Check server console for detailed error messages
- Ensure the video URL is accessible and valid

### Performance Issues
- Lower the framerate parameter for better performance
- Consider smaller video dimensions for servers with limited resources

## Support

If you encounter any issues, check the server console for detailed error messages. The plugin provides comprehensive logging to help diagnose problems.

## License

This plugin is open source. Feel free to modify and distribute according to your needs!