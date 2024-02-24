package dev.turtywurty.creamedblocks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 1
            )
    )
    private void creamedblocks$setBlock(BlockPos pos, BlockState state, int flags, int unknown,
                                        CallbackInfoReturnable<Boolean> callback,
                                        @Local(ordinal = 1) BlockState old, @Local Block block) {
        var level = (Level) (Object) this;
        if (level.isClientSide() || old.is(block))
            return;

        var serverLevel = (ServerLevel) level;
        var savedData = CreamedSavedData.get(serverLevel);
        if (savedData.isCreamed(pos)) {
            savedData.removeCreamed(pos);
        }
    }
}