# Hybric

Load Hytale via the Fabric Mod Loader.

## Compiling

Place `HytaleServer.jar` into the `libs/` folder.

## Running

Just run the Fabric run configuration from inside IntelliJ.
If IJ is having issues finding it, you can use the following settings:
- Java version: 21
- Module (-cp): hybric
- Main Class: net.fabricmc.loader.impl.launch.knot.KnotServer
- Working Directory: (project folder)/libs

The server root is the `libs/` folder, you can place both Fabric mods and Hytale plugins in the `libs/mods` folder.