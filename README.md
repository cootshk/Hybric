# Hybric

Load Hytale via the Fabric Mod Loader.

## Compiling

Place `HytaleServer.jar` into the `libs/` folder.

## Running

Just run the Fabric run configuration from inside IntelliJ.
If IJ is having issues finding it, you can use the following settings:
- Java version: 21
- Module (-cp): hybric.main
- Main Class: net.fabricmc.loader.impl.launch.knot.KnotServer
- Working Directory: (project folder)/libs

The server root is the `libs/` folder, you can place both Fabric mods and Hytale plugins in the `libs/mods` folder.

## Modding

An example mod can be found in the `mod` folder.

The `main` and `server` entrypoints are run immediately before starting `com.hypixel.hytale.Main`, essentially loading them before everything else.

If you want to load code later, check out Hytale's built in plugin system.

### Plugin Interop

Mods and plugins are loaded from the same folder, which means a .jar file can contain both a mod and a plugin, and the mod and plugin can share classes/data.