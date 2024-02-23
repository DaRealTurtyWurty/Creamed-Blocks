package dev.turtywurty.creamedblocks;

import dev.turtywurty.creamedblocks.network.PacketManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = CreamedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(CreamedBlocks.MODID)
public class CreamedBlocks {
    public static final String MODID = "creamedblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreamedBlocks.class);

    public CreamedBlocks() {
        LOGGER.info("Hello from CreamedBlocks!");
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketManager::registerPackets);
    }
}
