package dev.cootshk.hybric;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class HybricPlugin extends JavaPlugin {
    static {
        System.out.println("[Hybric] Plugin Loaded");
    }
    public HybricPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        System.out.println("[Hybric] Initialized...");
    }

    @Override
    protected void start() {
        System.out.println("[Hybric] Starting plugin...");
        Hybric.helloFromFabric();
    }
}
