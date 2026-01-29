# Hybric

Load Hytale via the Fabric Mod Loader.

## Features

- [x] Fabric Mods
- [x] `preLaunch`, `main`, and `server` entrypoints
- [x] Doesn't interfere with existing plugins
- [x] Mixins
- [x] Lightweight

## Compiling

Place `HytaleServer.jar` into the `libs/` folder.

Then, run `./gradlew shadowJar`.

To build the example mod, run `./gradlew :mod:build`.

## Running

### Server:
Make a new folder for your `HytaleServer.jar` file. Inside that folder, make another folder called `assets` and extract Hytale's `Assets.zip` there. (Make sure it isn't `assets/Assets`!)

Then, place the Hybric .jar file from [releases](https://github.com/cootshk/Hybric/releases) into the same folder as the `HytaleServer.jar` file.
Lastly, simply run Hybric with `java -jar Hybric-0.1.0.jar`.

### Development:
Just run the Fabric run configuration from inside IntelliJ.
If IJ is having issues finding it, you can use the following settings:
- Java version: 21
- Module (-cp): hybric.main
- Main Class: net.fabricmc.loader.impl.launch.knot.KnotServer
- Working Directory: (project folder)/libs

The server root is the `libs/` folder, you can place both Fabric mods and Hytale plugins in the `libs/mods` folder.

## Modding

An example mod can be found in the `mod` folder.

The `main` entrypoint is run after Hytale's `LateMain.lateMain()`, and `server` is run at the end of `HytaleServer.boot()`.

If you want to load code later, check out Hytale's built in plugin system.

### Plugin Interop

Mods and plugins are loaded from the same folder, which means a .jar file can contain both a mod and a plugin, and the mod and plugin can share classes/data.

## TODO

- [x] Automatically disable Sentry
- [ ] Allow launching from a different game dir/jar
- [ ] MixinExtras
- [ ] Expose Fabric Loader to Plugins
- [ ] Fabric Plugin for other plugins to depend on
- [ ] Bundle Fabric Language Kotlin?

## Support

Join my [Discord](https://discord.gg/8hbRnkhNEa)!

## Legal

Hybric is licensed under the GPLv3.

The example mod is licensed under the CC0, which means it is free to use for any purpose, with or without credit.

Hytale &copy; 2026 Hypixel Studios.
