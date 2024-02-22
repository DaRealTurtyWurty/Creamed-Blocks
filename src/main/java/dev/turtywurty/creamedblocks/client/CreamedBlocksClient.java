package dev.turtywurty.creamedblocks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class CreamedBlocksClient implements ClientModInitializer {
    private static final List<BlockPos> CLIENT_CREAMED_BLOCKS = new ArrayList<>();

    public static boolean isCreamed(BlockPos pos) {
        return CLIENT_CREAMED_BLOCKS.contains(pos);
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

        WorldRenderEvents.LAST.register((context) -> {
            ClientLevel level = context.world();
            if(level == null)
                return;

            PoseStack poseStack = context.matrixStack();
            MultiBufferSource bufferSource = context.consumers();
            for(BlockPos pos : CLIENT_CREAMED_BLOCKS) {
                renderMagmaCream(pos, poseStack, bufferSource);
            }
        });
    }

    public static void renderMagmaCream(BlockPos blockPos, PoseStack poseStack, MultiBufferSource bufferSource) {
        if(!isCreamed(blockPos))
            return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if(player == null || player.isSpectator() || !isHoldingMagmaCream(player) || player.blockPosition().distSqr(blockPos) > 64)
            return;

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        poseStack.pushPose();
        poseStack.translate(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(
                Items.MAGMA_CREAM.getDefaultInstance(),
                ItemDisplayContext.FIXED,
                15728880,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                minecraft.level,
                0);
        poseStack.popPose();
    }

    private static boolean isHoldingMagmaCream(Player player) {
        return player.getMainHandItem().is(Items.MAGMA_CREAM) || player.getOffhandItem().is(Items.MAGMA_CREAM);
    }
}
