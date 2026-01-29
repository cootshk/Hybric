package dev.cootshk.hybric;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class HybricPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    static {
        LOGGER.atInfo().log("Plugin Loaded");
    }
    public HybricPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Initialized...");
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Starting plugin...");
        Hybric.helloFromFabric();
    }
}
