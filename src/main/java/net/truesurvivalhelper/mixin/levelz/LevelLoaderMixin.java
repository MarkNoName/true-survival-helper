package net.truesurvivalhelper.mixin.levelz;

import net.levelz.data.LevelLists;
import net.levelz.data.LevelLoader;
import net.truesurvivalhelper.TshCustomItemRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Splices {@link TshCustomItemRegistrar}'s display-only list into
 * LevelLists.listOfAllLists every time LevelZ (re)builds it - both on its own resource
 * reload (LevelLoader#addAllInOneList itself) and on a PlayerStatsClientPacket sync
 * (which clears listOfAllLists and calls this same method to rebuild it). Adding a
 * *reference* here is enough even if the display-only list isn't populated yet at this
 * exact moment - TshCustomItemRegistrar's own reload listener fills it in afterward
 * (ordering guaranteed by its "levelz:level_loader" dependency), and
 * SkillScrollableWidget only reads listOfAllLists later, when a player actually opens
 * the screen.
 */
@Mixin(LevelLoader.class)
public class LevelLoaderMixin {
	@Inject(method = "addAllInOneList", at = @At("TAIL"))
	private static void tsh$addDisplayOnlyList(CallbackInfo ci) {
		LevelLists.listOfAllLists.add(TshCustomItemRegistrar.getDisplayOnlyList());
	}
}
