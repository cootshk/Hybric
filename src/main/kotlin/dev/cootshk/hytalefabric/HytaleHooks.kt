package dev.cootshk.hytalefabric

import dev.cootshk.hytalefabric.fabric.EntrypointUtils
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.impl.FabricLoaderImpl
import java.nio.file.Paths
import java.util.function.Consumer

object HytaleHooks {
    @JvmField
    val INTERNAL_NAME: String = HytaleHooks::class.java.getName().replace('.', '/')

    /** This hook runs Fabric's ModInitializer.onInitialize() from where it is called.
     * It's recommended that you call them from as late into the game's execution as you can while still being before the game loop,
     * to allow ModInitializer to allow as many game alterations as possible.
     */
    @JvmStatic
    @Suppress("unused")
    fun init() {
        val runDir = Paths.get(".")

        FabricLoaderImpl.INSTANCE.prepareModInit(runDir, FabricLoaderImpl.INSTANCE.gameInstance)
        EntrypointUtils.invoke(
            "main",
            ModInitializer::class.java,
            Consumer { obj: ModInitializer? -> obj!!.onInitialize() })
        if (FabricLoaderImpl.INSTANCE.environmentType == EnvType.CLIENT) {
            EntrypointUtils.invoke(
                "client",
                ClientModInitializer::class.java,
                Consumer { obj: ClientModInitializer? -> obj!!.onInitializeClient() })
        } else {
            EntrypointUtils.invoke(
                "server",
                DedicatedServerModInitializer::class.java,
                Consumer { obj: DedicatedServerModInitializer? -> obj!!.onInitializeServer() })
        }
    }
}