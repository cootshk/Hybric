package dev.cootshk.hybric.fabric

import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.fabricmc.loader.impl.FabricLoaderImpl
import net.fabricmc.loader.impl.util.ExceptionUtil
import net.fabricmc.loader.impl.util.log.Log
import net.fabricmc.loader.impl.util.log.LogCategory
import java.util.function.Consumer

object EntrypointUtils {
    @JvmStatic
    fun <T> invoke(name: String, type: Class<T>, invoker: Consumer<in T>) {
        val loader = FabricLoaderImpl.INSTANCE

        if (!loader.hasEntrypoints(name)) {
            Log.debug(LogCategory.ENTRYPOINT, "No subscribers for entrypoint '%s'", name)
        } else {
            invoke0(name, type, invoker)
        }
    }

    @JvmStatic
    private fun <T> invoke0(name: String, type: Class<T>, invoker: Consumer<in T>) {
        var exception: RuntimeException? = null
        val entrypoints: MutableCollection<EntrypointContainer<T>> =
            FabricLoaderImpl.INSTANCE.getEntrypointContainers<T>(name, type)

        Log.debug(LogCategory.ENTRYPOINT, "Iterating over entrypoint '%s'", name)

        for (container in entrypoints) {
            try {
                invoker.accept(container.getEntrypoint())
            } catch (t: Throwable) {
                exception = ExceptionUtil.gatherExceptions(
                    t,
                    exception
                ) { exc ->
                    RuntimeException(
                        String.format(
                            "Could not execute entrypoint stage '%s' due to errors, provided by '%s'!",
                            name, container.provider.metadata.id
                        ),
                        exc
                    )
                }
            }
        }

        if (exception != null) {
            throw exception
        }
    }
}