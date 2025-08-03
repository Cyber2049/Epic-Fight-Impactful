package com.nameless.impactful.command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nameless.impactful.network.CPApplyBlur;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class RadialBlurCommand implements Command<CommandSourceStack>
{
    private static final RadialBlurCommand COMMAND = new RadialBlurCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("blur")
                .then(Commands.argument("time", IntegerArgumentType.integer(0, 400))
                .then(Commands.argument("amplitude", FloatArgumentType.floatArg(0, 10F))
                .executes(COMMAND)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int time = IntegerArgumentType.getInteger(context, "time");
        float amplitude = FloatArgumentType.getFloat(context, "amplitude");
        NetWorkManger.sendToPlayer(new CPApplyBlur(time, amplitude), player);
        return 1;
    }
}