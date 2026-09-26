package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
	@Shadow
	@Final
	private List<WorldCreationUiState.WorldTypeEntry> normalPresetList;

	@Shadow
	private WorldCreationUiState.WorldTypeEntry worldType;

	@Shadow
	private WorldCreationContext settings;

	@Inject(method = "updatePresetLists", at = @At("TAIL"))
	private void tsh$replaceDefaultPreset(CallbackInfo ci) {
		WorldCreationUiState.WorldTypeEntry island = null;
		for (WorldCreationUiState.WorldTypeEntry entry : this.normalPresetList) {
			if (tsh$isPreset(entry, "survivalisland", "survivalisland")) {
				island = entry;
				break;
			}
		}
		if (island == null) {
			return;
		}
		this.normalPresetList.removeIf(entry -> tsh$isPreset(entry, "minecraft", "normal"));
		if (tsh$isPreset(this.worldType, "minecraft", "normal")) {
			WorldCreationUiState.WorldTypeEntry islandEntry = island;
			this.worldType = islandEntry;
			Holder<WorldPreset> holder = islandEntry.preset();
			if (holder != null) {
				this.settings = this.settings.withDimensions((frozen, dimensions) -> holder.value().createWorldDimensions());
			}
		}
	}

	@Unique
	private static boolean tsh$isPreset(WorldCreationUiState.WorldTypeEntry entry, String namespace, String path) {
		Holder<WorldPreset> holder = entry.preset();
		if (holder == null) {
			return false;
		}
		return holder.unwrapKey()
				.map(key -> key.location().getNamespace().equals(namespace) && key.location().getPath().equals(path))
				.orElse(false);
	}
}
