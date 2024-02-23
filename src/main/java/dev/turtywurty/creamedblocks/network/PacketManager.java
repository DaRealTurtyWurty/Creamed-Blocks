package dev.turtywurty.creamedblocks.network;

import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketManager {
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(CreamedBlocks.MODID, "main"))
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void registerPackets() {
        CHANNEL.messageBuilder(CreamedBlockPacket.class)
                .decoder(CreamedBlockPacket::new)
                .encoder(CreamedBlockPacket::encode)
                .consumerMainThread(CreamedBlockPacket::handle)
                .add();
    }

    public static void sendToClient(Object msg, ServerPlayer player) {
        CHANNEL.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToAllClients(Object msg) {
        CHANNEL.send(msg, PacketDistributor.ALL.noArg());
    }
}
