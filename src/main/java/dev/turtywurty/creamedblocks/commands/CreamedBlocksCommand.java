package dev.turtywurty.creamedblocks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.turtywurty.creamedblocks.CreamedBlocks;
import dev.turtywurty.creamedblocks.data.CreamedSavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;

public class CreamedBlocksCommand {
    private static final Component IS_CREAMED = Component.translatable("commands." + CreamedBlocks.MODID + ".is_creamed");
    private static final Component NOT_CREAMED = Component.translatable("commands." + CreamedBlocks.MODID + ".not_creamed");
    private static final Component RELOADING = Component.translatable("commands." + CreamedBlocks.MODID + ".reloading");
    private static final Component RELOAD_FINISHED = Component.translatable("commands." + CreamedBlocks.MODID + ".reload_finished");
    private static final Component RESET = Component.translatable("commands." + CreamedBlocks.MODID + ".reset");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(CreamedBlocks.MODID)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("reload")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> RELOADING, true);

                            var data = CreamedSavedData.get(context.getSource().getLevel());
                            data.reload();

                            context.getSource().sendSuccess(() -> RELOAD_FINISHED, true);

                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("query")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    var data = CreamedSavedData.get(context.getSource().getLevel());
                                    var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

                                    if (data.isCreamed(pos)) {
                                        context.getSource().sendSuccess(() -> IS_CREAMED, true);
                                    } else {
                                        context.getSource().sendSuccess(() -> NOT_CREAMED, true);
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("set")
                        .then(Commands.literal("true")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            var data = CreamedSavedData.get(context.getSource().getLevel());
                                            var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                            data.setCreamed(pos);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable("commands." + CreamedBlocks.MODID + ".set_creamed",
                                                            pos.getX(), pos.getY(), pos.getZ()), true);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("false")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            var data = CreamedSavedData.get(context.getSource().getLevel());
                                            var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                            data.removeCreamed(pos);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable("commands." + CreamedBlocks.MODID + ".remove_creamed",
                                                            pos.getX(), pos.getY(), pos.getZ()), true);
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("reset")
                        .executes(context -> {
                            var data = CreamedSavedData.get(context.getSource().getLevel());
                            data.reset();

                            context.getSource().sendSuccess(() -> RESET, true);
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}
