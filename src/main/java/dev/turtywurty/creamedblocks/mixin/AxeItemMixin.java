package dev.turtywurty.creamedblocks.mixin;

import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(
            method = "evaluateNewBlockState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void creamedblocks$evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> callback) {
        AxeItem item = (AxeItem) (Object) this;
        if (player == null || !player.getItemInHand(InteractionHand.MAIN_HAND).is(item) || level.isClientSide() || player.isShiftKeyDown())
            return;

        var serverLevel = (ServerLevel) level;
        var data = CreamedSavedData.getCached(serverLevel);
        if (data.isCreamed(pos)) {
            data.removeCreamed(pos);
            callback.setReturnValue(Optional.of(state));
        }
    }
}