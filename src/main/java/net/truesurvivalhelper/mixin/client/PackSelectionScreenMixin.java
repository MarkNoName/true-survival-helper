package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin {
	@Unique
	private static final Set<String> TSH$HIDDEN_PACK_IDS = Set.of(
			"burnt:compat",
			"burnt_global/Burnt Block Mappings 1.20.1",
			"fabric",
			"bundle",
			"vanillabackport:freshly_animated",
			"vanillabackport:freshly_animated_legacy",
			"vanillabackport:backported_ost",
			"Moonlight Mods Dynamic Assets"
	);

	@ModifyVariable(method = "updateList", at = @At("HEAD"), argsOnly = true)
	private Stream<PackSelectionModel.Entry> tsh$hideModPacks(Stream<PackSelectionModel.Entry> entries) {
		return entries.filter(entry -> !TSH$HIDDEN_PACK_IDS.contains(entry.getId()));
	}

	@ModifyConstant(method = "init", constant = @Constant(intValue = 48))
	private int guibackport$lowerFooterButtons(int original) {
		return 27;
	}
}
