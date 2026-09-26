package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BottleItem.class)
public class GlassBottleItemMixin {
	@Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void useMixin(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, List<AreaEffectCloud> list, ItemStack itemStack) {
		ArrayList<Object> levelList = LevelLists.dragonBreathList;
		if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
			user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResultHolder.fail(itemStack));
		}
	}
}
