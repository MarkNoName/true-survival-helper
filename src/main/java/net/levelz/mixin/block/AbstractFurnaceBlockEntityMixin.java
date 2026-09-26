package net.levelz.mixin.block;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Nullable
	private ServerPlayer serverPlayerEntity = null;

	@Mutable
	@Final
	@Shadow
	private Object2IntOpenHashMap<ResourceLocation> recipesUsed;

	@Inject(method = "awardUsedRecipesAndPopExperience", at = @At(value = "HEAD"))
	private void dropExperienceForRecipesUsedMixin(ServerPlayer player, CallbackInfo info) {
		serverPlayerEntity = player;
	}

	@Inject(method = "getRecipesToAwardAndPopExperience", at = @At(value = "TAIL"))
	private void getRecipesUsedAndDropExperienceMixin(ServerLevel world, Vec3 pos, CallbackInfoReturnable<List<Recipe<?>>> info) {
		if (ConfigInit.CONFIG.furnaceXPMultiplier > 0.0F) {
			for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
				world.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> {
					if (!recipe.getResultItem(world.registryAccess()).is(TagInit.RESTRICTED_FURNACE_EXPERIENCE_ITEMS)) {
						int i = Mth.floor((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe).getExperience());
						float f = Mth.frac((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe).getExperience());
						if (f != 0.0f && Math.random() < (double) f) {
							++i;
						}
						LevelExperienceOrbEntity.spawn(world, pos,
								(int) (i * ConfigInit.CONFIG.furnaceXPMultiplier
										* (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
												? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
												: 1.0F)));
					}
				});
			}
		}
	}
}
