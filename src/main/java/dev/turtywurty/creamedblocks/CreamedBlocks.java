package dev.turtywurty.creamedblocks;

import dev.turtywurty.creamedblocks.commands.CreamedBlocksCommand;
import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreamedBlocks implements ModInitializer {
    public static final String MODID = "creamedblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreamedBlocks.class);
    public static final ResourceLocation CREAMED_BLOCKS_PACKET_ID = new ResourceLocation(MODID, "creamed_blocks");
    public static final ResourceLocation CREAMED_BLOCKS_CLEAR_PACKET_ID = new ResourceLocation(MODID, "creamed_blocks_clear");

    @Override
    public void onInitialize() {
        LOGGER.info("Creamed Blocks has been initialized!");

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if(entity instanceof ServerPlayer player) {
                CreamedSavedData.getCached(serverLevel).syncToPlayer(player);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CreamedBlocksCommand.register(dispatcher);
        });
    }
}
