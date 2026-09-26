package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.init.ItemInit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

@Mixin(PotionBrewing.class)
public class BrewingRecipeRegistryMixin {
	@Inject(method = "addMix", at = @At(value = "HEAD"))
	private static void registerPotionRecipe(Potion input, Item item, Potion output, CallbackInfo info) {
		if (output != Potions.MUNDANE && output != Potions.THICK) {
			LevelLists.potionList.add(item);
			LevelLists.potionList.add(output);
		}
	}

	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private static void hasRecipeMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> info) {
		if (input.getItem() == Items.DRAGON_BREATH && ingredient.getItem() == Items.NETHER_STAR) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private static void isValidIngredientMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (stack.getItem() == Items.NETHER_STAR) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "mix", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionUtils;getPotion(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/alchemy/Potion;"), cancellable = true)
	private static void craftMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> info) {
		if (input.getItem() == Items.NETHER_STAR && ingredient.getItem() == Items.DRAGON_BREATH) {
			info.setReturnValue(new ItemStack(ItemInit.STRANGE_POTION));
		}
	}
}
