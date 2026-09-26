package net.levelz.mixin.misc;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;

@Mixin(Bucketable.class)
public interface BucketableMixin {
	@Inject(method = "bucketMobPickup(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
	private static <T extends LivingEntity> void tryBucketMixin(Player player, InteractionHand hand, T entity, CallbackInfoReturnable<Optional<InteractionResult>> info) {
		ArrayList<Object> levelList = LevelLists.bucketList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(Optional.empty());
		}
	}
}
