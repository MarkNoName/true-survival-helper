package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import net.truesurvivalhelper.TshPackInfo;
import net.truesurvivalhelper.TshWorldVersionAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private SelectWorldScreen screen;

	@Shadow
	@Final
	private LevelSummary summary;

	@Unique
	private boolean tsh$confirmed;

	@Shadow
	private void loadWorld() {
	}

	@Inject(method = "loadWorld", at = @At("HEAD"), cancellable = true)
	private void tsh$confirmVersionMismatch(CallbackInfo ci) {
		if (this.tsh$confirmed) {
			return;
		}
		String worldVersion = ((TshWorldVersionAccess) this.summary).tsh$worldVersion();
		if (worldVersion == null || worldVersion.equals(TshPackInfo.VERSION)) {
			return;
		}
		ci.cancel();
		this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
			if (confirmed) {
				this.tsh$confirmed = true;
				this.loadWorld();
			} else {
				this.minecraft.setScreen(this.screen);
			}
		},
				Component.translatable("tsh.selectWorld.versionMismatch.title"),
				Component.translatable("tsh.selectWorld.versionMismatch.message", worldVersion, TshPackInfo.VERSION)
		));
	}
}
