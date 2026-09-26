package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class InGameHudMixin {
	@Shadow
	@Mutable
	@Final
	private Minecraft minecraft;

	@ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920), require = 0)
	private int modifyExperienceNumberColor(int original) {
		if (((PlayerStatsManagerAccess) minecraft.player).getPlayerStatsManager().hasAvailableLevel()) {
			return 1507303;
		} else {
			return original;
		}
	}
}
