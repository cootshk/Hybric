package dev.cootshk.hytalefabric

import net.fabricmc.loader.impl.game.patch.GameTransformer

object HytaleGameTransformer: GameTransformer(HytaleEntrypointPatch()) {
}