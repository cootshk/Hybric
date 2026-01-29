package dev.cootshk.hybric;

import com.hypixel.hytale.LateMain;
import com.hypixel.hytale.Main;
import com.hypixel.hytale.server.core.HytaleServer;
import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

// https://github.com/Benonardo/ExampleGameProvider/blob/master/src/main/java/io/github/pseudodistant/provider/patch/ExampleEntrypointPatch.java
public class HytaleEntrypointPatch extends GamePatch {
    @Override
    public void process(@NotNull FabricLauncher launcher, Function<String, ClassNode> classSource, Consumer<ClassNode> classEmitter) {
        // Get the game's entrypoint (set in the GameProvider) from FabricLauncher
        classEmitter.accept(patch(LateMain.class, "lateMain", "initMain", classSource));
        classEmitter.accept(patch(HytaleServer.class, "boot", "initClientServer", classSource));
    }
    private @NotNull ClassNode patch(@NotNull Class<?> cl, final String methodName, String patchName, @NotNull Function<String, ClassNode> classSource) {
        // Store the entrypoint class as a ClassNode variable so that we can more easily work with it.
        ClassNode mainClass = classSource.apply(cl.getName());
        /* Set the initializer method, this is usually not the main method,
         * it should ideally be placed as close to the game loop as possible without being inside it...*/
        MethodNode initMethod = findMethod(mainClass, (method) -> method.name.equals(methodName));

        if (initMethod == null) {
            // Do this if our method doesn't exist in the entrypoint class.
            throw new RuntimeException("Could not find init method in " + cl.getSimpleName() + "." + methodName + "!");
        }

        // Debug log stating that we found our initializer method.
        Log.debug(LogCategory.GAME_PATCH, "Found init method: %s -> %s", cl.getName(), mainClass.name);
        // Debug log stating that the method is being patched with our hooks.
        Log.debug(LogCategory.GAME_PATCH, "Patching init method %s%s", initMethod.name, initMethod.desc);

        // Assign the variable `it` to the list of instructions for our initializer method.
        ListIterator<AbstractInsnNode> it = initMethod.instructions.iterator();
        // Add our hooks to the initializer method.
        it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HytaleHooks.INTERNAL_NAME, patchName, "()V", false));
        // And finally, apply our changes to the class.
        return mainClass;
    }
}
