package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

@Mixin(RandomizableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {
	@Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withLuck(F)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
	private void checkLootInteractionMixin(@Nullable Player player, CallbackInfo info) {
		((PlayerDropAccess) player).resetKilledMobStat();
	}
}
