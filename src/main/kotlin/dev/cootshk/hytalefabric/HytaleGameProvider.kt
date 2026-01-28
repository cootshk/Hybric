package dev.cootshk.hytalefabric

import com.hypixel.hytale.common.util.java.ManifestUtil
import com.hypixel.hytale.logger.backend.HytaleLogManager
import net.fabricmc.api.EnvType
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
import java.util.*
import java.util.zip.ZipFile


// https://wiki.fabricmc.net/documentation:fabric_loader
class HytaleGameProvider : GameProvider {
    init {
        System.setProperty("java.util.logging.manager", HytaleLogManager::class.java.getName())
    }

    private lateinit var gameJar: Path
    private var jarFiles: MutableSet<String> = mutableSetOf()
    private lateinit var arguments: Arguments
    private lateinit var gameDirectory: Path
    private val gameTransformer: GameTransformer = HytaleGameTransformer
    private var entrypoint: String = "com.hypixel.hytale.Main"
    private var development: Boolean = false

    private lateinit var envType: EnvType

    override fun getGameId(): String = "hytale"
    override fun getGameName(): String = "Hytale"
    override fun getRawGameVersion(): String = ManifestUtil.getImplementationVersion()?.slice(0..9) ?: "Unknown"
    override fun getNormalizedGameVersion(): String = ManifestUtil.getImplementationVersion() ?: "Unknown"
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


        val zipFiles: MutableMap<Path?, ZipFile?> = HashMap<Path?, ZipFile?>()

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

//        if (!arguments.containsKey("gameDir")) {
//            arguments.put("gameDir", getLaunchDirectory(arguments).toAbsolutePath().normalize().toString());
//        }
        if (!arguments.containsKey("assets")) {
            arguments.put("assets", "assets")
        }

        return true

    }

    override fun initialize(launcher: FabricLauncher) {
        this.envType = launcher.environmentType
        try {
            launcher.setValidParentClassPath(
                mutableListOf(
                    Path.of(
                        this.javaClass.protectionDomain.codeSource.location.toURI()
                    )
                )
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
    private fun isHostApp(path: Path): Boolean {
        if (Files.isDirectory(path)) {
            for (string in this.jarFiles) {
                if (Files.exists(path.resolve(string))) {
                    return true
                }
            }

            return false
        }

        try {
            ZipFile(path.toFile()).use { zip ->
                for (string in this.jarFiles) {
                    if (zip.getEntry(string) != null) {
                        return true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return false
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