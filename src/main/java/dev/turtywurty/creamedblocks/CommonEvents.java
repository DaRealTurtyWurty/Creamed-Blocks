package dev.turtywurty.creamedblocks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreamedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            var level = (ServerLevel) event.getLevel();
            CreamedSavedData.get(level).syncToPlayer(player);
        }
    }
}
