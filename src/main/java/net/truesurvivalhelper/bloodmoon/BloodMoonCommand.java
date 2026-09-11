package net.truesurvivalhelper.bloodmoon;

import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/** Registers {@code /event blood_moon trigger}, a debug command to force-activate Blood Moon. */
public final class BloodMoonCommand {
	private BloodMoonCommand() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			dispatcher.register(Commands.literal("event")
				.then(Commands.literal("blood_moon")
					.then(Commands.literal("trigger")
						.requires(source -> source.hasPermission(2))
						.executes(BloodMoonCommand::trigger)))));
	}

	private static int trigger(CommandContext<CommandSourceStack> ctx) {
		ServerLevel overworld = ctx.getSource().getServer().overworld();
		BloodMoonManager.forceTrigger(overworld);
		ctx.getSource().sendSuccess(() -> Component.literal("Blood Moon triggered."), true);
		return 1;
	}
}
