package dev.turtywurty.creamedblocks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.turtywurty.creamedblocks.network.CreamedBlockPacket;
import dev.turtywurty.creamedblocks.network.PacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreamedSavedData extends SavedData {
    public static final SavedData.Factory<CreamedSavedData> FACTORY =
            new Factory<>(CreamedSavedData::new, CreamedSavedData::load, DataFixTypes.LEVEL);

    private static final Cache<ServerLevel, CreamedSavedData> RECENT_CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .build();

    private static final String KEY = CreamedBlocks.MODID + FileSystems.getDefault().getSeparator() + "creamed_blocks";

    private final List<BlockPos> creamedBlocks;
    private ServerLevel level;

    public CreamedSavedData(List<BlockPos> creamedBlocks) {
        this.creamedBlocks = creamedBlocks;
    }

    public CreamedSavedData() {
        this(new ArrayList<>());
    }

    public static CreamedSavedData load(CompoundTag compoundTag) {
        List<BlockPos> temp = new ArrayList<>();
        ListTag listTag = compoundTag.getList("CreamedBlocks", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            if (!(tag instanceof CompoundTag compound)) continue;

            temp.add(NbtUtils.readBlockPos(compound));
        }

        return new CreamedSavedData(temp);
    }

    private static CreamedSavedData get(ServerLevel level) {
        var instance = level.getDataStorage().computeIfAbsent(FACTORY, KEY);
        instance.level = level;
        return instance;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        var listTag = new ListTag();
        for (BlockPos pos : this.creamedBlocks) {
            listTag.add(NbtUtils.writeBlockPos(pos));
        }

        compoundTag.put("CreamedBlocks", listTag);
        return compoundTag;
    }

    public void setCreamed(BlockPos pos) {
        this.creamedBlocks.add(pos);
        update(pos, false);
    }

    public void removeCreamed(BlockPos pos) {
        this.creamedBlocks.remove(pos);
        update(pos, true);
    }

    private void update(BlockPos pos, boolean remove) {
        setDirty();

        if (this.level != null) {
            PacketManager.sendToAllClients(new CreamedBlockPacket(pos, remove, this.level.dimension()));
        }
    }

    public boolean isCreamed(BlockPos pos) {
        return this.creamedBlocks.contains(pos);
    }

    public void syncToPlayer(ServerPlayer player) {
        for (BlockPos pos : this.creamedBlocks) {
            PacketManager.sendToClient(new CreamedBlockPacket(pos, false, this.level.dimension()), player);
        }
    }

    public static CreamedSavedData getCached(ServerLevel level) {
        try {
            return RECENT_CACHE.get(level, () -> get(level));
        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to get saved data for level " + level, exception);
        }
    }
}