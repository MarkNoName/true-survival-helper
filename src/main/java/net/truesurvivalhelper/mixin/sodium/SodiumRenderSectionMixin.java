package net.truesurvivalhelper.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;

import net.truesurvivalhelper.GuiBackportSodiumSectionAccess;

@Mixin(value = RenderSection.class, remap = false)
public abstract class SodiumRenderSectionMixin implements GuiBackportSodiumSectionAccess {
	@Unique
	private long guibackport$firstBuiltTime;

	@Override
	public long guibackport$getFirstBuiltTime() {
		return this.guibackport$firstBuiltTime;
	}

	@Override
	public void guibackport$setFirstBuiltTime(long time) {
		this.guibackport$firstBuiltTime = time;
	}

	@Inject(method = "setInfo", at = @At("HEAD"))
	private void guibackport$stampFirstBuilt(BuiltSectionInfo info, CallbackInfo ci) {
		if (info != null && this.guibackport$firstBuiltTime == 0L) {
			this.guibackport$firstBuiltTime = System.currentTimeMillis();
		}
	}
}
