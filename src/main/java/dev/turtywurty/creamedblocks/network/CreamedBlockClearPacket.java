package dev.turtywurty.creamedblocks.network;

import dev.turtywurty.creamedblocks.events.ClientEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class CreamedBlockClearPacket {
    private final ResourceKey<Level> dimension;

    public CreamedBlockClearPacket(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public CreamedBlockClearPacket(FriendlyByteBuf buffer) {
        this(buffer.readResourceKey(Registries.DIMENSION));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceKey(this.dimension);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if(context.isServerSide())
            return;

        ClientEvents.CLIENT_CREAMED_BLOCKS.clear();
    }
}
