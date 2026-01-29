package dev.cootshk.hybric;

import com.hypixel.hytale.logger.HytaleLogger;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;


public class Hybric implements PreLaunchEntrypoint, ModInitializer, DedicatedServerModInitializer {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Override
    public void onPreLaunch() {
        LOGGER.atInfo().log("Hello, PreLaunch!");
    }
    @Override
    public void onInitialize() {
        LOGGER.atInfo().log("Hello, Main!");
    }
    @Override
    public void onInitializeServer() {
        LOGGER.atInfo().log("Hello, World!");
    }
    public static void helloFromFabric() {
        LOGGER.atInfo().log("Hello, from Fabric!");
    }
}
