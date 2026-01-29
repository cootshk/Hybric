package dev.cootshk.hybric

import com.hypixel.hytale.logger.HytaleLogger
import dev.cootshk.hybric.fabric.EntrypointUtils
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.impl.FabricLoaderImpl
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.Mixins
import java.io.File
import java.nio.file.Paths
import java.util.function.Consumer

object HytaleHooks {
    init {
        MixinBootstrap.init()
        Mixins.addConfiguration("hybric.mixins.json")
    }
    @JvmField
    val INTERNAL_NAME: String = HytaleHooks::class.java.getName().replace('.', '/')

    private val runDir = Paths.get(".")
    private val logger = HytaleLogger.get("Hybric")

    /** This hook runs Fabric's ModInitializer.onInitialize() from where it is called.
     * It's recommended that you call them from as late into the game's execution as you can while still being before the game loop,
     * to allow ModInitializer to allow as many game alterations as possible.
     */
    @JvmStatic
    @Suppress("unused")
    fun initMain() {
        logger.atInfo().log("Loading Main Entrypoint!")
        FabricLoaderImpl.INSTANCE.prepareModInit(runDir, FabricLoaderImpl.INSTANCE.gameInstance)
        EntrypointUtils.invoke(
            "main",
            ModInitializer::class.java,
            Consumer { obj: ModInitializer -> obj.onInitialize() })
    }
    // The same, but for ClientModInitializer/DedicatedServerModInitializer
    @JvmStatic
    @Suppress("unused")
    fun initClientServer() {
        if (FabricLoaderImpl.INSTANCE.environmentType == EnvType.CLIENT) {
            logger.atInfo().log("Loading Client Entrypoint!")
            EntrypointUtils.invoke(
                "client",
                ClientModInitializer::class.java,
                Consumer { obj: ClientModInitializer -> obj.onInitializeClient() })
        } else {
            logger.atInfo().log("Loading Server Entrypoint!")
            EntrypointUtils.invoke(
                "server",
                DedicatedServerModInitializer::class.java,
                Consumer { obj: DedicatedServerModInitializer -> obj.onInitializeServer() })
        }
    }
}