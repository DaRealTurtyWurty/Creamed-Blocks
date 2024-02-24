package dev.turtywurty.creamedblocks.mixin;

import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
    @Inject(
            method = "randomTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void creamedblocks$randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo callback) {
        var data = CreamedSavedData.get(serverLevel);
        if (data.isCreamed(blockPos) || data.isCreamed(blockPos.below())) {
            Block.dropResources(blockState, serverLevel, blockPos);
            serverLevel.removeBlock(blockPos, false);
            callback.cancel();
        }
    }
}
