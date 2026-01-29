package dev.cootshk.hybric;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class HybricPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    static {
        LOGGER.atInfo().log("[Hybric] Plugin Loaded");
    }
    public HybricPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("[Hybric] Initialized...");
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("[Hybric] Starting plugin...");
        Hybric.helloFromFabric();
    }
}
