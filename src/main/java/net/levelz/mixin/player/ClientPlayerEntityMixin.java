package net.levelz.mixin.player;

import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void initMixin(Minecraft client, ClientLevel world, ClientPacketListener networkHandler, StatsCounter stats, ClientRecipeBook recipeBook, boolean lastSneaking,
			boolean lastSprinting, CallbackInfo info) {
	}
}
