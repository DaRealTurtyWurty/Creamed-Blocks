package dev.turtywurty.creamedblocks.events;

import dev.turtywurty.creamedblocks.CreamedBlocks;
import dev.turtywurty.creamedblocks.commands.CreamedBlocksCommand;
import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreamedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CreamedBlocksCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.loadedFromDisk() && event.getEntity() instanceof Player player){
            var level = (ServerLevel) event.getLevel();
            CreamedSavedData.getCached(level).syncToPlayer((ServerPlayer) player);
        }
    }

    @SubscribeEvent
    public static void blockInteraction(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (level.isClientSide() || player.isShiftKeyDown() || event.getHand() != InteractionHand.MAIN_HAND || !event.getItemStack().is(Items.MAGMA_CREAM))
            return;

        BlockPos pos = event.getPos();
        var serverLevel = (ServerLevel) event.getLevel();

        var data = CreamedSavedData.getCached(serverLevel);
        ItemStack stack = event.getItemStack();
        if (!data.isCreamed(pos)) {
            data.setCreamed(pos);

            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, stack);
            if (!player.isCreative())
                stack.shrink(1);

            // add particle effects
            serverLevel.sendParticles(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
            serverLevel.playSound(player, pos, SoundEvents.FIRECHARGE_USE, player.getSoundSource(), 1.0F, 1.0F);

            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }
}
