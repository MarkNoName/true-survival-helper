package net.truesurvivalhelper.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.storage.LevelSummary;
import net.truesurvivalhelper.TshPackInfo;
import net.truesurvivalhelper.TshWorldVersion;
import net.truesurvivalhelper.TshWorldVersionAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(LevelSummary.class)
public abstract class LevelSummaryMixin implements TshWorldVersionAccess {
	@Shadow
	@Final
	private Path icon;

	@Unique
	private boolean tsh$resolved;

	@Unique
	private String tsh$version;

	@Override
	public String tsh$worldVersion() {
		this.tsh$resolve();
		return this.tsh$version;
	}

	@Unique
	private void tsh$resolve() {
		if (!this.tsh$resolved) {
			this.tsh$resolved = true;
			this.tsh$version = TshWorldVersion.read(this.icon.getParent());
		}
	}

	@Inject(method = "getWorldVersionName", at = @At("HEAD"), cancellable = true)
	private void tsh$getWorldVersionName(CallbackInfoReturnable<MutableComponent> cir) {
		this.tsh$resolve();
		if (this.tsh$version == null || this.tsh$version.isEmpty()) {
			cir.setReturnValue(Component.translatable("selectWorld.versionUnknown"));
			return;
		}
		MutableComponent component = Component.literal(this.tsh$version);
		if (!this.tsh$version.equals(TshPackInfo.VERSION)) {
			component.withStyle(ChatFormatting.ITALIC);
		}
		cir.setReturnValue(component);
	}
}
