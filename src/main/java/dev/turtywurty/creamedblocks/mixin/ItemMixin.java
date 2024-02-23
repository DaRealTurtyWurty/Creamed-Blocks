package dev.turtywurty.creamedblocks.mixin;

import dev.turtywurty.creamedblocks.CreamedSavedData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Rewrite this to use events
@Mixin(Item.class)
public class ItemMixin {
    @Inject(
            method = "useOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void creamedblocks$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        Item item = (Item) (Object) this;
        Player player = context.getPlayer();

        if (context.getHand() != InteractionHand.MAIN_HAND || player == null || item != Items.MAGMA_CREAM || context.getLevel().isClientSide() || player.isShiftKeyDown())
            return;

        BlockPos pos = context.getClickedPos();
        var level = (ServerLevel) context.getLevel();

        var data = CreamedSavedData.get(level);
        ItemStack stack = context.getItemInHand();
        if (!data.isCreamed(pos)) {
            data.setCreamed(pos);

            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, stack);
            if (!player.isCreative())
                stack.shrink(1);

            // add particle effects
            level.sendParticles(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
            level.playSound(player, pos, SoundEvents.FIRECHARGE_USE, player.getSoundSource(), 1.0F, 1.0F);

            callback.setReturnValue(InteractionResult.CONSUME);
        }
    }
}