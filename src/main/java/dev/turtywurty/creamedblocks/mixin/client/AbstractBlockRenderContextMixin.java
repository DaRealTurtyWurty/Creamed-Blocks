package dev.turtywurty.creamedblocks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.turtywurty.creamedblocks.client.CreamedBlocksClient;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(AbstractBlockRenderContext.class)
public class AbstractBlockRenderContextMixin {
    @Shadow
    @Final
    protected BlockRenderInfo blockInfo;

    @Inject(
            method = "colorizeQuad",
            at = @At("HEAD"),
            remap = false
    )
    private void creamedblocks$colorizeQuad(MutableQuadViewImpl quad, int colorIndex, CallbackInfo callback) {
        if (colorIndex == -1) {
            if (CreamedBlocksClient.isCreamed(this.blockInfo.blockPos)) {
                for (int index = 0; index < 4; index++) {
                    quad.color(index, ColorHelper.multiplyColor(CreamedBlocksClient.getCreamedTintColor(), quad.color(index)));
                }
            }
        }
    }

    @ModifyExpressionValue(
            method = "colorizeQuad",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/client/indigo/renderer/render/BlockRenderInfo;blockColor(I)I"
            ),
            remap = false
    )
    private int creamedblocks$colorizeQuad(int original) {
        return CreamedBlocksClient.isCreamed(this.blockInfo.blockPos) ?
                ColorHelper.multiplyColor(0xFFFFFF00, original) :
                original;
    }
}
