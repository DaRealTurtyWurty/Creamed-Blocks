package dev.turtywurty.creamedblocks.mixin;

import dev.turtywurty.creamedblocks.CreamedBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(NbtIo.class)
public class NbtIoMixin {
    @Inject(
            method = "writeCompressed(Lnet/minecraft/nbt/CompoundTag;Ljava/nio/file/Path;)V",
            at = @At("HEAD")
    )
    private static void creamedblocks$writeCompressed(CompoundTag tag, Path path, CallbackInfo callback) {
        try {
            if(Files.notExists(path)) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException exception) {
            CreamedBlocks.LOGGER.error("Failed to create directories for file: " + path, exception);
        }
    }
}
