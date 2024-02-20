package dev.turtywurty.creamedblocks.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.creamedblocks.client.CreamedBlocksClient;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Inject(
            method = "putQuadData",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"
            )
    )
    private void creamedblocks$putQuadData(BlockAndTintGetter blockAndTintGetter,
                                           BlockState blockState,
                                           BlockPos blockPos,
                                           VertexConsumer vertexConsumer,
                                           PoseStack.Pose pose,
                                           BakedQuad bakedQuad,
                                           float f,
                                           float g,
                                           float h,
                                           float i,
                                           int j,
                                           int k,
                                           int l,
                                           int m,
                                           int n,
                                           CallbackInfo callback,
                                           @Local(ordinal = 4) LocalFloatRef red,
                                           @Local(ordinal = 5) LocalFloatRef green,
                                           @Local(ordinal = 6) LocalFloatRef blue) {
        if(CreamedBlocksClient.isCreamed(blockPos)) {
            red.set(red.get() * 1.7F);
            green.set(green.get() * 1.3F);
        }
    }
}
