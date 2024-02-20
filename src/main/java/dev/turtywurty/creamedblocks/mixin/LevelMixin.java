package dev.turtywurty.creamedblocks.mixin;

import dev.turtywurty.creamedblocks.CreamedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow
    public abstract BlockState getBlockState(BlockPos blockPos);

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private void creamedblocks$setBlock(BlockPos pos, BlockState state, int flags, int unknown, CallbackInfoReturnable<Boolean> callback) {
        var level = (Level) (Object) this;
        if (!(level instanceof ServerLevel serverLevel) || getBlockState(pos) == state)
            return;

        var savedData = CreamedSavedData.get(serverLevel);
        if (savedData.isCreamed(pos)) {
            savedData.removeCreamed(pos);
        }
    }
}
