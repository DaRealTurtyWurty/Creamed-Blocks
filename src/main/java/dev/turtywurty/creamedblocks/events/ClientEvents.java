package dev.turtywurty.creamedblocks.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CreamedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    public static final Map<ResourceKey<Level>, List<BlockPos>> CLIENT_CREAMED_BLOCKS = new HashMap<>();

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        float partialTicks = event.getPartialTick();

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
    }

    public static void renderMagmaCream(BlockPos blockPos, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity == null)
            return;

        BlockPos abovePos = blockPos.above();

        ClientLevel level = minecraft.level;
        Vec3 above = abovePos.getCenter();
        double x = above.x - Mth.lerp(partialTicks, cameraEntity.xOld, cameraEntity.getX());
        double y = above.y - 1.5 - Mth.lerp(partialTicks, cameraEntity.yOld, cameraEntity.getY());
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
}
