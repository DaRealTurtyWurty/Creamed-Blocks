package dev.turtywurty.creamedblocks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreamedBlocksClient implements ClientModInitializer {
    private static final Map<ResourceKey<Level>, List<BlockPos>> CLIENT_CREAMED_BLOCKS = new HashMap<>();

    public static void renderMagmaCream(BlockPos blockPos, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity == null)
            return;

        BlockPos abovePos = blockPos.above();

        Level level = minecraft.level;
        Vec3 above = abovePos.getCenter();
        double x = above.x - Mth.lerp(partialTicks, cameraEntity.xOld, cameraEntity.getX());
        double y = above.y - 2 - Mth.lerp(partialTicks, cameraEntity.yOld, cameraEntity.getY());
        double z = above.z - Mth.lerp(partialTicks, cameraEntity.zOld, cameraEntity.getZ());

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(
                Items.MAGMA_CREAM.getDefaultInstance(),
                ItemDisplayContext.FIXED,
                LightTexture.pack(
                        level.getBrightness(LightLayer.BLOCK, abovePos),
                        level.getBrightness(LightLayer.SKY, abovePos)
                ),
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                level,
                0);
        poseStack.popPose();
    }

    private static boolean isHoldingMagmaCream(Player player) {
        return player.getMainHandItem().is(Items.MAGMA_CREAM) || player.getOffhandItem().is(Items.MAGMA_CREAM);
    }

    @Override
    public void onInitializeClient() {
        CreamedBlocks.LOGGER.info("Creamed Blocks Client has been initialized!");

        ClientPlayNetworking.registerGlobalReceiver(CreamedBlocks.CREAMED_BLOCKS_PACKET_ID, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            boolean remove = buf.readBoolean();
            ResourceKey<Level> dimension = buf.readResourceKey(Registries.DIMENSION);

            List<BlockPos> list = CLIENT_CREAMED_BLOCKS.computeIfAbsent(dimension, key -> new ArrayList<>());
            if (remove) {
                list.remove(pos);
            } else {
                list.add(pos);
            }

            client.execute(() -> {
                if (client.level != null) {
                    client.levelRenderer.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
                }
            });
        });

        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            ClientLevel level = context.world();
            if (level == null)
                return;

            PoseStack poseStack = context.matrixStack();
            MultiBufferSource bufferSource = context.consumers();
            float partialTicks = context.tickDelta();

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null || player.isSpectator() || !isHoldingMagmaCream(player))
                return;

            ResourceKey<Level> dimension = level.dimension();

            List<BlockPos> withinRange = CLIENT_CREAMED_BLOCKS.getOrDefault(dimension, new ArrayList<>())
                    .stream()
                    .filter(pos -> player.blockPosition().distSqr(pos) <= 64)
                    .toList();

            for (BlockPos pos : withinRange) {
                renderMagmaCream(pos, poseStack, bufferSource, partialTicks);
            }
        });
    }
}
