package dev.cootshk.hybric.mixin;

import com.hypixel.hytale.LateMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LateMain.class)
public class MixinLateMain {
    @Inject(method = "lateMain([Ljava/lang/String;)V", at = @At("HEAD"))
    private static void lateMain(String[] args, CallbackInfo ci) {
        System.out.println("[Hybric] LateMain started.");
    }
}
