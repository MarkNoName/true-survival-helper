package net.truesurvivalhelper.mixin.levelz;

import net.levelz.data.LevelLists;
import net.levelz.data.LevelLoader;
import net.truesurvivalhelper.TshCustomItemRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoader.class)
public class LevelLoaderMixin {
	@Inject(method = "addAllInOneList", at = @At("TAIL"))
	private static void tsh$addDisplayOnlyList(CallbackInfo ci) {
		LevelLists.listOfAllLists.add(TshCustomItemRegistrar.getDisplayOnlyList());
	}
}
