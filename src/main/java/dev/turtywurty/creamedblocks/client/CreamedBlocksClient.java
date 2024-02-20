package dev.turtywurty.creamedblocks.client;

import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CreamedBlocksClient implements ClientModInitializer {
    private static final List<BlockPos> CLIENT_CREAMED_BLOCKS = new ArrayList<>();

    public static boolean isCreamed(BlockPos pos) {
        return CLIENT_CREAMED_BLOCKS.contains(pos);
    }

    public static int getCreamedTintColor() {
        return 0xFFFFA500;
    }

    @Override
    public void onInitializeClient() {
        CreamedBlocks.LOGGER.info("Creamed Blocks Client has been initialized!");

        ClientPlayNetworking.registerGlobalReceiver(CreamedBlocks.CREAMED_BLOCKS_PACKET_ID, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            boolean remove = buf.readBoolean();

            if (remove) {
                CLIENT_CREAMED_BLOCKS.remove(pos);
            } else {
                CLIENT_CREAMED_BLOCKS.add(pos);
            }

            client.execute(() -> {
                if (client.level != null) {
                    client.levelRenderer.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
                }
            });
        });
    }
}
