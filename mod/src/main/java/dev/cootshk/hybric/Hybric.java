package dev.cootshk.hybric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;


public class Hybric implements PreLaunchEntrypoint, ModInitializer, DedicatedServerModInitializer {
    @Override
    public void onPreLaunch() {
        System.out.println("[Hybric] Initializing PreLaunch...");
    }
    @Override
    public void onInitialize() {
        System.out.println("[Hybric] Initializing Main...");
    }
    @Override
    public void onInitializeServer() {
        System.out.println("[Hybric] Initializing Server...");
    }
    public static void helloFromFabric() {
        System.out.println("[Hybric] Hello from Fabric!");
    }
}
