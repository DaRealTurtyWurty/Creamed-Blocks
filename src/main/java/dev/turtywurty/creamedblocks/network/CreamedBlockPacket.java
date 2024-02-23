package dev.turtywurty.creamedblocks.network;

import dev.turtywurty.creamedblocks.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class CreamedBlockPacket {
    private final BlockPos pos;
    private final boolean removed;
    private final ResourceKey<Level> dimension;

    public CreamedBlockPacket(BlockPos pos, boolean removed, ResourceKey<Level> dimension) {
        this.pos = pos;
        this.removed = removed;
        this.dimension = dimension;
    }

    public CreamedBlockPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean(), buf.readResourceKey(Registries.DIMENSION));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.removed);
        buf.writeResourceKey(this.dimension);
    }

    public void handle(CustomPayloadEvent.Context context) {
        List<BlockPos> list = ClientEvents.CLIENT_CREAMED_BLOCKS.computeIfAbsent(dimension, key -> new ArrayList<>());
        if (this.removed) {
            list.remove(pos);
        } else {
            list.add(pos);
        }

        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client.level != null) {
                client.levelRenderer.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
            }
        });
    }
}
