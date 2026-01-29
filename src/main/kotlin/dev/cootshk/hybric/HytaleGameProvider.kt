package dev.cootshk.hybric

import com.hypixel.hytale.Main as Hytale
import com.hypixel.hytale.common.util.java.ManifestUtil
import com.hypixel.hytale.logger.backend.HytaleLogManager
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModEnvironment
import net.fabricmc.loader.impl.FabricLoaderImpl
import net.fabricmc.loader.impl.game.GameProvider
import net.fabricmc.loader.impl.game.GameProviderHelper
import net.fabricmc.loader.impl.game.patch.GameTransformer
import net.fabricmc.loader.impl.launch.FabricLauncher
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata
import net.fabricmc.loader.impl.util.Arguments
import net.fabricmc.loader.impl.util.SystemProperties
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.EnumSet
import java.util.zip.ZipFile


// https://wiki.fabricmc.net/documentation:fabric_loader
class HytaleGameProvider : GameProvider {
    init {
        // Load HytaleLogManager first.
        System.setProperty("java.util.logging.manager", HytaleLogManager::class.java.name)
    }

    private lateinit var gameJar: Path
    private lateinit var arguments: Arguments
    private lateinit var gameDirectory: Path
    private val gameTransformer: GameTransformer = GameTransformer(HytaleEntrypointPatch())
    private var entrypoint: String = Hytale::class.java.name
    private var development: Boolean = false

    private lateinit var envType: EnvType

    override fun getGameId(): String = "hytale"
    override fun getGameName(): String = "Hytale"
    override fun getRawGameVersion(): String = ManifestUtil.getImplementationVersion() ?: "Unknown"
    override fun getNormalizedGameVersion(): String = ManifestUtil.getImplementationVersion()?.slice(0..9) ?: "Unknown"
    override fun getBuiltinMods(): Collection<GameProvider.BuiltinMod> {
        // TODO: return built in plugins
        val metadata = BuiltinModMetadata.Builder(gameId, rawGameVersion)
            .setEnvironment(ModEnvironment.SERVER)
            .setName(gameName)
            .addAuthor("Hypixel", mapOf())
            .addLicense("All Rights Reserved")
            .build()
        val mod = GameProvider.BuiltinMod(
            listOf(),
            metadata
        )
        return listOf(mod)
    }
    override fun getEntrypoint(): String = entrypoint
    override fun getLaunchDirectory(): Path = if (this::gameDirectory.isInitialized) this.gameDirectory else Paths.get(".")
    override fun requiresUrlClassLoader(): Boolean = false

    override fun getBuiltinTransforms(className: String): Set<GameProvider.BuiltinTransform> {
        // This is copied from Minecraft; i don't know how well this will run on the client.
        return if (className.startsWith("com.hypixel")) {
            if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment) { // combined client+server jar, strip back down to production equivalent
                TRANSFORM_WIDENALL_STRIPENV_CLASSTWEAKS
            } else { // environment specific jar, inherently env stripped
                TRANSFORM_WIDENALL_CLASSTWEAKS
            }
        } else { // mod class TODO: exclude game libs
            TRANSFORM_STRIPENV
        }
    }

    override fun isEnabled(): Boolean = true

    override fun locateGame(launcher: FabricLauncher, args: Array<String>): Boolean {
        this.arguments = Arguments()
        this.arguments.parse(args)


        val zipFiles: MutableMap<Path, ZipFile> = HashMap()

        if (System.getProperty(SystemProperties.DEVELOPMENT) == "true") {
            development = true
        }

        try {
            var gameJarProperty = System.getProperty(SystemProperties.GAME_JAR_PATH)
            var result: GameProviderHelper.FindResult?
            if (gameJarProperty == null) {
                gameJarProperty = "./HytaleServer.jar"
            }
            val path = Paths.get(gameJarProperty)
            if (!Files.exists(path)) {
                throw RuntimeException("Game jar configured through " + SystemProperties.GAME_JAR_PATH + " system property doesn't exist")
            }

            result = GameProviderHelper.findFirst(mutableListOf(path), zipFiles, true, entrypoint)

            if (result == null) {
                return false
            }

            entrypoint = result.name
            gameJar = result.path
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        // TODO: do something about the assets folder/assets.zip file
        // (For now it just uses the assets/ folder)
        if (!arguments.containsKey("assets")) {
            arguments.put("assets", "assets")
        }
        if (!arguments.containsKey("disable-sentry") || arguments.extraArgs.contains("--disable-sentry")) {
            arguments.addExtraArg("--disable-sentry")
        }

        return true

    }

    override fun initialize(launcher: FabricLauncher) {
        this.envType = launcher.environmentType
        try {
            launcher.setValidParentClassPath(
                mutableListOf(
                    this::class,
                    FabricLoader::class,
                ).map { Path.of(it.java.protectionDomain.codeSource.location.toURI()).let { path -> println(path); path } }
            )
        } catch (e: URISyntaxException) {
            throw java.lang.RuntimeException(e)
        }
        gameTransformer.locateEntrypoints(launcher, mutableListOf(gameJar))
    }
    override fun getEntrypointTransformer(): GameTransformer = this.gameTransformer

    override fun unlockClassPath(launcher: FabricLauncher) {
        launcher.addToClassPath(this.gameJar)
    }

    override fun launch(loader: ClassLoader) {
        try {
            val main = loader.loadClass(this.getEntrypoint())
            val method = main.getMethod("main", Array<String>::class.java)

            method.invoke(null, this.arguments.toArray() as Any?)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getArguments(): Arguments {
        return this.arguments
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> {
        return this.arguments.toArray()
    }
    private val TRANSFORM_WIDENALL_STRIPENV_CLASSTWEAKS: MutableSet<GameProvider.BuiltinTransform> = EnumSet.of(
        GameProvider.BuiltinTransform.WIDEN_ALL_PACKAGE_ACCESS,
        GameProvider.BuiltinTransform.STRIP_ENVIRONMENT,
        GameProvider.BuiltinTransform.CLASS_TWEAKS
    )
    private val TRANSFORM_WIDENALL_CLASSTWEAKS: MutableSet<GameProvider.BuiltinTransform> =
        EnumSet.of(GameProvider.BuiltinTransform.WIDEN_ALL_PACKAGE_ACCESS, GameProvider.BuiltinTransform.CLASS_TWEAKS)
    private val TRANSFORM_STRIPENV: MutableSet<GameProvider.BuiltinTransform> =
        EnumSet.of(GameProvider.BuiltinTransform.STRIP_ENVIRONMENT)
}