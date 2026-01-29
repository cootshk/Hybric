package dev.cootshk.hybric.mixin;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import java.util.Map;

@Mixin(PluginManager.class)
public class MixinPluginManager {
    @Shadow
    @Final
    @Nonnull
    private static HytaleLogger LOGGER;

    @Redirect(method = "loadPendingPlugin", at = @At(value = "INVOKE", target = "Ljava/util/Map;putIfAbsent(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static <K, V> V putIfAbsent(Map<K, V> instance, K key, V value) {
        if (instance.putIfAbsent(key, value) != null) {
            LOGGER.atWarning().log("Skipping plugin %s", ((PluginIdentifier) key).getName());
        }
        return null;
    }
}
